package com.lifestyle.profile

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lifestyle.R
import com.lifestyle.fragment.NumberPickerFragment
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.*
import com.lifestyle.util.Helpers
import com.lifestyle.util.Helpers.Companion.updateNavBar
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {

    private var isFirst: Boolean = true
    private var nameEditText: EditText? = null
    private var ageButton: TextView? = null
    private var weightButton: TextView? = null
    private var heightButton: TextView? = null
    private var sexSpinner: Spinner? = null
    private var locationTextView : TextView? = null
    private var portraitButton : ImageButton? = null
    private val NUMBER_PICKER_TAG_AGE = "profileAge"
    private val NUMBER_PICKER_TAG_HEIGHT = "profileHeight"
    private val NUMBER_PICKER_TAG_WEIGHT = "profileWeight"

    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireContext().applicationContext as LifestyleApplication).userRepository)
    }

    //create an observer that watches the LiveData<UserData> object
    private val liveDataObserver: Observer<UserData> =
        Observer { userData -> // Update the UI if this data variable changes
            // Set view contents based on the data.
            if(userData.profilePictureThumbnail != null)
                portraitButton?.setImageBitmap(userData.profilePictureThumbnail)
            // Update the age, weight, height views.
            onAgeChanged()
            onWeightChanged()
            onHeightChanged()
            // Setup spinner
            if (isFirst) {
                isFirst = false
                nameEditText?.setText(userData.name)
                sexSpinner?.setSelection(userData.sex!!.ordinal)
            }
            onLocationUpdated()
            portraitButton?.setImageBitmap(userData.profilePictureThumbnail)
            updateNavBar(requireActivity(), mUserViewModel)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mUserViewModel.data.observe(viewLifecycleOwner, liveDataObserver)

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Get the views
        nameEditText = view.findViewById(R.id.profileName)
        ageButton = view.findViewById(R.id.profileAge)
        weightButton = view.findViewById(R.id.profileWeight)
        heightButton = view.findViewById(R.id.profileHeight)
        sexSpinner = view.findViewById(R.id.profileSex)
        locationTextView = view.findViewById(R.id.profileLocation)
        portraitButton = view.findViewById(R.id.profilePortrait)

        Helpers.setUpSpinner(view!!.context, sexSpinner!!, resources.getStringArray(R.array.sex), true)


        // Set up handlers to change the data.
        nameEditText?.doOnTextChanged { text, _, _, _ ->
            text?.let{
                mUserViewModel.setName(it.toString())
            }
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_AGE, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.setAge(result)
        }
        ageButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setAge), 0, 120, mUserViewModel.data.value!!.age!!, 1, getString(R.string.years))
                .show(childFragmentManager, NUMBER_PICKER_TAG_AGE)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_HEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.setHeight(result.toFloat())
        }
        heightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setHeight), 12, 120, mUserViewModel.data.value!!.height!!.roundToInt(), 1, getString(R.string.inches))
                .show(childFragmentManager, NUMBER_PICKER_TAG_HEIGHT)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_WEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.setWeight(result.toFloat())
        }
        weightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setWeight), 10, 1000, mUserViewModel.data.value!!.weight!!.roundToInt(), 5, getString(R.string.pounds))
                .show(childFragmentManager, NUMBER_PICKER_TAG_WEIGHT)
        }
        sexSpinner?.onItemSelectedListener = sexSpinnerListener
        portraitButton?.setOnClickListener { _ ->
            //The button press should open a camera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try{
                cameraActivity.launch(cameraIntent)
            }catch(ex: ActivityNotFoundException){
                //Do error handling here
            }
        }
        locationTextView?.setOnClickListener { view ->
            activity?.let {
                mUserViewModel.update(it)
            }
        }

        // Set up viewmodel observers.
        mUserViewModel.data.observe(this.viewLifecycleOwner) { userData ->
            onLocationUpdated()
        }
        return view
    }

    private fun onAgeChanged() {
        ageButton?.text = getString(R.string.yearsQuantity, mUserViewModel.data.value!!.age)
    }

    private fun onHeightChanged() {
        heightButton?.text = getString(R.string.inchesQuantity, mUserViewModel.data.value!!.height?.roundToInt())
    }

    private fun onWeightChanged() {
        weightButton?.text = getString(R.string.poundsQuantity, mUserViewModel.data.value!!.weight?.roundToInt())
    }

    private fun onLocationUpdated() {
        locationTextView?.text = mUserViewModel.data.value!!.locationName ?: getString(R.string.none)
    }

    private var sexSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            val currentSex = mUserViewModel.data.value?.sex
            if(pos==0)
                mUserViewModel.setSex(Sex.MALE)
            else if(pos==1)
                mUserViewModel.setSex(Sex.FEMALE)
            else if(currentSex==Sex.MALE || currentSex==Sex.FEMALE)
                sexSpinner?.setSelection(currentSex.ordinal)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {

        }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            //val extras = result.data!!.extras
            //val thumbnailImage = extras!!["data"] as Bitmap?

            val thumbnailImage = result.data!!.getParcelableExtra<Bitmap>("data")
            if(thumbnailImage != null) {
                portraitButton?.setImageBitmap(thumbnailImage)
                mUserViewModel.setProfilePictureThumbnail(thumbnailImage)
                requireActivity().findViewById<ImageButton>(R.id.imageButton).setImageBitmap(thumbnailImage)
            }
        }
    }
}
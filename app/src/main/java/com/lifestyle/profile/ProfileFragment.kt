package com.lifestyle.profile

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Context
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
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.lifestyle.R
import com.lifestyle.fragment.NumberPickerFragment
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider

class ProfileFragment : Fragment() {
    private var userProvider: UserProvider? = null
    private var nameEditText: EditText? = null
    private var ageButton: TextView? = null
    private var weightButton: TextView? = null
    private var heightButton: TextView? = null
    private var sexSpinner: Spinner? = null
    private var locationTextView : TextView? = null
    private var activityLevelTextView : TextView? = null
    private var portraitButton : ImageButton? = null
    private val NUMBER_PICKER_TAG_AGE = "profileAge"
    private val NUMBER_PICKER_TAG_HEIGHT = "profileHeight"
    private val NUMBER_PICKER_TAG_WEIGHT = "profileWeight"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userProvider = try {
            context as UserProvider
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ${UserProvider::class}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        //Get the views
        nameEditText = view.findViewById<View>(R.id.profileName) as EditText
        ageButton = view.findViewById(R.id.profileAge)
        weightButton = view.findViewById(R.id.profileWeight)
        heightButton = view.findViewById(R.id.profileHeight)
        sexSpinner = view.findViewById<View>(R.id.profileSex) as Spinner
        locationTextView = view.findViewById(R.id.profileLocation)
        activityLevelTextView = view.findViewById<View>(R.id.profileActivityLevel) as TextView
        portraitButton = view.findViewById(R.id.profilePortrait)

        // Get the data that was sent in.
        val user = userProvider!!.getUser()

        // Set view contents based on the data.
        if(user.profilePictureThumbnail != null)
            portraitButton?.setImageBitmap(user.profilePictureThumbnail)
        nameEditText?.setText(user.name)
        // Update the age, weight, height views.
        onAgeChanged()
        onWeightChanged()
        onHeightChanged()
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sex,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sexSpinner?.adapter = adapter
        }
        sexSpinner?.setSelection(user.sex.ordinal)
        activityLevelTextView?.text = user.activityLevel.getLevel().name(requireContext())
        onLocationUpdated()

        // Set up handlers to change the data.
        nameEditText?.doOnTextChanged { text, _, _, _ -> user.name = text?.toString() }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_AGE, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.age = result
            onAgeChanged()
        }
        ageButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setAge), 0, 120, user.age, getString(R.string.years))
                .show(childFragmentManager, NUMBER_PICKER_TAG_AGE)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_HEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.height = result
            onHeightChanged()
        }
        heightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setHeight), 12, 120, user.height, getString(R.string.inches))
                .show(childFragmentManager, NUMBER_PICKER_TAG_HEIGHT)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_WEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.weight = result
            onWeightChanged()
        }
        weightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setWeight), 10, 1000, user.weight, getString(R.string.pounds))
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
                user?.refreshLocation(it) { newLocation ->
                    onLocationUpdated()
                }
            }
        }

        return view
    }

    private fun onAgeChanged() {
        ageButton?.text = getString(R.string.yearsQuantity, userProvider?.getUser()?.age)
    }

    private fun onHeightChanged() {
        heightButton?.text = getString(R.string.inchesQuantity, userProvider?.getUser()?.height)
    }

    private fun onWeightChanged() {
        weightButton?.text = getString(R.string.poundsQuantity, userProvider?.getUser()?.weight)
    }

    private fun onLocationUpdated() {
        locationTextView?.text = userProvider?.getUser()?.locationName ?: getString(R.string.none)
    }

    private var sexSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            val user: User? = userProvider?.getUser()
            if(pos==0)
                user?.sex = User.Sex.MALE
            else if(pos==1)
                user?.sex = User.Sex.FEMALE
            else if(user != null && user.sex != User.Sex.UNASSIGNED)
                sexSpinner?.setSelection(user.sex.ordinal)
        }

        override fun onNothingSelected(parent: AdapterView<*>) { }
    }

    private val cameraActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        if(result.resultCode == RESULT_OK) {
            //val extras = result.data!!.extras
            //val thumbnailImage = extras!!["data"] as Bitmap?

            val thumbnailImage = result.data!!.getParcelableExtra<Bitmap>("data")
            if(thumbnailImage != null) {
                portraitButton?.setImageBitmap(thumbnailImage)
                userProvider?.getUser()?.profilePictureThumbnail = thumbnailImage
                // TODO: Set userProvider.getUser()'s profile picture.
            }
        }
    }
}
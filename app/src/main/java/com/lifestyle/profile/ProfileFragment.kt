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
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.fragment.NumberPickerFragment
import com.lifestyle.bmr.Level
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider
import com.lifestyle.util.Helpers
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {
    private var userProvider: UserProvider? = null
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
        nameEditText = view.findViewById(R.id.profileName)
        ageButton = view.findViewById(R.id.profileAge)
        weightButton = view.findViewById(R.id.profileWeight)
        heightButton = view.findViewById(R.id.profileHeight)
        sexSpinner = view.findViewById(R.id.profileSex)
        locationTextView = view.findViewById(R.id.profileLocation)
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
        // Setup spinner
        Helpers.setUpSpinner(view.context, sexSpinner!!, resources.getStringArray(R.array.sex), true)
        sexSpinner?.setSelection(user.sex.ordinal)
        onLocationUpdated()
        portraitButton?.setImageBitmap(user.profilePictureThumbnail)

        // Set up handlers to change the data.
        nameEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.name = text?.toString()
                updateNavBar(user)
            }
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_AGE, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.age = result
            updateNavBar(user)
            onAgeChanged()
        }
        ageButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setAge), 0, 120, user.age, getString(R.string.years))
                .show(childFragmentManager, NUMBER_PICKER_TAG_AGE)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_HEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.height = result.toFloat()
            updateNavBar(user)
            onHeightChanged()
        }
        heightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setHeight), 12, 120, user.height.roundToInt(), getString(R.string.inches))
                .show(childFragmentManager, NUMBER_PICKER_TAG_HEIGHT)
        }
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_WEIGHT, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            userProvider?.getUser()?.weight = result.toFloat()
            updateNavBar(user)
            onWeightChanged()
        }
        weightButton?.setOnClickListener { _ ->
            NumberPickerFragment.newInstance(getString(R.string.setWeight), 10, 1000, user.weight.roundToInt(), getString(R.string.pounds))
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
        heightButton?.text = getString(R.string.inchesQuantity, userProvider?.getUser()?.height?.roundToInt())
    }

    private fun onWeightChanged() {
        weightButton?.text = getString(R.string.poundsQuantity, userProvider?.getUser()?.weight?.roundToInt())
    }

    private fun onLocationUpdated() {
        locationTextView?.text = userProvider?.getUser()?.locationName ?: getString(R.string.none)
    }
    
    private fun updateNavBar(user: User) {
        requireActivity().findViewById<TextView>(R.id.recommendedCalorieIntakeValue).text =
            getString(R.string.calPerDayShort, user.getDailyCalorieIntake().roundToInt().toString())
        requireActivity().findViewById<TextView>(R.id.ageAndSexValue).text = getString(R.string.ageAndSex, user.age.toString(),  user.sex.toString().substring(0, 1))
        val activityLevel = when(user.activityLevel.getLevel())
        {
            Level.SEDENTARY -> "Sedentary"
            Level.LIGHTLY_ACTIVE -> "Lightly Active"
            Level.ACTIVE -> "Active"
            Level.VERY_ACTIVE -> "Very Active"
        }
        requireActivity().findViewById<TextView>(R.id.nameTextValue).text = user.name
        requireActivity().findViewById<TextView>(R.id.activityLevelValue).text = activityLevel
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
            updateNavBar(user!!)
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
                requireActivity().findViewById<ImageButton>(R.id.imageButton).setImageBitmap(thumbnailImage)
                // TODO: Set userProvider.getUser()'s profile picture.
            }
        }
    }
}
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
import com.lifestyle.bmr.Level
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider
import kotlin.math.roundToInt

class ProfileFragment : Fragment() {
    private var userProvider: UserProvider? = null
    private var nameEditText: EditText? = null
    private var ageEditText: EditText? = null
    private var weightEditText: EditText? = null
    private var heightEditText: EditText? = null
    private var sexSpinner: Spinner? = null
    private var activityLevelTextView : TextView? = null
    private var portraitButton : ImageButton? = null

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
        ageEditText = view.findViewById<View>(R.id.profileAge) as EditText
        weightEditText = view.findViewById<View>(R.id.profileWeight) as EditText
        heightEditText = view.findViewById<View>(R.id.profileHeight) as EditText
        sexSpinner = view.findViewById<View>(R.id.profileSex) as Spinner
        activityLevelTextView = view.findViewById<View>(R.id.profileActivityLevel) as TextView
        portraitButton = view.findViewById(R.id.profilePortrait)

        // Get the data that was sent in.
        val user = userProvider!!.getUser()

        // Set view contents based on the data.
        nameEditText?.setText(user.name)
        ageEditText?.setText(user.age.toString())
        weightEditText?.setText(user.weight.toString())
        heightEditText?.setText(user.height.toString())
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

        // Set up handlers to change the data.
        nameEditText?.doOnTextChanged { text, _, _, _ ->
            run {
                user.name = text?.toString()
                updateNavBar(user)
            }
        }
        ageEditText?.doOnTextChanged { text, _, _, _ ->
            if(text != null)
                user.age = parseOrResetText(ageEditText!!, text, user.age)
                updateNavBar(user)

        }
        heightEditText?.doOnTextChanged { text, _, _, _ ->
            if(text != null)
                user.height = parseOrResetText(heightEditText!!, text, user.height)
                updateNavBar(user)
        }
        weightEditText?.doOnTextChanged { text, _, _, _ ->
            if (text != null)
                user.weight = parseOrResetText(weightEditText!!, text, user.weight)
                updateNavBar(user)
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

        return view
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
        activityLevelTextView?.text = activityLevel
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
            portraitButton?.setImageBitmap(thumbnailImage)
            requireActivity().findViewById<ImageButton>(R.id.imageButton).setImageBitmap(thumbnailImage)
            // TODO: Set userProvider.getUser()'s profile picture.
        }
    }

    companion object {
        private fun parseOrResetText(editText: EditText, text: CharSequence?, currentValue: Int): Int {
            if (text.toString() == "") {
                editText.setText("0")
                return 0;
            }
            if(text != null) {
                try {
                    return Integer.parseInt(text.toString())
                } catch(_: NumberFormatException) { }
            }
            editText.setText(currentValue)
            return currentValue
        }
    }
}
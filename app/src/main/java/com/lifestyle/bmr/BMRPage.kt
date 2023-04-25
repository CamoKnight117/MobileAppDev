package com.lifestyle.bmr

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.lifestyle.R
import com.lifestyle.fragment.NumberPickerFragment
import com.lifestyle.main.LifestyleApplication
import com.lifestyle.user.UserData
import com.lifestyle.user.UserViewModel
import com.lifestyle.util.Helpers
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val NUMBER_PICKER_TAG_CALPERHOUR = "CalPerHour"
private const val NUMBER_PICKER_TAG_WORKOUTSPERWEEK = "WorkoutsPerWeek"
private const val NUMBER_PICKER_TAG_WORKOUTLENGTH = "WorkoutLength"


/**
 * A simple [Fragment] subclass.
 * Use the [bmr_page_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BMRPage : Fragment() {
    private val mUserViewModel: UserViewModel by viewModels {
        UserViewModel.UserViewModelFactory((requireContext().applicationContext as LifestyleApplication).userRepository)
    }

    private val liveDataObserver: Observer<UserData> =
        Observer { userData ->

        }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mUserViewModel.data.observe(viewLifecycleOwner, liveDataObserver)

        val view: View = inflater.inflate(R.layout.fragment_bmr_page, container, false)

        //Setup spinner
        Helpers.setUpSpinner(view.context, view.findViewById(R.id.intensitySpinner), resources.getStringArray(R.array.spinnerItems), false)

        //Setup other listeners
        view.findViewById<TextView>(R.id.caloriesPerHourValue)?.setOnClickListener(caloriesPerHourClickListener)
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_CALPERHOUR, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.data.value!!.activityLevel?.caloriesPerHour = result
            updateBMRPage(view, true, false)
        }
        view.findViewById<TextView>(R.id.workoutsPerWeekValue)?.setOnClickListener(workoutsPerWeekClickListener)
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_WORKOUTSPERWEEK, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.data.value!!.activityLevel?.workoutsPerWeek = result
            updateBMRPage(view, true, false)
        }
        view.findViewById<TextView>(R.id.workoutLengthValue)?.setOnClickListener(workoutLengthClickListener)
        childFragmentManager.setFragmentResultListener(NUMBER_PICKER_TAG_WORKOUTLENGTH, this) { key, bundle ->
            val result = NumberPickerFragment.getResultNumber(bundle)
            mUserViewModel.data.value!!.activityLevel?.averageWorkoutLength = result
            updateBMRPage(view, true, false)
        }

        //Setup page
        updateBMRPage(view, true, true)

        return view
    }

    private fun addListeners(view: View) {
        view.findViewById<TextView>(R.id.caloriesPerHourValue).addTextChangedListener(textChangeListener)
        view.findViewById<TextView>(R.id.workoutsPerWeekValue).addTextChangedListener(textChangeListener)
        view.findViewById<TextView>(R.id.workoutLengthValue).addTextChangedListener(textChangeListener)
        view.findViewById<Spinner>(R.id.intensitySpinner).onItemSelectedListener = itemSelectListener
    }

    private fun removeListeners(view: View) {
        view.findViewById<TextView>(R.id.caloriesPerHourValue).removeTextChangedListener(textChangeListener)
        view.findViewById<TextView>(R.id.workoutsPerWeekValue).removeTextChangedListener(textChangeListener)
        view.findViewById<TextView>(R.id.workoutLengthValue).removeTextChangedListener(textChangeListener)
        view.findViewById<Spinner>(R.id.intensitySpinner).onItemSelectedListener = null
    }

    private fun updateBMRPage(view: View, setSpinner: Boolean, isFirst: Boolean) {
        //Remove listeners before updating
        removeListeners(view)

        val user = mUserViewModel.data.value
        val intensitySpinner = view.findViewById<Spinner>(R.id.intensitySpinner)

        //Calories per hour
        //If spinner is updated, first update calories per hour accordingly
        if (!setSpinner) {
            user?.activityLevel!!.caloriesPerHour = if (intensitySpinner.selectedItemPosition == 0) {
                150
            } else if (intensitySpinner.selectedItemPosition == 1) {
                450
            } else {
                750
            }
        }
        val calPerHourEditText = view.findViewById<TextView>(R.id.caloriesPerHourValue)
        calPerHourEditText.setText(user?.activityLevel!!.caloriesPerHour.toString())

        //Workouts per week
        val workoutsPerWeekEditText = view.findViewById<TextView>(R.id.workoutsPerWeekValue)
        workoutsPerWeekEditText.setText(user?.activityLevel!!.workoutsPerWeek.toString())

        //Workout length
        val workoutLengthEditText = view.findViewById<TextView>(R.id.workoutLengthValue)
        workoutLengthEditText.setText(user?.activityLevel!!.averageWorkoutLength.toString())

        //Intensity spinner
        if (setSpinner) {
            if (user?.activityLevel!!.caloriesPerHour < 300) {
                intensitySpinner.setSelection(0, false)
            } else if (user?.activityLevel!!.caloriesPerHour < 600) {
                intensitySpinner.setSelection(1, false)
            } else {
                intensitySpinner.setSelection(2, false)
            }
        }

        //Calories burned per workout
        val caloriesBurnedPerWorkoutText = view.findViewById<TextView>(R.id.caloriesBurnedPerWorkoutValue)
        caloriesBurnedPerWorkoutText.text = user?.activityLevel!!.getCaloriesBurnedPerWorkout().roundToInt().toString()

        //Workout calories per week
        val workoutCaloriesPerWeekText = view.findViewById<TextView>(R.id.workoutCaloriesPerWeekValue)
        workoutCaloriesPerWeekText.text = user?.activityLevel!!.workoutCaloriesPerWeek().roundToInt().toString()

        //Daily calorie needs
        val dailyCalorieNeedsText = view.findViewById<TextView>(R.id.dailyCalorieNeedsValue)
        dailyCalorieNeedsText.text = mUserViewModel.getDailyCalorieIntake().toString()

        //BMR
        val bmrText = view.findViewById<TextView>(R.id.bmrValue)
        bmrText.text = mUserViewModel.calculateBMR().toString()

        //Sedentary
        val sedentaryRowText = view.findViewById<TextView>(R.id.sedentaryRowValue)
        sedentaryRowText.text = mUserViewModel.calculateSedentaryCalNeed().toString()

        //Lightly active
        val lightlyActiveRowText = view.findViewById<TextView>(R.id.lightlyActiveRowValue)
        lightlyActiveRowText.text = mUserViewModel.calculateLightlyActiveCalNeed().toString()

        //Active
        val activeRowText = view.findViewById<TextView>(R.id.activeRowValue)
        activeRowText.text = mUserViewModel.calculateActiveCalNeed().toString()

        //Very Active
        val veryActiveRowText = view.findViewById<TextView>(R.id.veryActiveRowValue)
        veryActiveRowText.text = mUserViewModel.calculateVeryActiveCalNeed().toString()

        //Activity level
        val activityLevelText = view.findViewById<TextView>(R.id.LocationTextView)
        activityLevelText.text = when(user?.activityLevel!!.getLevel())
        {
            Level.SEDENTARY -> "Sedentary"
            Level.LIGHTLY_ACTIVE -> "Lightly Active"
            Level.ACTIVE -> "Active"
            Level.VERY_ACTIVE -> "Very Active"
        }

        //Top navbar
        Helpers.updateNavBar(requireActivity(), mUserViewModel)

        //Add back listeners
        addListeners(view)
    }

    private val caloriesPerHourClickListener = OnClickListener {
        NumberPickerFragment.newInstance(getString(R.string.SetCaloriesPerHour), 0, 1000, mUserViewModel.data.value!!.activityLevel!!.caloriesPerHour, 10, getString(R.string.cals))
            .show(childFragmentManager, NUMBER_PICKER_TAG_CALPERHOUR)
    }

    private val workoutsPerWeekClickListener = OnClickListener {
        NumberPickerFragment.newInstance(getString(R.string.SetWorkoutsPerWeek), 0, 50, mUserViewModel.data.value!!.activityLevel!!.workoutsPerWeek, 1, getString(R.string.workouts))
            .show(childFragmentManager, NUMBER_PICKER_TAG_WORKOUTSPERWEEK)
    }

    private val workoutLengthClickListener = OnClickListener {
        NumberPickerFragment.newInstance(getString(R.string.SetWorkoutLength), 0, 1440, mUserViewModel.data.value!!.activityLevel!!.averageWorkoutLength, 5, getString(R.string.minutes))
            .show(childFragmentManager, NUMBER_PICKER_TAG_WORKOUTLENGTH)
    }

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (view != null) {
                updateBMRPage(view!!, true, false)
            }
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    }

    private val itemSelectListener = object : OnItemSelectedListener
    {
        override fun onItemSelected(parent: AdapterView<*>?, spinnerView: View?, position: Int, id: Long) {
            if (view != null) {
                updateBMRPage(view!!, false, false)
            }
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment bmr_page_fragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BMRPage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


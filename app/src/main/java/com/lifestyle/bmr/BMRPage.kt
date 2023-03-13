package com.lifestyle.bmr

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.lifestyle.R
import com.lifestyle.main.UserProvider
import com.lifestyle.util.Helpers
import kotlin.math.roundToInt

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [bmr_page_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BMRPage : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var userProvider: UserProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        userProvider = try {
            context as UserProvider
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ${UserProvider::class}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_bmr_page, container, false)

        //Setup spinner
        Helpers.setUpSpinner(view.context, view.findViewById(R.id.intensitySpinner), resources.getStringArray(R.array.spinnerItems), false)

        //Setup page
        updateBMRPage(view, true, true)

        return view
    }

    private fun addListeners(view: View) {
        view.findViewById<EditText>(R.id.caloriesPerHourValue).addTextChangedListener(textChangeListener)
        view.findViewById<EditText>(R.id.workoutsPerWeekValue).addTextChangedListener(textChangeListener)
        view.findViewById<EditText>(R.id.workoutLengthValue).addTextChangedListener(textChangeListener)
        view.findViewById<Spinner>(R.id.intensitySpinner).onItemSelectedListener = itemSelectListener
    }

    private fun removeListeners(view: View) {
        view.findViewById<EditText>(R.id.caloriesPerHourValue).removeTextChangedListener(textChangeListener)
        view.findViewById<EditText>(R.id.workoutsPerWeekValue).removeTextChangedListener(textChangeListener)
        view.findViewById<EditText>(R.id.workoutLengthValue).removeTextChangedListener(textChangeListener)
        view.findViewById<Spinner>(R.id.intensitySpinner).onItemSelectedListener = null
    }

    private fun updateBMRPage(view: View, setSpinner: Boolean, isFirst: Boolean) {
        //Remove listeners before updating
        removeListeners(view)

        val user = userProvider!!.getUser()
        val intensitySpinner = view.findViewById<Spinner>(R.id.intensitySpinner)

        //Calories per hour
        val calPerHourEditText = view.findViewById<EditText>(R.id.caloriesPerHourValue)
        val capPerHourText = calPerHourEditText.text.toString()
        if (capPerHourText == "") {
            user.activityLevel.caloriesPerHour = 0
            calPerHourEditText.setText("0")
        } else {
            //If spinner is updated, first update calories per hour accordingly
            if (!setSpinner) {
                user.activityLevel.caloriesPerHour = if (intensitySpinner.selectedItemPosition == 0) {
                    150
                } else if (intensitySpinner.selectedItemPosition == 1) {
                    450
                } else {
                    750
                }
                calPerHourEditText.setText(user.activityLevel.caloriesPerHour.toString())
            } else if (!isFirst) {
                user.activityLevel.caloriesPerHour = capPerHourText.toInt()
                if (!calPerHourEditText.hasFocus()) {
                    calPerHourEditText.setText(capPerHourText)
                }
            } else {
                calPerHourEditText.setText(user.activityLevel.caloriesPerHour.toString())
            }
        }

        //Workouts per week
        val workoutsPerWeekEditText = view.findViewById<EditText>(R.id.workoutsPerWeekValue)
        val workoutsPerWeekText = workoutsPerWeekEditText.text.toString()
        if (workoutsPerWeekText == "") {
            user.activityLevel.workoutsPerWeek = 0
            workoutsPerWeekEditText.setText("0")
        } else if (!isFirst) {
            user.activityLevel.workoutsPerWeek = workoutsPerWeekText.toInt()
            if (!workoutsPerWeekEditText.hasFocus()) {
                workoutsPerWeekEditText.setText(workoutsPerWeekText)
            }
        } else {
            workoutsPerWeekEditText.setText(user.activityLevel.workoutsPerWeek.toString())
        }

        //Workout length
        val workoutLengthEditText = view.findViewById<EditText>(R.id.workoutLengthValue)
        val workoutLengthText = workoutLengthEditText.text.toString()
        if (workoutLengthText == "" || workoutLengthText == ".") {
            user.activityLevel.averageWorkoutLength = 0f
            workoutLengthEditText.setText("0.0")
        } else if (!isFirst) {
            user.activityLevel.averageWorkoutLength = workoutLengthText.toFloat()
            if (!workoutLengthEditText.hasFocus()) {
                workoutLengthEditText.setText(workoutLengthText)
            }
        } else {
            workoutLengthEditText.setText(user.activityLevel.averageWorkoutLength.toString())
        }

        //Intensity spinner
        if (setSpinner) {
            if (user.activityLevel.caloriesPerHour < 300) {
                intensitySpinner.setSelection(0, false)
            } else if (user.activityLevel.caloriesPerHour < 600) {
                intensitySpinner.setSelection(1, false)
            } else {
                intensitySpinner.setSelection(2, false)
            }
        }

        //Calories burned per workout
        val caloriesBurnedPerWorkoutText = view.findViewById<TextView>(R.id.caloriesBurnedPerWorkoutValue)
        caloriesBurnedPerWorkoutText.text = user.activityLevel.getCaloriesBurnedPerWorkout().toString()

        //Workout calories per week
        val workoutCaloriesPerWeekText = view.findViewById<TextView>(R.id.workoutCaloriesPerWeekValue)
        workoutCaloriesPerWeekText.text = user.activityLevel.workoutCaloriesPerWeek().toString()

        //Daily calorie needs
        val dailyCalorieNeedsText = view.findViewById<TextView>(R.id.dailyCalorieNeedsValue)
        dailyCalorieNeedsText.text = user.getDailyCalorieIntake().toString()

        //BMR
        val bmrText = view.findViewById<TextView>(R.id.bmrValue)
        bmrText.text = user.calculateBMR().toString()

        //Sedentary
        val sedentaryRowText = view.findViewById<TextView>(R.id.sedentaryRowValue)
        sedentaryRowText.text = user.calculateSedentaryCalNeed().toString()

        //Lightly active
        val lightlyActiveRowText = view.findViewById<TextView>(R.id.lightlyActiveRowValue)
        lightlyActiveRowText.text = user.calculateLightlyActiveCalNeed().toString()

        //Active
        val activeRowText = view.findViewById<TextView>(R.id.activeRowValue)
        activeRowText.text = user.calculateActiveCalNeed().toString()

        //Very Active
        val veryActiveRowText = view.findViewById<TextView>(R.id.veryActiveRowValue)
        veryActiveRowText.text = user.calculateVeryActiveCalNeed().toString()

        //Activity level
        val activityLevelText = view.findViewById<TextView>(R.id.LocationTextView)
        activityLevelText.text = when(user.activityLevel.getLevel())
        {
            Level.SEDENTARY -> "Sedentary"
            Level.LIGHTLY_ACTIVE -> "Lightly Active"
            Level.ACTIVE -> "Active"
            Level.VERY_ACTIVE -> "Very Active"
        }

        //Top navbar
        Helpers.updateNavBar(requireActivity(), user)

        //Add back listeners
        addListeners(view)
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


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

var isUpdatingPage = true
var justSetSpinner = false


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

    private fun spinnerUpdate(view: View)
    {
        if(!justSetSpinner) {
            val intensitySpinner = view.findViewById<Spinner>(R.id.intensitySpinner)

            userProvider!!.getUser().activityLevel.caloriesPerHour =
                (if (intensitySpinner.selectedItemPosition == 0) {
                    150
                } else if (intensitySpinner.selectedItemPosition == 1) {
                    450
                } else {
                    750
                })
            justSetSpinner = true
            updateBMRpage(view)
        }
        else
        {
            justSetSpinner = false
        }
    }
    private fun updateBMRValues(view: View)
    {
        val calPerHourEditText = view.findViewById<EditText>(R.id.caloriesPerHourValue)
        val calPerHour = if(calPerHourEditText.length() > 0) {
            calPerHourEditText.text.toString().toInt()
        } else {
            0
        }

        val workoutsPerWeekEditText = view.findViewById<EditText>(R.id.workoutsPerWeekValue)
        val workoutsPerWeek = if(workoutsPerWeekEditText.length() > 0) {
            workoutsPerWeekEditText.text.toString().toInt()
        } else {
            0
        }

        val workoutLengthEditText = view.findViewById<EditText>(R.id.workoutLengthValue)
        val workoutLength = if(workoutLengthEditText.length() > 0) {
            workoutLengthEditText.text.toString().toFloat()
        } else {
            0f
        }

        val user = userProvider!!.getUser()

        user.activityLevel.caloriesPerHour = calPerHour
        user.activityLevel.workoutsPerWeek = workoutsPerWeek
        user.activityLevel.averageWorkoutLength = workoutLength

        updateBMRpage(view)
    }

    private fun updateBMRpage(view: View){
        val calPerHourEditText = view.findViewById<EditText>(R.id.caloriesPerHourValue)
        val workoutsPerWeekEditText = view.findViewById<EditText>(R.id.workoutsPerWeekValue)
        val workoutLengthEditText = view.findViewById<EditText>(R.id.workoutLengthValue)

        val user = userProvider!!.getUser()

        //Only updates values if they need to be updated, otherwise infinite looping will occur
        val calPerHourText = calPerHourEditText.text.toString()
        if (calPerHourText == "" || calPerHourEditText.text.toString().toInt() != user.activityLevel.caloriesPerHour) {
            calPerHourEditText.setText(
                if (calPerHourText == "") {
                    "0"
                } else {
                    user.activityLevel.caloriesPerHour.toString()
                })
        }
        val workoutsPerWeekText = workoutsPerWeekEditText.text.toString()
        if (workoutsPerWeekText == "" || workoutsPerWeekText.toInt() != user.activityLevel.workoutsPerWeek) {
            workoutsPerWeekEditText.setText(
                if (workoutsPerWeekText == "") {
                    "0"
                } else {
                    user.activityLevel.workoutsPerWeek.toString()
                })
        }
        val workoutLengthText = workoutLengthEditText.text.toString()
        if (workoutLengthText == "" || workoutLengthText.toFloat() != user.activityLevel.averageWorkoutLength) {
            workoutLengthEditText.setText(
                if (workoutLengthText == "") {
                    "0.0"
                } else {
                    user.activityLevel.averageWorkoutLength.toString()
                })
        }

        val caloriesBurnedPerWorkoutText = view.findViewById<TextView>(R.id.caloriesBurnedPerWorkoutValue)
        val workoutCaloriesPerWeekText = view.findViewById<TextView>(R.id.workoutCaloriesPerWeekValue)
        val intensitySpinner = view.findViewById<Spinner>(R.id.intensitySpinner)

        caloriesBurnedPerWorkoutText.text = user.activityLevel.getCaloriesBurnedPerWorkout().toString()
        workoutCaloriesPerWeekText.text = user.activityLevel.workoutCaloriesPerWeek().toString()
        println(isUpdatingPage)
        if(!justSetSpinner) {
            justSetSpinner = true
            if (user.activityLevel.caloriesPerHour < 300) {
                intensitySpinner.setSelection(0)
            } else if (user.activityLevel.caloriesPerHour < 600) {
                intensitySpinner.setSelection(1)
            } else {
                intensitySpinner.setSelection(2)
            }
        }
        else {
            justSetSpinner = false
        }

        val activityLevelText = view.findViewById<TextView>(R.id.LocationTextView)
        val activityLevelValue = requireActivity().findViewById<TextView>(R.id.activityLevelValue)
        val dailyCalorieNeedsText = view.findViewById<TextView>(R.id.dailyCalorieNeedsValue)
        val recommendedCalorieIntakeValue = requireActivity().findViewById<TextView>(R.id.recommendedCalorieIntakeValue)
        val bmrText = view.findViewById<TextView>(R.id.bmrValue)
        val sedentaryRowText = view.findViewById<TextView>(R.id.sedentaryRowValue)
        val lightlyActiveRowText = view.findViewById<TextView>(R.id.lightlyActiveRowValue)
        val activeRowText = view.findViewById<TextView>(R.id.activeRowValue)
        val veryActiveRowText = view.findViewById<TextView>(R.id.veryActiveRowValue)

        val activityLevel = when(user.activityLevel.getLevel())
        {
            Level.SEDENTARY -> "Sedentary"
            Level.LIGHTLY_ACTIVE -> "Lightly Active"
            Level.ACTIVE -> "Active"
            Level.VERY_ACTIVE -> "Very Active"
        }

        activityLevelText.text = activityLevel
        activityLevelValue.text = activityLevel

        dailyCalorieNeedsText.text = user.getDailyCalorieIntake().toString()
        recommendedCalorieIntakeValue.text = getString(R.string.calPerDayShort, user.getDailyCalorieIntake().roundToInt().toString())
        bmrText.text = user.calculateBMR().toString()

        sedentaryRowText.text = user.calculateSedentaryCalNeed().toString()
        lightlyActiveRowText.text = user.calculateLightlyActiveCalNeed().toString()
        activeRowText.text = user.calculateActiveCalNeed().toString()
        veryActiveRowText.text = user.calculateVeryActiveCalNeed().toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_bmr_page, container, false)

        //Setup spinner
        Helpers.setUpSpinner(view.context, view.findViewById(R.id.intensitySpinner), resources.getStringArray(R.array.spinnerItems))

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(!isUpdatingPage) {
                    isUpdatingPage = true
                    updateBMRValues(view)
                    isUpdatingPage = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        }

        val itemSelectListener = object : OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                spinnerView: View?,
                position: Int,
                id: Long
            ) {
                if(!isUpdatingPage) {
                    isUpdatingPage = true
                    spinnerUpdate(view)
                    isUpdatingPage = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        val caloriesPerHourValue = view.findViewById<View>(R.id.caloriesPerHourValue) as EditText
        val workoutsPerWeekValue = view.findViewById<View>(R.id.workoutsPerWeekValue) as EditText
        val workoutLengthValue = view.findViewById<View>(R.id.workoutLengthValue) as EditText
        val intensitySpinner = view.findViewById<View>(R.id.intensitySpinner) as Spinner
        caloriesPerHourValue.addTextChangedListener(textWatcher)
        workoutLengthValue.addTextChangedListener(textWatcher)
        workoutsPerWeekValue.addTextChangedListener(textWatcher)
        intensitySpinner.onItemSelectedListener = itemSelectListener
        updateBMRpage(view)
        isUpdatingPage = false
        return view
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
package com.lifestyle.bmr

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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.lifestyleapp_spring2023.R
import com.example.lifestyleapp_spring2023.databinding.FragmentBmrPageFragmentBinding
import com.lifestyle.main.User


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private val BMRUser = User()


/**
 * A simple [Fragment] subclass.
 * Use the [bmr_page_fragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BMRPage : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        BMRUser.age = 23
        BMRUser.height = 180
        BMRUser.weight = 70
        BMRUser.sex = User.Sex.MALE
        BMRUser.activityLevel.caloriesPerHour = 200
        BMRUser.activityLevel.workoutsPerWeek = 3
        BMRUser.activityLevel.averageWorkoutLength = 0.5f
    }


    private val itemSelectListener = object : OnItemSelectedListener
    {
        override fun onItemSelected(
            parent: AdapterView<*>?,
            view: View?,
            position: Int,
            id: Long
        ) {
            spinnerUpdate()
            //updateBMRValues()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private fun spinnerUpdate()
    {
        val binding = FragmentBmrPageFragmentBinding.inflate(layoutInflater)
        binding.caloriesPerHourValue.setText(if(binding.intensitySpinner.selectedItem == 0)
        {
            "150"
        }
        else if(binding.intensitySpinner.selectedItem == 1)
        {
            "450"
        }
        else
        {
            "750"
        })
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
            0
        }

        BMRUser.activityLevel.caloriesPerHour = calPerHour
        BMRUser.activityLevel.workoutsPerWeek = workoutsPerWeek
        BMRUser.activityLevel.averageWorkoutLength = workoutLength as Float

        updateBMRpage(view)
    }

    private fun updateBMRpage(view: View){
        val calPerHourEditText = view.findViewById<EditText>(R.id.caloriesPerHourValue)
        val workoutsPerWeekEditText = view.findViewById<EditText>(R.id.workoutsPerWeekValue)
        val workoutLengthEditText = view.findViewById<EditText>(R.id.workoutLengthValue)

        //Only updates values if they need to be updated, otherwise infinite looping will occur
        val calPerHourText = calPerHourEditText.text.toString()
        if (calPerHourText == "" || calPerHourEditText.text.toString().toInt() != BMRUser.activityLevel.caloriesPerHour) {
            calPerHourEditText.setText(
                if (calPerHourText == "") {
                    "0"
                } else {
                    BMRUser.activityLevel.caloriesPerHour.toString()
                })
            return
        }
        val workoutsPerWeekText = workoutsPerWeekEditText.text.toString()
        if (workoutsPerWeekText == "" || workoutsPerWeekText.toInt() != BMRUser.activityLevel.workoutsPerWeek) {
            workoutsPerWeekEditText.setText(
                if (workoutsPerWeekText == "") {
                    "0"
                } else {
                    BMRUser.activityLevel.workoutsPerWeek.toString()
                })
            return
        }
        val workoutLengthText = workoutLengthEditText.text.toString()
        if (workoutLengthText == "" || workoutLengthText.toFloat() != BMRUser.activityLevel.averageWorkoutLength) {
            workoutLengthEditText.setText(
                if (workoutLengthText == "") {
                    "0.0"
                } else {
                    BMRUser.activityLevel.averageWorkoutLength.toString()
                })
            return
        }

        val caloriesBurnedPerWorkoutText = view.findViewById<TextView>(R.id.caloriesBurnedPerWorkoutValue)
        val workoutCaloriesPerWeekText = view.findViewById<TextView>(R.id.workoutCaloriesPerWeekValue)
        val intensitySpinner = view.findViewById<Spinner>(R.id.intensitySpinner)

        caloriesBurnedPerWorkoutText.text = BMRUser.activityLevel.getCaloriesBurnedPerWorkout().toString()
        workoutCaloriesPerWeekText.text = BMRUser.activityLevel.workoutCaloriesPerWeek().toString()
        if(BMRUser.activityLevel.caloriesPerHour < 300) {
            intensitySpinner.setSelection(0)
        }
        else if(BMRUser.activityLevel.caloriesPerHour < 600) {
            intensitySpinner.setSelection(1)
        }
        else{
            intensitySpinner.setSelection(2)
        }

        val activityLevelText = view.findViewById<TextView>(R.id.ActivityLevelTextView)
        val dailyCalorieNeedsText = view.findViewById<TextView>(R.id.dailyCalorieNeedsValue)
        val bmrText = view.findViewById<TextView>(R.id.bmrValue)
        val sedentaryRowText = view.findViewById<TextView>(R.id.sedentaryRowValue)
        val lightlyActiveRowText = view.findViewById<TextView>(R.id.lightlyActiveRowValue)
        val activeRowText = view.findViewById<TextView>(R.id.activeRowValue)
        val veryActiveRowText = view.findViewById<TextView>(R.id.veryActiveRowValue)

        activityLevelText.text = BMRUser.activityLevel.getLevel().toString()
        dailyCalorieNeedsText.text = BMRUser.getDailyCalorieIntake().toString()
        bmrText.text = BMRUser.calculateBMR().toString()

        sedentaryRowText.text = BMRUser.calculateSedentaryCalNeed().toString()
        lightlyActiveRowText.text = BMRUser.calculateLightlyActiveCalNeed().toString()
        activeRowText.text = BMRUser.calculateActiveCalNeed().toString()
        veryActiveRowText.text = BMRUser.calculateVeryActiveCalNeed().toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_bmr_page_fragment, container, false)

        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateBMRValues(view)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
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
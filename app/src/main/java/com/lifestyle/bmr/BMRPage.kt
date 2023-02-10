package com.lifestyle.bmr

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
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
    private val binding = FragmentBmrPageFragmentBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        binding.caloriesPerHourValue.addTextChangedListener(textWatcher)
        binding.workoutsPerWeekValue.addTextChangedListener(textWatcher)
        binding.workoutLengthValue.addTextChangedListener(textWatcher)
        binding.intensitySpinner.onItemSelectedListener = itemSelectListener

        updateBMRpage()
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
            updateBMRValues()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateBMRValues()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }
    }

    private fun spinnerUpdate()
    {
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
    private fun updateBMRValues()
    {
        val calPerHour = if(binding.caloriesPerHourValue.length() > 0) {
            binding.caloriesBurnedPerWorkoutValue.toString().toInt()
        } else {
            0
        }

        val workoutsPerWeek = if(binding.workoutsPerWeekValue.length() > 0) {
            binding.caloriesBurnedPerWorkoutValue.toString().toInt()
        } else {
            0
        }

        val workoutLength = if(binding.workoutLengthValue.length() > 0) {
            binding.caloriesBurnedPerWorkoutValue.toString().toFloat()
        } else {
            0
        }

        BMRUser.activityLevel.caloriesPerHour = calPerHour
        BMRUser.activityLevel.workoutsPerWeek = workoutsPerWeek
        BMRUser.activityLevel.averageWorkoutLength = workoutLength as Float

        updateBMRpage()
    }

    private fun updateBMRpage(){

        binding.caloriesPerHourValue.setText(BMRUser.activityLevel.caloriesPerHour.toString())
        binding.workoutsPerWeekValue.setText(BMRUser.activityLevel.workoutsPerWeek.toString())
        binding.workoutLengthValue.setText(BMRUser.activityLevel.averageWorkoutLength.toString())

        binding.caloriesBurnedPerWorkoutValue.text = BMRUser.activityLevel.getCaloriesBurnedPerWorkout().toString()
        binding.workoutCaloriesPerWeekTextView.text = BMRUser.activityLevel.workoutCaloriesPerWeek().toString()
        if(BMRUser.activityLevel.caloriesPerHour < 300) {
            binding.intensitySpinner.setSelection(0)
        }
        else if(BMRUser.activityLevel.caloriesPerHour < 600) {
            binding.intensitySpinner.setSelection(1)
        }
        else{
            binding.intensitySpinner.setSelection(2)
        }

        binding.ActivityLevelTextView.text = BMRUser.activityLevel.getLevel().toString()
        binding.dailyCalorieNeedsValue.text = BMRUser.getDailyCalorieIntake().toString()
        binding.bmrValue.text = BMRUser.calculateBMR().toString();

        binding.sedentaryRowValue.text = BMRUser.calculateSedentaryCalNeed().toString()
        binding.lightlyActiveRowValue.text = BMRUser.calculateLightlyActiveCalNeed().toString()
        binding.activeRowValue.text = BMRUser.calculateActiveCalNeed().toString()
        binding.veryActiveRowValue.text = BMRUser.calculateVeryActiveCalNeed().toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bmr_page_fragment, container, false)
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
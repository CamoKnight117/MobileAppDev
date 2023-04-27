package com.lifestyle.fragment

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.lifestyle.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_MIN = "min"
private const val ARG_MAX = "max"
private const val ARG_CURRENT = "current"
private const val ARG_STEP = "step"
private const val ARG_UNITS = "units"
private const val ARG_TITLE = "title"
private const val RESULT_KEY = "number"

/**
 * A simple [Fragment] subclass.
 * Use the [NumberPickerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NumberPickerFragment : DialogFragment() {
    private var minNumber: Int = 0
    private var maxNumber: Int = 0
    private var currentNumber: Int = 0
    private var step: Int = 0
    private var unitsName: String? = null
    private var title: String? = null
    private var unitsLabel: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            minNumber = it.getInt(ARG_MIN)
            maxNumber = it.getInt(ARG_MAX)
            currentNumber = it.getInt(ARG_CURRENT)
            step = it.getInt(ARG_STEP)
            unitsName = it.getString(ARG_UNITS)
            title = it.getString(ARG_TITLE)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.apply {
            //putInt(ARG_ID, numberPickerFragmentId)
            putInt(ARG_MIN, minNumber)
            putInt(ARG_MAX, maxNumber)
            putInt(ARG_CURRENT, currentNumber)
            putInt(ARG_STEP, step)
            putString(ARG_UNITS, unitsName)
            putString(ARG_TITLE, title)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)

        return activity?.let {
            val inflater = requireActivity().layoutInflater

            val newView = inflater.inflate(R.layout.fragment_number_picker, null)

            unitsLabel = newView.findViewById(R.id.numberPickerUnitsLabel)
            unitsLabel?.text = arguments?.getString(ARG_UNITS)

            var numberPicker: NumberPicker? = newView.findViewById<NumberPicker>(R.id.numberPickerNumberPicker)
            numberPicker?.minValue = minNumber / step
            numberPicker?.maxValue = maxNumber / step
            numberPicker?.value = currentNumber / step

            val numbers = mutableListOf<String>()

            for (i in minNumber..maxNumber step step) {
                numbers.add(i.toString())
            }

            numberPicker?.displayedValues = numbers.toTypedArray()

            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setTitle(arguments?.getString(ARG_TITLE))
                .setPositiveButton(R.string.submit,
                    DialogInterface.OnClickListener { dialog, id ->
                        if(numberPicker != null)
                            parentFragmentManager.setFragmentResult(this.tag ?: "", Bundle().apply {
                                putInt(RESULT_KEY, numberPicker.value * step)
                            })
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
                .setView(newView)
            // Create the AlertDialog object and return it
            return builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param id A number uniquely identifying the request this NumberPickerDialog is serving. It may be useful to set this to the ID of the view that triggered this dialog.
         * @return A new instance of fragment NumberPickerFragment.
         */
        @JvmStatic
        //fun newInstance(min: Int, max: Int, current: Int, requestKey: String) =
        fun newInstance(title: String, min: Int, max: Int, current: Int, step: Int, unitsName: String) =
            NumberPickerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_MIN, min)
                    putInt(ARG_MAX, max)
                    putInt(ARG_CURRENT, current)
                    putInt(ARG_STEP, step)
                    putString(ARG_UNITS, unitsName)
                    putString(ARG_TITLE, title)
                }
            }

        public fun getResultNumber(result: Bundle): Int {
            return result.getInt(RESULT_KEY)
        }
    }
}
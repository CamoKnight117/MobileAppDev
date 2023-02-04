package com.lifestyle.profile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.lifestyleapp_spring2023.R
import com.lifestyle.main.User
import com.lifestyle.main.UserProvider

class ProfileFragment : Fragment() {
    private var userProvider: UserProvider? = null;
    private var nameEditText: EditText? = null;
    private var ageEditText: EditText? = null;
    private var weightEditText: EditText? = null;
    private var heightEditText: EditText? = null;
    private var sexSpinner: Spinner? = null;

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

        //Get the text views
        nameEditText = view.findViewById<View>(R.id.profileName) as EditText
        ageEditText = view.findViewById<View>(R.id.profileAge) as EditText
        weightEditText = view.findViewById<View>(R.id.profileWeight) as EditText
        heightEditText = view.findViewById<View>(R.id.profileHeight) as EditText
        sexSpinner = view.findViewById<View>(R.id.profileSex) as Spinner

        //Get the data that was sent in
        var user = userProvider!!.getUser()

        //Set the data
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

        // Set up handlers to change the data.
        nameEditText?.doOnTextChanged { text, start, before, count -> user.name = text?.toString() }
        ageEditText?.doOnTextChanged { text, start, before, count ->
            if(text != null)
                user.age = Integer.parseInt(text.toString())
        }
        heightEditText?.doOnTextChanged { text, start, before, count ->
            if(text != null)
                user.height = Integer.parseInt(text.toString())
        }
        weightEditText?.doOnTextChanged { text, start, before, count ->
            if(text != null)
                user.weight = Integer.parseInt(text.toString())
        }
        sexSpinner?.onItemSelectedListener = sexSpinnerListener

        return view
    }

    private var sexSpinnerListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
            if(pos==0)
                userProvider?.getUser()?.sex = User.Sex.MALE
            if(pos==1)
                userProvider?.getUser()?.sex = User.Sex.FEMALE
        }

        override fun onNothingSelected(parent: AdapterView<*>) { }
    }
}
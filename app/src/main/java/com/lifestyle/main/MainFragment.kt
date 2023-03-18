package com.lifestyle.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.lifestyle.R


/**
 *
 */
class MainFragment : Fragment() {
    private var fragmentStarter:fragmentStarterInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentStarter = try {
            context as fragmentStarterInterface
        }
        catch(e:java.lang.ClassCastException) {
            throw java.lang.ClassCastException("Main activity must implement fragmentStarterInterface")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val profileButton = view.findViewById<Button>(R.id.prof_main_fragment_button)
        val bmrButton = view.findViewById<Button>(R.id.bmr_main_fragment_button)
        val weatherButton = view.findViewById<Button>(R.id.weather_main_fragment_button)
        val hikesButton = view.findViewById<Button>(R.id.hikes_main_fragment_button)

        profileButton.setOnClickListener{
            fragmentStarter?.startProfileFrag()
        }
        bmrButton.setOnClickListener{
            fragmentStarter?.startBMRFrag()
        }
        weatherButton.setOnClickListener{
            fragmentStarter?.startWeatherFrag()
        }
        hikesButton.setOnClickListener{
            fragmentStarter?.startHikesFrag()
        }

        return view
    }

    companion object {
        /**
         */
        @JvmStatic
        fun newInstance() =
            MainFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}
package com.example.ponggame.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("HomePage", "Home Page created!")
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val pressToStart = view.findViewById<Button>(R.id.press_button)
        pressToStart.setOnClickListener {
            view.findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToLoginFragment()
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

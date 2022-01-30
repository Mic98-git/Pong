package com.example.ponggame.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("HomePage", "Home Page created!")
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.homeConstraintLayout

        val home = view.findViewById<ConstraintLayout>(R.id.home_constraint_layout)
        val pressToStart = view.findViewById<ImageView>(R.id.press_button)

        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = 1000
        alphaAnimation.repeatCount = Animation.INFINITE
        alphaAnimation.repeatMode = Animation.REVERSE
        pressToStart.startAnimation(alphaAnimation)

        pressToStart.setOnClickListener {
            view.findNavController().navigate(
                HomePageFragmentDirections.actionHomePageFragmentToLoginFragment()
            )
        }

        home.setOnClickListener {
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

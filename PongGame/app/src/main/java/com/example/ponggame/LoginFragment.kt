package com.example.ponggame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.ponggame.databinding.FragmentLoginBinding
import com.example.ponggame.databinding.FragmentMenuBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("LoginFragment", "Login Fragment created!")
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        setActivityTitle("Login")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.constraintLayout
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }
}
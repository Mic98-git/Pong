package com.example.ponggame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.ponggame.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedUsername: String
    private lateinit var insertedPassword: String

    fun getUserData(){
        insertedUsername = constraintLayout
            .findViewById<EditText>(
                R.id.username_login_edit_text
            ).text.toString()
        insertedPassword = constraintLayout
            .findViewById<EditText>(
                R.id.password_login_edit_text
            ).text.toString()
    }



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
        constraintLayout = binding.loginConstraintLayout

        val loginButton = view.findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            getUserData()
            if(insertedPassword.equals("palle")) {
                view.findNavController().navigate(
                    LoginFragmentDirections
                        .actionLoginFragmentToMenuFragment(
                            username = insertedUsername,
                            password = insertedPassword
                        )
                )
            }
            else{
                Toast.makeText(
                    context,"Wrong password!",Toast.LENGTH_SHORT
                ).show()
            }
        }

        val registerButton = view.findViewById<Button>(R.id.signup_button)
        registerButton.setOnClickListener {
            view.findNavController().navigate(
                LoginFragmentDirections
                    .actionLoginFragmentToRegisterFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }
}
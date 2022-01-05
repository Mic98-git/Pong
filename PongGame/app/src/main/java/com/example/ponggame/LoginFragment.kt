package com.example.ponggame

import android.os.Bundle
import android.util.Log
import android.util.Patterns
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
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedEmail: String
    private lateinit var insertedPassword: String

    private fun getUserData() {
        insertedEmail = constraintLayout
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
    ): View {
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
            if (insertedEmail.isNotEmpty() && insertedPassword.isNotEmpty()) {
                if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches())
                    Toast.makeText(
                        context,
                        "Please provide a valid email",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    loginUser()
                }
            } else {
                Toast.makeText(
                    context,
                    "Please insert all the requested data",
                    Toast.LENGTH_SHORT
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

    private fun loginUser() {
        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().signOut()
        }
        FirebaseAuth.getInstance().signInWithEmailAndPassword(insertedEmail, insertedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    binding.root.findNavController().navigate(
                        LoginFragmentDirections
                            .actionLoginFragmentToMenuFragment()
                    )
                    Toast.makeText(
                        context,
                        "You are logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        "Failed to login! Please check your credentials",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
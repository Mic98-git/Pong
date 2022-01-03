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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.ponggame.databinding.FragmentRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedEmail: String
    private lateinit var insertedUsername: String
    private lateinit var insertedPassword: String
    private lateinit var confirmedPassword: String

    private fun getUserData() {
        insertedEmail = constraintLayout
            .findViewById<EditText>(
                R.id.email_register_edit_text
            ).text.toString()
        insertedUsername = constraintLayout
            .findViewById<EditText>(
                R.id.username_register_edit_text
            ).text.toString()
        insertedPassword = constraintLayout
            .findViewById<EditText>(
                R.id.password_register_edit_text
            ).text.toString()
        confirmedPassword = constraintLayout
            .findViewById<EditText>(
                R.id.confirm_password_register_edit_text
            ).text.toString()
    }

    private fun checkPassword(str1: String, str2: String): Boolean {
        return str1==str2
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        setActivityTitle("Register")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.registerConstraintLayout
        val registerButton = view.findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            getUserData()
            if (insertedPassword.length < 6) {
                Toast.makeText(
                    context,
                    "Min password should be 6 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (insertedEmail.isNotEmpty() && insertedUsername.isNotEmpty() && insertedPassword.isNotEmpty() && checkPassword(insertedPassword, confirmedPassword)) {
                if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches())
                    Toast.makeText(
                        context,
                        "Please provide a valid email",
                        Toast.LENGTH_SHORT
                    ).show()
                else {
                    registerUser()
                    view.findNavController().navigate(
                        RegisterFragmentDirections
                            .actionRegisterFragmentToMenuFragment()
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Please insert all the requested data",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun registerUser() {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(insertedEmail, insertedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(insertedUsername, insertedPassword, insertedEmail, 0)
                        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().currentUser?.uid.toString())
                            .setValue(user).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "You are registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to register! Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                else {
                    Toast.makeText(
                        context,
                        "Failed to register! Try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        return
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

}
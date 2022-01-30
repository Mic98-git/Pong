package com.example.ponggame.fragments

import android.app.AlertDialog
import android.content.DialogInterface
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
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentLoginBinding
import com.sanojpunchihewa.glowbutton.GlowButton

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedEmail: String
    private lateinit var insertedPassword: String
    private lateinit var builder: AlertDialog.Builder

    private fun getTypedData() {
        insertedEmail = constraintLayout
            .findViewById<EditText>(
                R.id.username_login_edit_text
            ).text.toString().filter { !it.isWhitespace() }
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
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.loginConstraintLayout

        builder = AlertDialog.Builder(context)
        builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Email not verified!</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setMessage("Please verify your email address to login")
        builder.setNegativeButton("Ok",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
            }
        )

        val loginButton = view.findViewById<GlowButton>(R.id.login_button)
        loginButton.setOnClickListener {
            getTypedData()
            if (insertedEmail.isNotEmpty() && insertedPassword.isNotEmpty() && checkCredentials()) {
                loginUser()
            } else {
                Toast.makeText(
                    context,
                    "Please check your credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val forgotPasswordButton = view.findViewById<Button>(R.id.forgot_password_button)
        forgotPasswordButton.setOnClickListener {
            view.findNavController().navigate(
                LoginFragmentDirections
                    .actionLoginFragmentToForgotPasswordFragment()
            )
        }

        val registerButton = view.findViewById<Button>(R.id.signup_button)
        registerButton.setOnClickListener {
            view.findNavController().navigate(
                LoginFragmentDirections
                    .actionLoginFragmentToRegisterFragment()
            )
        }
    }

    private fun checkCredentials(): Boolean {
        var result = true
        if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches()) {
            constraintLayout.findViewById<EditText>(R.id.username_login_edit_text).error = "Please provide a valid email"
            constraintLayout.findViewById<EditText>(R.id.username_login_edit_text).requestFocus()
            result = false
        }
        if (insertedPassword.length < 6) {
            constraintLayout.findViewById<EditText>(R.id.password_login_edit_text).error = "Please insert a valid password with more than 6 character"
            constraintLayout.findViewById<EditText>(R.id.password_login_edit_text).requestFocus()
            result = false
        }
        /*else if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches() && insertedPassword.length < 6) {
            constraintLayout.findViewById<EditText>(R.id.username_login_edit_text).error = "Please provide a valid email"
            constraintLayout.findViewById<EditText>(R.id.password_login_edit_text).error = "Please insert a valid password with more than 6 char"
            result = false
        }*/
        return result
    }

    private fun loginUser() {
        DatabaseImpl.loginUser(insertedEmail, insertedPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (DatabaseImpl.getAuthInstance().currentUser!!.isEmailVerified) {
                    binding.root.findNavController().navigate(
                        LoginFragmentDirections
                            .actionLoginFragmentToMenuFragment()
                    )
                    Toast.makeText(
                        context,
                        "You are logged in successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    val alert = builder.create()
                    alert.show()
                }
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
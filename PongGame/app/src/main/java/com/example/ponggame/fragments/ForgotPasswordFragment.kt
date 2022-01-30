package com.example.ponggame.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private var _binding: FragmentForgotPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var insertedEmail: String
    private lateinit var builder: AlertDialog.Builder

    private fun getTypedData() {
        insertedEmail = constraintLayout
            .findViewById<EditText>(
                R.id.email_edit_text
            ).text.toString()
    }

    private fun validateInput(): Boolean {
        if (insertedEmail == "") {
            constraintLayout.findViewById<EditText>(R.id.email_edit_text).requestFocus()
            constraintLayout.findViewById<EditText>(R.id.email_edit_text).error = "Please insert an email"
            return false
        }
        // checking the proper email format
        if (!isEmailValid(insertedEmail)) {
            constraintLayout.findViewById<EditText>(R.id.email_edit_text).requestFocus()
            constraintLayout.findViewById<EditText>(R.id.email_edit_text).error = "Please provide a valid email"
            return false
        }
        return true
    }

    private fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        setActivityTitle("Password Forgotten")
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.forgotPasswordConstraintLayout

        builder = AlertDialog.Builder(context)
        builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Email sent to your email address!</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setNegativeButton("Ok",
            DialogInterface.OnClickListener { dialog, _ ->
                dialog.dismiss()
                view.findNavController().navigate(
                    ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
                )
            }
        )

        val restorePassword = view.findViewById<Button>(R.id.pw_reset)
        restorePassword.setOnClickListener {
            getTypedData()
            if (validateInput()) {
                DatabaseImpl.resetPassword(insertedEmail).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val alert = builder.create()
                        alert.show()
                    }
                    else {
                        Toast.makeText(
                            context,
                            "Error during the procedure. Please, try again!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        val backToLogin = view.findViewById<Button>(R.id.login_button)
        backToLogin.setOnClickListener {
            view.findNavController().navigate(
                ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment()
            )
        }
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

}
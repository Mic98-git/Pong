package com.example.ponggame

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.ponggame.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedUsername: String
    private lateinit var insertedPassword: String
    private lateinit var confirmedPassword: String

    fun getUserData(): Array<String>{
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
        //fare funzione getUsername, getPassword, getConfirmedPassword
        // al posto di questa
        //val userData =
    }

    fun checkPassword(str1: String, str2:String): Boolean{
        return str1.equals(str2)
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

        }

    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

}
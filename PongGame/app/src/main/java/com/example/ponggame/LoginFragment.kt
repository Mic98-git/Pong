package com.example.ponggame

import android.os.Bundle
import android.text.TextUtils
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
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var insertedUsername: String
    private lateinit var insertedPassword: String

    private fun getUserData(){
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
        constraintLayout = binding.constraintLayout

        val button = view.findViewById<Button>(R.id.login_button)
        button.setOnClickListener {
            getUserData()
            when {
                TextUtils.isEmpty(insertedUsername) -> {
                    Toast.makeText(
                        context,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(insertedPassword) -> {
                    Toast.makeText(
                        context,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(insertedUsername, insertedPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "You are logged in successfully",
                                Toast.LENGTH_SHORT
                            ).show()
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
                                context,
                                "Wrong password!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
            /*
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
            }*/
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

/* REGISTRATION
when {
                TextUtils.isEmpty(insertedUsername) -> {
                    Toast.makeText(
                        context,
                        "Please enter email",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                TextUtils.isEmpty(insertedPassword) -> {
                    Toast.makeText(
                        context,
                        "Please enter password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(insertedUsername, insertedPassword).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                "You are registered successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
 */
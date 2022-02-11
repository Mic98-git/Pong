package com.example.ponggame.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ponggame.*
import com.example.ponggame.databinding.FragmentLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout
    //private var googleSignInClient = (activity as MainActivity).googleSignInClient

    private lateinit var insertedEmail: String
    private lateinit var insertedPassword: String
    private lateinit var builder: AlertDialog.Builder
    private lateinit var loginButton: ImageView

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
        savedInstanceState: Bundle?,
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
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            builder.setTitle(HtmlCompat.fromHtml("<font color='#ffffff'>Email not verified!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        } else {
            builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Email not verified!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        builder.setMessage("Please verify your email address to login")
        builder.setNegativeButton("Ok",
            DialogInterface.OnClickListener { dialog, _ ->
                loginButton.alpha = 1.0F
                dialog.dismiss()
            }
        )

        loginButton = view.findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            loginButton.alpha = 0.5F
            getTypedData()
            if (insertedEmail.isNotEmpty() && insertedPassword.isNotEmpty() && checkCredentials()) {
                CoroutineScope(Job()).launch(Dispatchers.Main) {
                    loginUser()
                }
            } else {
                loginButton.alpha = 1.0F
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
            constraintLayout.findViewById<EditText>(R.id.username_login_edit_text).error =
                "Please provide a valid email"
            constraintLayout.findViewById<EditText>(R.id.username_login_edit_text).requestFocus()
            result = false
        }
        if (insertedPassword.length < 6) {
            constraintLayout.findViewById<EditText>(R.id.password_login_edit_text).error =
                "Please insert a valid password with more than 6 character"
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

    private suspend fun loginUser() {
        try {
            RetrofitClient.instance.loginUser(insertedEmail, insertedPassword)
            Toast.makeText(
                context,
                "You are logged in successfully",
                Toast.LENGTH_SHORT
            ).show()
            binding.root.findNavController().navigate(
                LoginFragmentDirections
                    .actionLoginFragmentToMenuFragment()
            )
        } catch (response: HttpException) {
            loginButton.alpha = 1.0F
            Toast.makeText(
                context,
                "Failed to login! Please check your credentials",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /*
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
                loginButton.alpha = 1.0F
                Toast.makeText(
                    context,
                    "Failed to login! Please check your credentials",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

    /*
    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        login.launch(signInIntent)
    }

    private val login =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    Toast.makeText(
                        context,
                        "Google sign-in succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(
                        context,
                        "Google sign-in failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        DatabaseImpl.getAuthInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = DatabaseImpl.getAuthInstance().currentUser
                    val username = user!!.displayName.toString()
                    val email = user!!.email.toString()
                    if (!checkIfUserAlreadyExists(user!!.uid.toString())) {
                        user.photoUrl?.let { DatabaseImpl.uploadProfilePicture(it) }
                        DatabaseImpl.createNewUser(username, email).addOnCompleteListener { userCreated ->
                            if (userCreated.isSuccessful) {
                                requireView().findNavController().navigate(
                                    LoginFragmentDirections
                                        .actionLoginFragmentToMenuFragment()
                                )
                            }
                            else {
                                Toast.makeText(
                                    context,
                                    "Failed to create your profile! Please try-again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    else {
                        requireView().findNavController().navigate(
                            LoginFragmentDirections
                                .actionLoginFragmentToMenuFragment()
                        )
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    // Google Sign In failed, update UI appropriately
                    Toast.makeText(
                        context,
                        "signInWithCredential failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun checkIfUserAlreadyExists(uid: String): Boolean {
        var userExists = false
        DatabaseImpl.getUsersReference().child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userExists = snapshot.exists()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })
        return userExists
    }
    */

}
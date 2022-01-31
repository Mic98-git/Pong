package com.example.ponggame.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentRegisterBinding
import de.hdodenhof.circleimageview.CircleImageView

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var builder: AlertDialog.Builder
    private lateinit var registerButton : ImageView

    private lateinit var insertedEmail: String
    private lateinit var insertedUsername: String
    private lateinit var insertedPassword: String
    private lateinit var confirmedPassword: String
    private lateinit var profileImage: CircleImageView
    private lateinit var profileUri: Uri
    private var imageSelected: Boolean = false

    private val selectImageFromGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val res: Intent? = result.data
            if (res != null) run {
                profileUri = res.data!!
                profileImage = binding.root.findViewById(R.id.profile_image)
                profileImage.setImageURI(profileUri)
                imageSelected = true
            }
        }
    }

    private fun getTypedData() {
        insertedEmail = constraintLayout
            .findViewById<EditText>(
                R.id.email_register_edit_text
            ).text.toString().filter { !it.isWhitespace() }
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
        (activity as AppCompatActivity?)!!.supportActionBar?.show()

        val selectImageButton = binding.root.findViewById<ImageButton>(R.id.add_profile_image_button)
        selectImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            //intent.action = Intent.ACTION_GET_CONTENT
            intent.action = Intent.ACTION_PICK //image from gallery
            selectImageFromGallery.launch(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.registerConstraintLayout

        registerButton = view.findViewById(R.id.register_button)

        builder = AlertDialog.Builder(context)
        builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>You are registered successfully!</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setMessage("Please check your email address to confirm it")
        builder.setNegativeButton("Ok",
            DialogInterface.OnClickListener { dialog, _ ->
                registerButton.alpha = 1.0F
                dialog.dismiss()
                binding.root.findNavController().navigate(
                    RegisterFragmentDirections
                        .actionRegisterFragmentToLoginFragment()
                )
            }
        )

        registerButton.setOnClickListener {
            registerButton.alpha = 0.5F
            getTypedData()
            if (insertedEmail.isNotEmpty() && insertedUsername.isNotEmpty() && insertedPassword.isNotEmpty() && confirmedPassword.isNotEmpty() && checkCredentials()) {
                registerUser()
            } else {
                Toast.makeText(
                    context,
                    "Please check your data",
                    Toast.LENGTH_SHORT
                ).show()
                registerButton.alpha = 1.0F
            }
        }
    }

    private fun checkCredentials(): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches()) {
            constraintLayout.findViewById<EditText>(R.id.email_register_edit_text).error =
                "Please provide a valid email"
            constraintLayout.findViewById<EditText>(R.id.email_register_edit_text).requestFocus()
            return false
        }
        if (insertedPassword.length < 6) {
            constraintLayout.findViewById<EditText>(R.id.password_register_edit_text).error = "Min password should be 6 characters"
            constraintLayout.findViewById<EditText>(R.id.password_register_edit_text).requestFocus()
            return false
        }
        if (!checkPassword(insertedPassword, confirmedPassword)){
            constraintLayout.findViewById<EditText>(R.id.confirm_password_register_edit_text).error = "The two passwords don't match"
            constraintLayout.findViewById<EditText>(R.id.confirm_password_register_edit_text).requestFocus()
            return false
        }
        return true
    }

    private fun registerUser() {
        DatabaseImpl.registerUser(insertedEmail, insertedPassword)
            .addOnCompleteListener { registration ->
                if (registration.isSuccessful) {
                    DatabaseImpl.createNewUser(insertedUsername, insertedEmail)
                        .addOnCompleteListener { createUser ->
                            if (createUser.isSuccessful) {
                                uploadImageToFirebase()
                                DatabaseImpl.sendEmailVerification().addOnCompleteListener { emailVerification ->
                                    if (emailVerification.isSuccessful) {
                                        val alert = builder.create()
                                        alert.show()
                                    }
                                    else {
                                        registerButton.alpha = 1.0F
                                        Toast.makeText(
                                            context,
                                            emailVerification.exception!!.message,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                registerButton.alpha = 1.0F
                                Toast.makeText(
                                    context,
                                    "Failed to create your profile! Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    registerButton.alpha = 1.0F
                    constraintLayout.findViewById<EditText>(R.id.email_register_edit_text).error = "This email is already associated with another account!"
                    constraintLayout.findViewById<EditText>(R.id.email_register_edit_text).requestFocus()
                }
            }
    }

    private fun uploadImageToFirebase() {
        if (imageSelected) {
            DatabaseImpl.uploadProfilePicture(profileUri)
        }
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

}

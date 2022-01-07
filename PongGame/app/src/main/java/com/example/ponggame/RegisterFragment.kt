package com.example.ponggame

import android.app.Activity
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
import androidx.navigation.findNavController
import com.example.ponggame.databinding.FragmentRegisterBinding
import de.hdodenhof.circleimageview.CircleImageView

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

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
                profileImage = binding.root.findViewById(R.id.profile_image_icon_register)
                profileImage.setImageURI(profileUri)
                imageSelected = true
            }
        }
    }

    private fun getTypedData() {
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

        val selectImageButton = binding.root.findViewById<ImageButton>(R.id.add_profile_image_button)
        selectImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            selectImageFromGallery.launch(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.registerConstraintLayout

        val registerButton = view.findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            getTypedData()
            if (insertedEmail.isNotEmpty() && insertedUsername.isNotEmpty() && insertedPassword.isNotEmpty() && checkPassword(insertedPassword, confirmedPassword)) {
                if (!Patterns.EMAIL_ADDRESS.matcher(insertedEmail).matches())
                    Toast.makeText(
                        context,
                        "Please provide a valid email",
                        Toast.LENGTH_SHORT
                    ).show()
                if (insertedPassword.length < 6) {
                    Toast.makeText(
                        context,
                        "Min password should be 6 characters",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    registerUser()
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
        DatabaseImpl.registerUser(insertedEmail, insertedPassword)
            .addOnCompleteListener { registration ->
                if (registration.isSuccessful) {
                    DatabaseImpl.createNewUser(insertedUsername, insertedEmail)
                        .addOnCompleteListener { createUser ->
                            if (createUser.isSuccessful) {
                                uploadImageToFirebase()
                                binding.root.findNavController().navigate(
                                    RegisterFragmentDirections
                                        .actionRegisterFragmentToLoginFragment()
                                )
                                Toast.makeText(
                                    context,
                                    "You are registered successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Failed to create your profile! Try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Failed to register! Try again",
                        Toast.LENGTH_SHORT
                    ).show()
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

package com.example.ponggame.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentUserProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    private lateinit var usernameInputText: EditText
    private lateinit var userUsername: TextView
    private var editing: Boolean = false
    private lateinit var addImageButton: ImageButton
    private lateinit var logOutButton: Button
    private lateinit var undoButton: Button
    private lateinit var confirmButton: Button
    private lateinit var editUsernameButton: ImageButton

    private lateinit var builder: AlertDialog.Builder
    private lateinit var alert: AlertDialog
    private lateinit var toast: Toast

    // Variables for image update
    private var imageSelected: Boolean = false
    private lateinit var profileImage: CircleImageView
    private lateinit var profileUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("UserProfileFragment", "User profile fragment created!")
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        setActivityTitle("User Profile")
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.constraintLayoutProfileImage
        setProfilePicture()

        if (DatabaseImpl.getCurrentUserId().isNotEmpty()) {
            DatabaseImpl.getUsersReference().child(DatabaseImpl.getCurrentUserId())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        view.findViewById<TextView>(R.id.user_email).text =
                            snapshot.child("email").value.toString()
                        view.findViewById<TextView>(R.id.user_username).text =
                            snapshot.child("username").value.toString()
                        view.findViewById<TextView>(R.id.user_score).text =
                            snapshot.child("score").value.toString()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        toast = Toast.makeText(
                            context,
                            "Error retrieving your data",
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    }
                })
        }

        // Initialize builder for alert
        builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Attention!</font>", HtmlCompat.FROM_HTML_MODE_LEGACY))
        builder.setMessage("Username field must be not empty")
        builder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        // Initialize buttons
        addImageButton = view.findViewById(R.id.add_profile_image_button)
        logOutButton = view.findViewById(R.id.log_out_button)
        undoButton = view.findViewById(R.id.undo_update_button)
        confirmButton = view.findViewById(R.id.confirm_update_button)
        editUsernameButton = view.findViewById(R.id.edit_username_button)


        // Handling edit text and image
        userUsername = view.findViewById(R.id.user_username)
        usernameInputText = view.findViewById(R.id.user_username_input)
        profileImage = view.findViewById(R.id.profile_image)


        editUsernameButton.setOnClickListener {
            if (!editing) {
                editing = true
                editInputTextVisibilityOn()
                editButtonsVisibilityOn()
                usernameInputText.requestFocus()
            }
        }

        confirmButton.setOnClickListener {
            if (editing) {
                if (!usernameInputText.text.toString().equals("")) {
                    DatabaseImpl.updateUserUsername(usernameInputText.text.toString())
                    editInputVisibilityOff()
                    editButtonsVisibilityOff()
                    toast = Toast.makeText(
                        this.context,
                        "Username successfully updated!",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
                    toast.show()
                    editing = false
                } else {
                    alert = builder.create()
                    alert.show()
                }
            }
        }

        undoButton.setOnClickListener {
            editing = false
            editInputVisibilityOff()
            editButtonsVisibilityOff()
        }

        addImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            //intent.action = Intent.ACTION_GET_CONTENT
            intent.action = Intent.ACTION_PICK //image from gallery
            selectImageFromGallery.launch(intent)
        }

        logOutButton.setOnClickListener {
            logOut()
            view.findNavController().navigate(
                UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment()
            )
        }
    }

    override fun onDestroyView() {
        if (editing) {
            undoButton.performClick()
        }
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

    private fun logOut() {
        DatabaseImpl.logOut()
        Toast.makeText(
            context,
            "You are successfully logged out",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun setProfilePicture() {
        val localFile = File.createTempFile("tempImage", "")
        DatabaseImpl.getProfilePicture(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            profileImage.setImageBitmap(bitmap)
        }
    }

    private fun editInputTextVisibilityOn() {
        userUsername.visibility = View.INVISIBLE
        usernameInputText.visibility = View.VISIBLE
    }

    private fun editInputVisibilityOff() {
        userUsername.visibility = View.VISIBLE
        usernameInputText.visibility = View.INVISIBLE
    }

    private fun editButtonsVisibilityOn() {
        editUsernameButton.visibility = View.INVISIBLE
        logOutButton.visibility = View.INVISIBLE
        undoButton.visibility = View.VISIBLE
        confirmButton.visibility = View.VISIBLE
    }

    private fun editButtonsVisibilityOff() {
        editUsernameButton.visibility = View.VISIBLE
        logOutButton.visibility = View.VISIBLE
        undoButton.visibility = View.INVISIBLE
        confirmButton.visibility = View.INVISIBLE
    }

    private val selectImageFromGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val res: Intent? = result.data
                if (res != null) run {
                    profileUri = res.data!!
                    profileImage = binding.root.findViewById(R.id.profile_image)
                    profileImage.setImageURI(profileUri)
                    imageSelected = true
                    DatabaseImpl.updateProfilePicture(profileUri)
                }
            }
        }

}
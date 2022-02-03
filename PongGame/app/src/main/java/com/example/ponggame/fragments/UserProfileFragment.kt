package com.example.ponggame.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
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
    private lateinit var logOutButton: ImageView
    private lateinit var undoButton: ImageView
    private lateinit var confirmButton: ImageView
    private lateinit var editUsernameButton: ImageButton

    private lateinit var failedUsernameBuilder: AlertDialog.Builder
    private lateinit var logoutBuilder: AlertDialog.Builder
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
        setActivityTitle("My Profile")
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

        // Initialize builder for alert
        failedUsernameBuilder = AlertDialog.Builder(this.requireContext())
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            failedUsernameBuilder.setTitle(HtmlCompat.fromHtml("<font color='#ffffff'>Attention!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        else {
            failedUsernameBuilder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Attention!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        failedUsernameBuilder.setMessage("Username field must be not empty")
        failedUsernameBuilder.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
            confirmButton.alpha = 1.0F
            dialog.dismiss()
        })

        logoutBuilder = AlertDialog.Builder(this.requireContext())
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK === Configuration.UI_MODE_NIGHT_YES) {
            logoutBuilder.setTitle(HtmlCompat.fromHtml("<font color='#ffffff'>Attention!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        else {
            logoutBuilder.setTitle(HtmlCompat.fromHtml("<font color='#000000'>Attention!</font>",
                HtmlCompat.FROM_HTML_MODE_LEGACY))
        }
        logoutBuilder.setMessage("Are you sure you want to log out?")
        logoutBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            logOutButton.alpha = 1.0F
            logOut()
            view.findNavController().navigate(
                UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment()
            )
        })
        logoutBuilder.setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
            logOutButton.alpha = 1.0F
            dialog.dismiss()
        })

        editUsernameButton.setOnClickListener {
            if (!editing) {
                editing = true
                editInputTextVisibilityOn()
                editButtonsVisibilityOn()
                usernameInputText.requestFocus()
            }
        }

        confirmButton.setOnClickListener {
            confirmButton.alpha = 0.5F
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
                    confirmButton.alpha = 1.0F
                    editing = false
                } else {
                    alert = failedUsernameBuilder.create()
                    alert.show()
                }
            }
        }

        undoButton.setOnClickListener {
            undoButton.alpha = 0.5F
            editing = false
            editInputVisibilityOff()
            editButtonsVisibilityOff()
            undoButton.alpha = 1.0F
        }

        addImageButton.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            //intent.action = Intent.ACTION_GET_CONTENT
            intent.action = Intent.ACTION_PICK //image from gallery
            selectImageFromGallery.launch(intent)
        }

        logOutButton.setOnClickListener {
            logOutButton.alpha = 0.5F
            alert = logoutBuilder.create()
            alert.show()
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
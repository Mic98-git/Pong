package com.example.ponggame.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentMenuBinding
import com.google.firebase.database.*
import java.io.File


class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MenuFragment", "Menu Fragment created!")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        setActivityTitle("Menu")
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.menuConstraintLayout
        val localFile = File.createTempFile("tempImage", "")
        DatabaseImpl.getProfilePicture(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.userImage.setImageBitmap(bitmap)
        }

        if (DatabaseImpl.getCurrentUserId().isNotEmpty()) {
            DatabaseImpl.getUsersReference().child(DatabaseImpl.getCurrentUserId()).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    view.findViewById<TextView>(R.id.username_text_view).text = snapshot.child("username").value.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Error retrieving your data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        val rankButton = view.findViewById<Button>(R.id.ranking_list_button)
        rankButton.setOnClickListener {
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToRankingListFragment()
            )
        }

        val myProfileButton = view.findViewById<Button>(R.id.profile_button)
        myProfileButton.setOnClickListener {
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToUserProfileFragment()
            )
        }

        val newGameButton = view.findViewById<Button>(R.id.new_game_button)
        newGameButton.setOnClickListener {
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToPongActivity()
            )
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

package com.example.ponggame

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
import androidx.navigation.fragment.navArgs
import com.example.ponggame.databinding.FragmentMenuBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout
    //private lateinit var user: User

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("MenuFragment", "Menu Fragment created!")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        setActivityTitle("Menu")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.menuConstraintLayout

        val userid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        if (userid.isNotEmpty()) {
            databaseReference.child(userid).addValueEventListener(object : ValueEventListener{
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
            /*request to list all the users with their scores

            */
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToRankingListFragment()
            )
        }

        val logOutButton = view.findViewById<Button>(R.id.log_out_button)
        logOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            // view.findNavController().navigate(
            // //go to the welcome page
            // )
            Toast.makeText(
                context,
                "You are successfully logged out",
                Toast.LENGTH_SHORT
            ).show()
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

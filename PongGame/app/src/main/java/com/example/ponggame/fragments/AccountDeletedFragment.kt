package com.example.ponggame.fragments

import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentAccountDeletedBinding
import com.example.ponggame.databinding.FragmentMenuBinding
import com.google.firebase.database.*
import java.io.File
import kotlin.concurrent.thread

class AccountDeletedFragment : Fragment() {
    private var _binding: FragmentAccountDeletedBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    // Set alert variables
    private lateinit var goodByeBuilder: AlertDialog.Builder
    private lateinit var alert: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAccountDeletedBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.hide()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.accountDeletedConstraintLayout

        // Handle dialog
        goodByeBuilder = AlertDialog.Builder(this.requireContext())
        setWhiteTitleForAlert(goodByeBuilder, "Your account has been deleted!")
        goodByeBuilder.setMessage("we will miss you :(")
        goodByeBuilder.setPositiveButton(
            "Ok",
            DialogInterface.OnClickListener { dialog, which ->
                view.findNavController().navigate(
                    AccountDeletedFragmentDirections.actionAccountDeletedFragmentToHomePageFragment()
                )
                dialog.dismiss()
            })
        super.onViewCreated(view, savedInstanceState)
        Thread.sleep(1000)
        alert = goodByeBuilder.create()
        alert.show()
    }

    private fun setWhiteTitleForAlert(builder: AlertDialog.Builder, title: String) {
        if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            ===
            Configuration.UI_MODE_NIGHT_YES
        ) {
            builder.setTitle(
                HtmlCompat.fromHtml(
                    "<font color='#ffffff'>${title}</font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
        } else {
            builder.setTitle(
                HtmlCompat.fromHtml(
                    "<font color='#000000'>${title}</font>",
                    HtmlCompat.FROM_HTML_MODE_LEGACY
                )
            )
        }
    }
}
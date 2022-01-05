package com.example.ponggame

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class RankingListAdapter(private var users: ArrayList<User>) : RecyclerView.Adapter<RankingListAdapter.ViewHolder>() {

    private lateinit var storageReference: StorageReference

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user : User = users[position]
        holder.username.text = user.username
        holder.score.text = user.score.toString()
        holder.position.text = (position+1).toString()
        storageReference = FirebaseStorage.getInstance().reference.child("profile_pictures").child(user.uid.toString())
        val localFile = File.createTempFile("tempImage", "")
        storageReference.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            holder.image.setImageBitmap(bitmap)
        }
    }

    override fun getItemCount(): Int {
       return users.size
    }

    fun addItem(user : User) {
        users.add(user)
        notifyDataSetChanged()
    }

    class ViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        var username : TextView = itemView.findViewById(R.id.username_text_view)
        var score : TextView = itemView.findViewById(R.id.user_points)
        var position: TextView = itemView.findViewById(R.id.user_ranking_position)
        var image: CircleImageView = itemView.findViewById(R.id.user_image)
    }
}

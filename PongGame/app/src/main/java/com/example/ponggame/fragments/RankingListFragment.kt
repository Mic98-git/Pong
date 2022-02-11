package com.example.ponggame.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.RankingListAdapter
import com.example.ponggame.RetrofitClient
import com.example.ponggame.User
import com.example.ponggame.databinding.FragmentRankingListBinding
import kotlinx.coroutines.*
import retrofit2.HttpException


class RankingListFragment : Fragment() {
    private var _binding: FragmentRankingListBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val currentUserEmail: String =
        DatabaseImpl.getAuthInstance().currentUser?.email.toString()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentRankingListBinding.inflate(inflater, container, false)
        val view = binding.root
        setActivityTitle("Ranking List")
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = binding.rankingRecyclerView
        val users: ArrayList<User> = arrayListOf()
        val rankingListAdapter = RankingListAdapter(users, currentUserEmail)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = rankingListAdapter
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            showRankingList(rankingListAdapter)
        }
    }

    private suspend fun showRankingList(rankingListAdapter: RankingListAdapter) {
        var usersRetrieved: Boolean
        var rankingList: HashMap<String, User> = HashMap()
        withContext(Dispatchers.IO) {
            usersRetrieved = try {
                rankingList = RetrofitClient.instance.getUsersList()
                true
            } catch (exception: HttpException){
                false
            }
        }
        if (usersRetrieved) {
            for ((uid, user) in rankingList) {
                rankingListAdapter.addItem(user)
            }
        }
        else {
            Toast.makeText(
                context,
                "Error retrieving ranking list",
                Toast.LENGTH_SHORT
            ).show()
        }

    }
    /*
        DatabaseImpl.getUsersReference().addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    data.getValue(User::class.java)?.let { rankingListAdapter.addItem(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    context,
                    "Error retrieving ranking list",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle the up button here
        return NavigationUI.onNavDestinationSelected(item,
            requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

}
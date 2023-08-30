package com.kierman.lufanalezaco.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.FragmentUserListBinding
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo
import com.kierman.lufanalezaco.viewmodel.UserModel
import com.kierman.lufanalezaco.viewmodel.UserViewHolder

class UserListFragment : Fragment() {

    private lateinit var binding: FragmentUserListBinding
    private val firebaseRepo = FirebaseRepo()
    private lateinit var adapter: FirestoreRecyclerAdapter<UserModel, UserViewHolder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserListBinding.inflate(inflater, container, false)

        val options = FirestoreRecyclerOptions.Builder<UserModel>()
            .setQuery(firebaseRepo.getUsersCollection(), UserModel::class.java)
            .build()

        adapter = object : FirestoreRecyclerAdapter<UserModel, UserViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.user_list_item, parent, false)
                return UserViewHolder(itemView)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: UserModel) {
                holder.bind(model)
            }
        }

        binding.userListView.layoutManager = LinearLayoutManager(requireContext())
        binding.userListView.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}

package com.kierman.lufanalezaco.util

import UserModel
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kierman.lufanalezaco.databinding.UserListItemBinding

class UserListAdapter(
    private val userList: List<UserModel>,
    private val itemClickListener: ItemClickListener
) : RecyclerView.Adapter<UserListAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserListItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(private val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserModel) {
            binding.userNameTextView.text = user.name

            binding.root.setOnClickListener {
                itemClickListener.onItemClick(user.id) // Pass the document ID to the listener
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(documentId: String)
    }
}

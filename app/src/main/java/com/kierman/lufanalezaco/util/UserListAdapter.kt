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

    // Funkcja do znalezienia indeksu użytkownika z najlepszym wynikiem
    private fun findIndexOfBestUser(): Int {
        var bestIndex = -1
        var bestTime = Long.MAX_VALUE // Domyślnie ustawiamy na największą możliwą wartość

        for ((index, user) in userList.withIndex()) {
            val results = user.time
            if (results.isNotEmpty()) {
                val bestResult = results
                    .filter { it.isNotBlank() }
                    .minByOrNull { time ->
                        val parts = time.split(":")
                        val seconds = parts[0].toInt() * 60 + parts[1].toInt()
                        seconds
                    }

                bestResult?.let {
                    val timeParts = it.split(":")
                    val timeInSeconds = timeParts[0].toInt() * 60 + timeParts[1].toInt()
                    if (timeInSeconds < bestTime) {
                        bestTime = timeInSeconds.toLong()
                        bestIndex = index
                    }
                }
            }
        }

        return bestIndex
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = UserListItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position], position)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(private val binding: UserListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserModel, position: Int) {
            val isBestUser = position == findIndexOfBestUser()

            val userNameText = if (isBestUser) {
                "🥇${user.name}" // Dodać emotikonę tylko przed imieniem najlepszego użytkownika
            } else {
                user.name // Pozostali użytkownicy bez emotikony
            }

            binding.userNameTextView.text = userNameText

            binding.root.setOnClickListener {
                itemClickListener.onItemClick(user) // Pass the document ID to the listener
            }
        }
    }

    interface ItemClickListener {
        fun onItemClick(user: UserModel)
    }
}

@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import UserModel
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.databinding.ActivityChooseBinding
import com.kierman.lufanalezaco.util.UserListAdapter
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo

class ChooseActivity : AppCompatActivity(), UserListAdapter.ItemClickListener {

    private lateinit var binding: ActivityChooseBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var repo: FirebaseRepo


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        repo = FirebaseRepo()

        val userList = mutableListOf<UserModel>()
        adapter = UserListAdapter(userList, this)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = adapter

        repo.getUsers { users ->
            userList.clear()
            userList.addAll(users)
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemClick(documentId: String) {
        Toast.makeText(this, "Wybrano użytkownika o ID: $documentId", Toast.LENGTH_SHORT).show()
    }
}

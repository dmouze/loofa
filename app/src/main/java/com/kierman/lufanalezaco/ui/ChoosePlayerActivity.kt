@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import UserModel
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityChoosePlayerBinding
import com.kierman.lufanalezaco.util.UserListAdapter
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo

class ChoosePlayerActivity : AppCompatActivity(), UserListAdapter.ItemClickListener {

    private lateinit var binding: ActivityChoosePlayerBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var repo: FirebaseRepo

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChoosePlayerBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(binding.root)


        repo = FirebaseRepo()

        val newUser = findViewById<TextView>(R.id.createMenel)

        newUser.setOnClickListener{
            val intent = Intent(this,CreateActivity::class.java)
            startActivity(intent)
            finish()
        }

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

    override fun onItemClick(user: UserModel) {
        repo.getUsersCollection()
        val id = user.id
        val results = user.time
        val imie = user.name
        val intent = Intent(this, TimerActivity::class.java)
        intent.putExtra("user_name", imie)
        intent.putExtra("user_id",id)
        intent.putStringArrayListExtra("user_results", ArrayList(results))
        startActivity(intent)
        finish()
    }




}

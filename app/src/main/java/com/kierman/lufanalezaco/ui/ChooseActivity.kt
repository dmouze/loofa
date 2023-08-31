package com.kierman.lufanalezaco.ui

import UserListAdapter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.databinding.ActivityChooseBinding
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo

class ChooseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var repo: FirebaseRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicjalizacja FirebaseRepo
        repo = FirebaseRepo()

        // Pobranie listy użytkowników
        repo.getUsers { userList ->
            // Tworzenie i ustawianie adaptera
            adapter = UserListAdapter(userList)
            binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
            binding.userRecyclerView.adapter = adapter
        }
    }
}

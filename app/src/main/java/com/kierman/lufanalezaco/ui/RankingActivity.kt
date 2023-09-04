@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import UserModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityRankingBinding
import com.kierman.lufanalezaco.util.ResultsAdapter
import com.kierman.lufanalezaco.util.UserListAdapter
import com.kierman.lufanalezaco.viewmodel.FirebaseRepo

class RankingActivity : AppCompatActivity(), UserListAdapter.ItemClickListener {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var repo: FirebaseRepo

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(binding.root)



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

    override fun onItemClick(user: UserModel) {
        val results = user.time
        val imie = user.name
        showResultsDialog(results, imie)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showResultsDialog(results: List<String>, imie: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.custom_result_list)

        val window = dialog.window
        val attributes = window?.attributes

        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        attributes?.y = resources.getDimensionPixelSize(R.dimen.bottom_margin)

        val titleTextView = dialog.findViewById<TextView>(R.id.resultTitleTextView)
        titleTextView.text = "Wyniki wariaciny: $imie"

        val listView = dialog.findViewById<ListView>(R.id.resultListView)

        // Wyszukaj najlepszy wynik (najkrótszy czas)
        val bestResult = results // Usuń nulle z listy wyników
            .filter { it.isNotBlank() } // Usuń puste ciągi znaków
            .minByOrNull { time ->
                val parts = time.split(":")
                val seconds = parts[0].toInt() * 60 + parts[1].toInt()
                seconds
            }

        val otherResults = results // Usuń nulle z listy wyników
            .filter { it.isNotBlank() } // Usuń puste ciągi znaków
            .filter { it != bestResult } // Usuń najlepszy wynik
            .toMutableList()
            .asReversed()


        val bestResultWithEmoji = bestResult?.let { "🥇$it" } ?: ""
        val sortedResults = mutableListOf<String>()
        if (bestResult != null) {
            sortedResults.add(bestResultWithEmoji)
        }
        sortedResults.addAll(otherResults)

        val adapter = ResultsAdapter(sortedResults)
        listView.adapter = adapter

        dialog.show()
    }


}

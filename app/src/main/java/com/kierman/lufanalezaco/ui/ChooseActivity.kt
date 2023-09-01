@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui
import UserModel
import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityChooseBinding
import com.kierman.lufanalezaco.util.ResultsAdapter
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

    override fun onItemClick(user: UserModel) {
        val results = user.czas
        val imie = user.imie
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

        attributes?.y = resources.getDimensionPixelSize(R.dimen.bottom_margin)

        val titleTextView = dialog.findViewById<TextView>(R.id.resultTitleTextView)
        titleTextView.text = "Wyniki menela: $imie"

        val listView = dialog.findViewById<ListView>(R.id.resultListView)
        val adapter = ResultsAdapter(results)
        listView.adapter = adapter

        dialog.show()
    }

}

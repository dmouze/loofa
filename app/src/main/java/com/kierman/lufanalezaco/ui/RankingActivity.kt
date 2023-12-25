@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityRankingBinding
import com.kierman.lufanalezaco.util.ResultsAdapter
import com.kierman.lufanalezaco.util.UserListAdapter
import com.kierman.lufanalezaco.util.UserModel

class RankingActivity : AppCompatActivity(), UserListAdapter.ItemClickListener {

    private lateinit var binding: ActivityRankingBinding
    private lateinit var adapter: UserListAdapter
    private lateinit var databaseReference: DatabaseReference

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRankingBinding.inflate(layoutInflater)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        setContentView(binding.root)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference =
            firebaseDatabase.reference.child("menele") // Zmień na odpowiednią ścieżkę w swojej bazie danych

        val userList = mutableListOf<UserModel>()
        adapter = UserListAdapter(userList, this)

        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.userRecyclerView.adapter = adapter

        databaseReference.addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (userSnapshot in snapshot.children) {
                    val id = userSnapshot.key // Pobierz ID użytkownika
                    val name = userSnapshot.child("imie").getValue(String::class.java) // Pobierz imię użytkownika
                    val czasMap = userSnapshot.child("czas").getValue(object : GenericTypeIndicator<HashMap<String, Double>>() {})
                    val timeList = ArrayList(czasMap?.values ?: emptyList())
                    val user = UserModel(id, name, timeList) // Tworzenie UserModel z ID, imieniem i czasami
                    userList.add(user)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun onItemClick(user: UserModel) {
        val results = user.time
        val imie = user.name
        showResultsDialog(results, imie)
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showResultsDialog(results: List<Double>?, imie: String?) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Ukrycie tytułu, jeśli istnieje
        dialog.setContentView(R.layout.custom_result_list)

        val window = dialog.window
        val attributes = window?.attributes

        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        // Ustawienie tła dla okna dialogowego (możesz dostosować to do swojego tła)
        window?.setBackgroundDrawableResource(R.drawable.round_background_white)

        attributes?.y = resources.getDimensionPixelSize(R.dimen.bottom_margin)

        val titleTextView = dialog.findViewById<TextView>(R.id.resultTitleTextView)
        titleTextView.text = "Wyniki osoby: $imie"

        val listView = dialog.findViewById<ListView>(R.id.resultListView)

        // Wyszukaj najlepszy wynik (najkrótszy czas)
        val bestResult = results
            ?.filter { it > 0 } // Usuń nulle z listy wyników
            ?.minByOrNull { it }

        val otherResults = results
            ?.filter { it > 0 } // Usuń nulle z listy wyników
            ?.filter { it != bestResult } // Usuń najlepszy wynik
            ?.map { getTimeStringFromDouble(it) } // Konwertuj wynik na "ss:SSS"
            ?.toMutableList()
            ?.asReversed()

        val bestResultWithEmoji = bestResult?.let { "🥇${getTimeStringFromDouble(it)}" } ?: ""
        val sortedResults = mutableListOf<String>()
        if (bestResult != null) {
            sortedResults.add(bestResultWithEmoji)
        }
        if (otherResults != null) {
            sortedResults.addAll(otherResults)
        }

        val adapter = ResultsAdapter(sortedResults)
        listView.adapter = adapter

        dialog.show()
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val seconds = time.toInt()
        val milliseconds = ((time - seconds) * 1000).toInt()
        return String.format("%02d:%03d", seconds, milliseconds)
    }
}

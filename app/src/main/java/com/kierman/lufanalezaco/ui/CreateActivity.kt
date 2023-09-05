@file:Suppress("DEPRECATION")

package com.kierman.lufanalezaco.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kierman.lufanalezaco.R
import com.kierman.lufanalezaco.databinding.ActivityCreateBinding

class CreateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateBinding

    private lateinit var imie: EditText
    private lateinit var przycisk: Button

    private var db = Firebase.firestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_create
        )

        imie = findViewById(R.id.imie_menela)
        przycisk = findViewById(R.id.btn_add_menel)

        przycisk.setOnClickListener{
            val sImie = imie.text.toString().trim()
            val sCzas = ArrayList<String>()

            val userMap = hashMapOf(
                "imie" to sImie,
                "czas" to sCzas
             )

            db.collection("menele").add(userMap)
                .addOnSuccessListener { documentReference ->
                    val sId = documentReference.id
                    // Aktualizuj mapę, przypisując wygenerowane ID dokumentu
                    userMap["id"] = sId

                    // Aktualizuj dokument w bazie danych
                    documentReference.update(userMap as Map<String, Any>)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Menel dodany!", Toast.LENGTH_SHORT).show()
                            imie.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Wystąpił problem...", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener{
                    Toast.makeText(this,"Wystąpił problem...",Toast.LENGTH_SHORT).show()
                }
            val intent = Intent(this,ChoosePlayerActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

}
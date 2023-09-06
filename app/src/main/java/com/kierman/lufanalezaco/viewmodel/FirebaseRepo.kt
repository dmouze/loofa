package com.kierman.lufanalezaco.viewmodel

import UserModel
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class FirebaseRepo {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()


    fun getUsersCollection(): Query {
        return firestore.collection("menele")
    }

    fun getUsers(callback: (List<UserModel>) -> Unit) {
        getUsersCollection().get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userList: MutableList<UserModel> = mutableListOf()
                for (document in task.result!!) {
                    val name = document.getString("imie") ?: ""
                    val time = document.get("czas") as? List<String> ?: emptyList()
                    val id = document.getString("id") ?: ""
                    val menel = UserModel(id, name, time)
                    userList.add(menel)
                }
                callback(userList)
            } else {
                Log.d("error", "Failed to retrieve data")
            }
        }
    }
}
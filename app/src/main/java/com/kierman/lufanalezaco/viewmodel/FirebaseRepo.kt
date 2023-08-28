package com.kierman.lufanalezaco.viewmodel

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
                    val userName = document.getString("imie") ?: ""
                    val user = UserModel(userName)
                    userList.add(user)
                }
                callback(userList)
            } else {
                Log.d("bład","nie udało się pobrać danych")
            }
        }
    }
}
package com.example.loofa.util

import android.widget.Toast
import com.example.loofa.LufaApp


class Util {
    companion object{
        fun showNotification(msg: String) {
            Toast.makeText(LufaApp.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}
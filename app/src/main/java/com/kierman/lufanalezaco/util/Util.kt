package com.kierman.lufanalezaco.util

import android.widget.Toast
import com.kierman.lufanalezaco.MyApplication

class Util {
    companion object{
        fun showNotification(msg: String) {
            Toast.makeText(MyApplication.applicationContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }
}
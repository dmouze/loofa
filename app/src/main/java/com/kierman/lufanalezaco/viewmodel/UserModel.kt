package com.kierman.lufanalezaco.viewmodel

import com.google.firebase.firestore.PropertyName

data class UserModel(
    @get:PropertyName("imie") @set:PropertyName("imie") var imie: String = "",
) {
    constructor() : this("")
}
package com.example.contactsharetest.modelTest

import android.net.Uri

data class ContactData(
    val name: String,
    val phoneNumber: String,
    val email: String,
    val imageUri: Uri?
)
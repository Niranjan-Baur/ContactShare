package com.example.contactsharetest.modelTest

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ContactStore{

//    private var _contact by mutableStateOf(ContactData("","","",null))
//    val contact : ContactData get() = _contact

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ContactStore")

        private val nameKey = stringPreferencesKey("Name")
        private val phoneNumberKey = stringPreferencesKey("PhoneNumber")
        private val emailKey = stringPreferencesKey("Email")
        private val imageUriKey = stringPreferencesKey("ImageUri")
    }


    fun getContact(context: Context) : Flow<ContactData> {
        return context.dataStore.data.catch { exception ->

        }.map {
                preferences ->
            ContactData(
                name = preferences[nameKey] ?: "",
                phoneNumber = preferences[phoneNumberKey] ?: "",
                email = preferences[emailKey] ?: "",
                imageUri = preferences[imageUriKey]?.let { Uri.parse(it) }
            )
        }

    }

    suspend fun updateContact(
        name: String? = null,
        phoneNumber: String? = null,
        email: String? = null,
        imageUri: Uri? = null,
        context: Context
    ) {
            context.dataStore.edit{ preferences ->
                name?.let { preferences[nameKey] = it }
                phoneNumber?.let { preferences[phoneNumberKey] = it }
                email?.let { preferences[emailKey] = it }
//                imageUri?.let { preferences[imageUriKey] = it.toString() }
                imageUri?.let { uri ->
                    preferences[imageUriKey] = uri.toString()
                } ?: preferences.remove(imageUriKey)
            }

    }
}
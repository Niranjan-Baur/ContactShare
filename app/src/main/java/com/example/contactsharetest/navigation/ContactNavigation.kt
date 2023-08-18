package com.example.contactsharetest.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.contactsharetest.HomePage
import com.example.contactsharetest.modelTest.ContactScreen
import com.example.contactsharetest.modelTest.ContactViewModel
import com.example.contactsharetest.pages.EditDetailsPage

@Composable
fun ContactNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current


    NavHost(navController = navController,
        startDestination = ContactScreen.HomePage.name){
        composable(ContactScreen.HomePage.name){
            // call the SplashScreen here
            HomePage(navController)
        }
        composable(ContactScreen.EditDetailsPage.name){
            EditDetailsPage()
        }
    }
}

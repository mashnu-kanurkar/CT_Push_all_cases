package com.example.ctpushallcases

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class MainActivityViewModel: ViewModel() {
    fun saveLoginCredentials(email: String, context: Context){
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putBoolean("isLoggedIn", true)
        editor.apply()
    }

    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    fun loggedInUsingEmail(context: Context): String? {
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("Login", Context.MODE_PRIVATE)
        return sharedPreferences.getString("email", "")
    }
}
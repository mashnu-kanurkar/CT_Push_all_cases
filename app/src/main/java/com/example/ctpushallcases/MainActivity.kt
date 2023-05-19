package com.example.ctpushallcases

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clevertap.android.sdk.CleverTapAPI


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var editText: EditText
    private lateinit var loginButton: Button
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var clevertapDefaultInstance: CleverTapAPI? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        editText = findViewById(com.example.ctpushallcases.R.id.editTextTextEmailAddress)
        loginButton = findViewById(R.id.login_button)
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(
            applicationContext
        )

        loginButton.setOnClickListener{
            login()
        }
        CleverTapAPI.createNotificationChannel(applicationContext,"general",
            "General","General notifications",
            NotificationManager.IMPORTANCE_MAX,true)
    }

    override fun onStart() {
        super.onStart()
        val isLoggedIn = mainActivityViewModel.isUserLoggedIn(applicationContext)
        var userEmail = mainActivityViewModel.loggedInUsingEmail(applicationContext)
        Log.d(TAG, "isUserLoggedIn: $isLoggedIn")
        Log.d(TAG, "Email: $userEmail")
        if (isLoggedIn ){
            goToNext()
        }
    }
    fun String.isEmailValid(): Boolean {
        return !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun login(){
        val email = editText.text.toString().trim()
        if (!email.isEmailValid()){
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
            return
        }
        mainActivityViewModel.saveLoginCredentials(email, applicationContext)
        val identity = email.split("@")[0]
        val profile = mapOf<String, String>("Email" to email, "Identity" to identity)
        clevertapDefaultInstance?.onUserLogin(profile)
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
        clevertapDefaultInstance?.pushEvent("Login successful", mapOf("Email" to email))
        goToNext()
    }

    private fun goToNext(){
        startActivity(Intent(this@MainActivity, AllPushTypesActivity::class.java))
    }


}
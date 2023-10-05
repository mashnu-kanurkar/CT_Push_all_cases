package com.example.ctpushallcases

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.clevertap.android.pushtemplates.PTConstants
import com.clevertap.android.sdk.CTInboxListener
import com.clevertap.android.sdk.CleverTapAPI


class MainActivity : AppCompatActivity(), CTInboxListener {
    private val TAG = "MainActivity"
    private lateinit var editText: EditText
    private lateinit var loginButton: Button
    private lateinit var editTextEventName: EditText
    private lateinit var pushEventButton: Button
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private var clevertapDefaultInstance: CleverTapAPI? = null
    private lateinit var inboxButton: Button

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            NotificationUtils.dismissNotification(intent, applicationContext)
        }
        val extras = intent?.extras
        CleverTapAPI.processPushNotification(applicationContext,extras);
        if (extras != null) {
            for(key in extras.keySet()){
                Log.d(TAG, "$key: ${extras[key]}")
            }
        }
        Log.d(TAG, "onNewIntent extras: ${extras.toString()}")
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Created main activity")
        setContentView(R.layout.activity_main)
        mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        editText = findViewById(com.example.ctpushallcases.R.id.editTextTextEmailAddress)
        loginButton = findViewById(R.id.login_button)
        editTextEventName = findViewById(com.example.ctpushallcases.R.id.editTextEventName)
        pushEventButton = findViewById(R.id.event_push_button)
        inboxButton = findViewById(R.id.inbox_button)
        clevertapDefaultInstance = CleverTapAPI.getDefaultInstance(
            applicationContext
        )

        loginButton.setOnClickListener{
            login()
        }
        pushEventButton.setOnClickListener {
            pushEvent()
        }

        inboxButton.setOnClickListener {
            startActivity(Intent(this, AppInbox::class.java))
        }

        clevertapDefaultInstance?.initializeInbox()
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

    private fun pushEvent(){
        try {
            val eventName = editTextEventName.text.toString().trim()
            if (eventName.isNotEmpty()){
                clevertapDefaultInstance?.pushEvent(eventName)
            }
        }catch (e: Exception){
            Toast.makeText(this, "Event name exception", Toast.LENGTH_SHORT).show()
        }

    }

    private fun goToNext(){
        finish()
        startActivity(Intent(this@MainActivity, AllPushTypesActivity::class.java))
    }

    override fun inboxDidInitialize() {
        var count  = clevertapDefaultInstance?.inboxMessageCount;
        println("count: $count")
        if (count != null){
            inboxButton.setText("Notifications ($count)")
        }

    }

    override fun inboxMessagesDidUpdate() {
        println("inboxMessagesDidUpdate")
    }


}

object NotificationUtils {

    //Require to close notification on action button click
    fun dismissNotification(intent: Intent?, applicationContext: Context){
        intent?.extras?.apply {
            var autoCancel = true
            var notificationId = -1

            getString("actionId")?.let {
                Log.d("ACTION_ID", it)
                autoCancel = getBoolean("autoCancel", true)
                notificationId = getInt("notificationId", -1)
            }
            /**
             * If using InputBox template, add ptDismissOnClick flag to not dismiss notification
             * if pt_dismiss_on_click is false in InputBox template payload. Alternatively if normal
             * notification is raised then we dismiss notification.
             */
            val ptDismissOnClick = intent.extras!!.getString(PTConstants.PT_DISMISS_ON_CLICK,"")

            if (autoCancel && notificationId > -1 && ptDismissOnClick.isNullOrEmpty()) {
                val notifyMgr: NotificationManager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notifyMgr.cancel(notificationId)
            }
        }
    }
}
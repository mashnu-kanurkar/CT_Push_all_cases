package com.example.ctpushallcases

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.sdk.CleverTapAPI
import com.clevertap.android.sdk.displayunits.DisplayUnitListener
import com.clevertap.android.sdk.displayunits.model.CleverTapDisplayUnit

class AllPushTypesActivity : AppCompatActivity(), DisplayUnitListener {

    private lateinit var spinner: Spinner
    private lateinit var pushEventButton: Button
    private lateinit var pushTypes: Array<String>
    private lateinit var selectedType: String
    private lateinit var logOutButton: Button
    private lateinit var cleverTapDefaultInstance: CleverTapAPI
    private val TAG = "AllPushTypesActivity"

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
        setContentView(R.layout.activity_all_push_types)
        Log.d(TAG, "AllPushTypesActivity: onCreate")
        spinner = findViewById(R.id.spinner)
        pushEventButton = findViewById(R.id.push_event_button)
        logOutButton = findViewById(R.id.log_out_button)

        cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)!!

        pushTypes = resources.getStringArray(R.array.Push_Types)
        selectedType = pushTypes[0]
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pushTypes)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedType = pushTypes[position]
                Log.d(TAG, "on Item selected invoked")
                Toast.makeText(this@AllPushTypesActivity, "Selected push type: ${pushTypes[position]}", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.d(TAG, "nothing selected invoked")
                selectedType = pushTypes[0]
            }
        }
        cleverTapDefaultInstance?.apply {
            setDisplayUnitListener(this@AllPushTypesActivity)
        }

        pushEventButton.setOnClickListener{
            pushEvent()
        }

        logOutButton.setOnClickListener{

        }
    }

    private fun logOut(){
        val sharedPreferences: SharedPreferences = applicationContext.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", "")
        editor.putBoolean("isLoggedIn", false)
        editor.apply()
        Toast.makeText(this@AllPushTypesActivity, "Logout successful", Toast.LENGTH_SHORT).show()
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushEvent("Logout successful")
        startActivity(Intent(this@AllPushTypesActivity, MainActivity::class.java))
        finish()
    }

    private fun pushEvent(){
        cleverTapDefaultInstance.pushEvent("Push Event", mapOf("type" to selectedType))
        Toast.makeText(this@AllPushTypesActivity, "Pushed event: Push Event with prop -> type: $selectedType", Toast.LENGTH_SHORT).show()
    }

    override fun onDisplayUnitsLoaded(units: ArrayList<CleverTapDisplayUnit>?) {
        for (i in 0 until units!!.size) {
            val unit = units[i]
            //prepareDisplayView(unit)
        }
    }
}
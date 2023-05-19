package com.example.ctpushallcases

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.clevertap.android.sdk.CleverTapAPI

class AllPushTypesActivity : AppCompatActivity() {

    private lateinit var spinner: Spinner
    private lateinit var pushEventButton: Button
    private lateinit var pushTypes: Array<String>
    private lateinit var selectedType: String
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_push_types)
        spinner = findViewById(R.id.spinner)
        pushEventButton = findViewById(R.id.push_event_button)
        logOutButton = findViewById(R.id.log_out_button)

        pushTypes = resources.getStringArray(R.array.Push_Types)
        selectedType = pushTypes[0]
        if (spinner !=null){
            val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pushTypes)
            spinner.adapter = adapter

            spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedType = pushTypes[position]
                    Toast.makeText(this@AllPushTypesActivity, "Selected push type: ${pushTypes[position]}", Toast.LENGTH_SHORT).show()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    selectedType = pushTypes[0]
                }
            }
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
        CleverTapAPI.getDefaultInstance(applicationContext)?.pushEvent("Push Event", mapOf("type" to selectedType))
        Toast.makeText(this@AllPushTypesActivity, "Pushed event: Push Event with prop -> type: $selectedType", Toast.LENGTH_SHORT).show()
    }
}
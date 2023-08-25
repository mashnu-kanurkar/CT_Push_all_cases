package com.example.ctpushallcases

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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
    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var cleverTapDefaultInstance: CleverTapAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_push_types)
        spinner = findViewById(R.id.spinner)
        pushEventButton = findViewById(R.id.push_event_button)
        logOutButton = findViewById(R.id.log_out_button)
        image1 = findViewById(R.id.imageView2)
        image2 = findViewById(R.id.imageView3)
        image3 = findViewById(R.id.imageView4)

        val cleverTapDefaultInstance = CleverTapAPI.getDefaultInstance(applicationContext)

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
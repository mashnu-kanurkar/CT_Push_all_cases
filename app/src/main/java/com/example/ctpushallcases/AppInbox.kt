package com.example.ctpushallcases

import android.graphics.drawable.ShapeDrawable
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.clevertap.android.sdk.CleverTapAPI


class AppInbox : AppCompatActivity() {
    private var cleverTapAPI : CleverTapAPI? = null
    private lateinit var linearLayout : LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_inbox)
        cleverTapAPI = CleverTapAPI.getDefaultInstance(applicationContext)
        linearLayout = findViewById(R.id.inbox_linear_view)
        linearLayout.orientation = LinearLayout.VERTICAL

        val allmessages = cleverTapAPI?.allInboxMessages;
        if (allmessages != null) {
            println("All messages: ${allmessages.size}")
            for (message in allmessages){
                println("message: ${message.messageId}")
                var inboxMessage = cleverTapAPI?.getInboxMessageForId(message.messageId);
                var textViewTitle = TextView(this)
                var textViewBody = TextView(this)
                linearLayout.addView(textViewTitle)
                linearLayout.addView(textViewBody)

                println("message:title ${inboxMessage?.title}")
                println("message: ${inboxMessage?.body}")
                val contentList = inboxMessage?.inboxMessageContents

                println("message: ${inboxMessage?.data}")

                if(contentList != null){
                    for (content in contentList){
                        println("message:title ${content.toString()}")
                    }
                }

                val divider = ShapeDrawable()
                divider.intrinsicHeight = 8
                divider.intrinsicWidth = 50

                linearLayout.dividerDrawable = divider
                linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
            }
        }else{
            var textView = TextView(this)
            linearLayout.addView(textView)
            textView.setText("No messages")
        }
    }

}
package com.instabox.bjsimulator

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_broadcast.*

class BroadcastListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boradcast_list)

        initializingWidgets()

    }

    fun initializingWidgets(){

        broadcastBackButton.setOnClickListener { finish() }

    }

}
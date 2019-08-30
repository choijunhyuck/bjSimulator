package com.instabox.bjsimulator

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.instabox.bjsimulator.start.NewStationActivity

class SplashActivity : AppCompatActivity() {

    lateinit var preference : SharedPreferences

    var SPLASH_TIME_OUT:Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            preference = getSharedPreferences("simulator", 0)
            if(preference.getInt("category", -1) != -1){
                //haveStation
                startActivity(Intent(this,MainActivity::class.java))
            } else {
                //noStation
                startActivity(Intent(this,NewStationActivity::class.java))
            }

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)

    }

}
package com.instabox.bjsimulator.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.instabox.bjsimulator.MainActivity
import com.instabox.bjsimulator.R
import com.instabox.bjsimulator.func.GetDateFunc
import kotlinx.android.synthetic.main.activity_new_category.*
import java.util.*
import java.text.SimpleDateFormat

class NewCategoryActivity : AppCompatActivity() {

    var CATEGORY:Int = 0

    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_category)

        preferences = getSharedPreferences("simulator", 0)

        initializingWidgets()
    }

    fun initializingWidgets(){

        Toast.makeText(this, "현재는 게임만 지원됩니다", Toast.LENGTH_LONG).show()

        /*
        newCategorySelect.setOnClickListener {

            if(CATEGORY == 0){
                newCategorySelect.setText("캠")
                newCategoryPreview.setText("캠")
                CATEGORY = 1
            } else {
                newCategorySelect.setText("게임")
                newCategoryPreview.setText("게임")
                CATEGORY = 0
            }

        }
        */

        newCategoryCompleteBtn.setOnClickListener {

            //SET STANDARD VARIABLES
            var nowDate = GetDateFunc.getDateTime()

            preferences.edit().putInt("category", CATEGORY).apply()
            Log.d("TAG", "카테고리 : "+CATEGORY.toString())

            preferences.edit().putString("createDate", nowDate).apply()
            Log.d("TAG", "개설일자 : "+ nowDate)

            preferences.edit().putInt("broadcastValue", 100).apply()
            preferences.edit().putInt("broadcastPopularity", 100).apply()
            preferences.edit().putInt("totalRecommand", 0).apply()
            preferences.edit().putInt("totalBroadcastTime", 0).apply()
            preferences.edit().putInt("totalViewer", 0).apply()
            preferences.edit().putInt("loveViewer", 0).apply()
            preferences.edit().putInt("fan", 0).apply()
            preferences.edit().putInt("simulyCoin", 0).apply()
            preferences.edit().putInt("cashCoin", 5000).apply()
            preferences.edit().putInt("stress", 0).apply()
            preferences.edit().putInt("hp", 100).apply()
            preferences.edit().putInt("full", 100).apply()
            preferences.edit().putInt("chairLevel", 1).apply()
            preferences.edit().putInt("lightLevel", 1).apply()
            preferences.edit().putInt("equipmentLevel", 1).apply()
            preferences.edit().putInt("interiorLevel", 1).apply()

            startActivity(Intent(this,MainActivity::class.java))

            finish()
            NewStationActivity.activity.finish()

        }

    }

}
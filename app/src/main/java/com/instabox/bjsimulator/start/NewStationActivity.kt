package com.instabox.bjsimulator.start

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.instabox.bjsimulator.R
import kotlinx.android.synthetic.main.activity_new_station.*

class NewStationActivity : AppCompatActivity() {

    companion object {

        lateinit var activity: NewStationActivity

    }

    lateinit var preference : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_station)

        activity = this
        preference = getSharedPreferences("simulator", 0)

        initializingWidgets()

    }

    fun initializingWidgets(){

        //NEW STATION TITLE EDIT
        newStationEdit.addTextChangedListener(object: TextWatcher {override fun afterTextChanged(s: Editable?) {

        }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                newStationPreview.setText(s)

            }

        })

        newStationCompleteBtn.setOnClickListener {

            if(newStationEdit.text.length > 0){
                Log.d("TAG", "INPUT TITLE : " + newStationEdit.text.toString())
                preference.edit().putString("stationTitle", newStationEdit.text.toString()).apply()

                startActivity(Intent(this,NewCategoryActivity::class.java))

            } else {
                Toast.makeText(this, "방송국 이름을 입력해주세요!", Toast.LENGTH_LONG).show()
            }

        }

    }

}
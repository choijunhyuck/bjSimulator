package com.instabox.bjsimulator

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.instabox.bjsimulator.adapter.ChatListAdapter
import com.instabox.bjsimulator.database.RankingDatabaseHelper
import com.instabox.bjsimulator.database.VodDatabaseHelper
import com.instabox.bjsimulator.func.GetDateFunc
import com.instabox.bjsimulator.item.ChatItem
import kotlinx.android.synthetic.main.activity_broadcast.*

class BroadcastActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences

    var COUNT_BACK = 0
    var UNIQUE_REACTION_TIMER = 0
    var NORMAL_REACTION_TIMER = 0
    var ISBROADCAST = false

    var ISOPENBFIRSTBUTTON = false
    var ISOPENBSECONDBUTTON = false
    var ISOPENBTHIRDBUTTON = false
    var ISOPENBFOURTHBUTTON = false

    var ISNORMALREACTIONACTIVATE = false
    var ISUNIQUEREACTIONACTIVATE = false

    var ISEVENTACTIVATE = false
    var ISLIVEITEMACTIVATE = false

    var broadcastTitle = ""
    var broadcastTime = 0
    var viewer = 0
    var nowScreen = ""

    var chatModel = ArrayList<ChatItem>()
    val chatAdapter = ChatListAdapter(this, chatModel)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        preferences = getSharedPreferences("simulator", 0)
        broadcastChatView.adapter = chatAdapter

        initializingWidgets()
    }

    fun initializingWidgets(){

        //SET STATE
        broadcastHPState.text = "체력 "+preferences.getInt("hp", 100).toString()
        broadcastFullState.text = "포만감 "+preferences.getInt("full", 100).toString()
        broadcastStressState.text = "스트레스 "+preferences.getInt("stress", 0).toString()

        //SET STATE INDICATOR
        if(preferences.getInt("hp", 100) < 31){
            broadcastHPIndicator.setBackgroundResource(R.drawable.round_red)
        }else if(preferences.getInt("hp", 100) > 30){
            broadcastHPIndicator.setBackgroundResource(R.drawable.round_green)
        }
        if(preferences.getInt("full", 100) < 31){
            broadcastFullIndicator.setBackgroundResource(R.drawable.round_red)
        }else if(preferences.getInt("full", 100) > 30){
            broadcastFullIndicator.setBackgroundResource(R.drawable.round_green)
        }
        if(preferences.getInt("stress", 100) > 69){
            broadcastStressIndicator.setBackgroundResource(R.drawable.round_red)
        }else if(preferences.getInt("stress", 100) < 70){
            broadcastStressIndicator.setBackgroundResource(R.drawable.round_green)
        }

        broadcastThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 1000).toString()

        //SET REACTION
        broadcastNormalReactionUseButton.setOnClickListener {

            if(ISBROADCAST){

                if(!ISNORMALREACTIONACTIVATE && !ISUNIQUEREACTIONACTIVATE){
                    if(!ISNORMALREACTIONACTIVATE && ISBROADCAST && !ISUNIQUEREACTIONACTIVATE){

                        if(preferences.getInt("stress", 0) < 90){
                            //REFRESH CASH COIN
                            preferences.edit().putInt("stress", preferences.getInt("stress", 0)+10).apply()
                            //FUNCTION
                            preferences.edit().putInt("nowSimulyCoin", (preferences.getInt("nowSimulyCoin", 0)*1.2).toInt()).apply()
                            Toast.makeText(this, "기본리액션을 사용했습니다.", Toast.LENGTH_LONG).show()

                            ISNORMALREACTIONACTIVATE = true
                            NORMAL_REACTION_TIMER = 30

                            broadcastNormalReactionUseButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                            broadcastNormalReactionUseButton.text = "사용중"

                            val reaction = resources.getStringArray(R.array.normalReaction)
                            val reactionRnds = (0..reaction.size - 1).random()

                            broadcastReactionView.text = reaction[reactionRnds]
                            broadcastReactionTimer.text = "00:30"
                            broadcastReactionWindow.visibility = View.VISIBLE

                        }else{
                            Toast.makeText(this, "스트레스가 많아서 사용할 수 없습니다", Toast.LENGTH_LONG).show()
                        }

                    }else{
                        Toast.makeText(this, "기본리액션을 사용 중입니다", Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(this, "리액션을 사용 중입니다", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "방송을 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            }

        }

        broadcastUniqueReactionUseButton.setOnClickListener {

            if(ISBROADCAST){

                if(!ISNORMALREACTIONACTIVATE && !ISUNIQUEREACTIONACTIVATE){

                    if(preferences.getInt("stress", 0) < 80){
                        //REFRESH CASH COIN
                        preferences.edit().putInt("stress", preferences.getInt("stress", 0)+20).apply()
                        //FUNCTION
                        preferences.edit().putInt("nowSimulyCoin", (preferences.getInt("nowSimulyCoin", 0)*1.3).toInt()).apply()
                        Toast.makeText(this, "고급리액션을 사용했습니다.", Toast.LENGTH_LONG).show()

                        ISUNIQUEREACTIONACTIVATE = true
                        UNIQUE_REACTION_TIMER = 60

                        broadcastUniqueReactionUseButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                        broadcastUniqueReactionUseButton.text = "사용중"

                        val reaction = resources.getStringArray(R.array.uniqueReaction)
                        val reactionRnds = (0..reaction.size - 1).random()

                        broadcastReactionView.text = reaction[reactionRnds]
                        broadcastReactionTimer.text = "00:60"
                        broadcastReactionWindow.visibility = View.VISIBLE

                    }else{
                        Toast.makeText(this, "스트레스가 많아서 사용할 수 없습니다", Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(this, "리액션을 사용 중입니다", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "방송을 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            }

        }

        //BUY CASH & SET ITEM
        broadcastBuyCash.setOnClickListener {

        }

        broadcastUseEventButton.setOnClickListener {

            if(ISBROADCAST){

                if(preferences.getInt("cashCoin", 1000) > 3000){
                    //REFRESH CASH COIN
                    preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-3000).apply()
                    broadcastThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 0)
                    //FUNCTION
                    preferences.edit().putInt("livePopularity", preferences.getInt("livePopularity", 0)+50).apply()
                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 0)
                    Toast.makeText(this, "이벤트를 시작했습니다.", Toast.LENGTH_LONG).show()

                    ISLIVEITEMACTIVATE = true

                    broadcastUseEventButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                    broadcastUseEventButton.text = "사용됨"
                    broadcastUseEventButton.setOnClickListener {
                        Toast.makeText(this, "이미 아이템을 사용중입니다.", Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "방송이 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            }

        }

        broadcastUseLiveItemButton.setOnClickListener {

            if(ISBROADCAST){

                if(preferences.getInt("cashCoin", 1000) > 7000){
                    //REFRESH CASH COIN
                    preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-7000).apply()
                    broadcastThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 0)
                    //FUNCTION
                    preferences.edit().putInt("livePopularity", preferences.getInt("livePopularity", 0)+100).apply()
                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 0)
                    Toast.makeText(this, "생방송 홍보를 시작했습니다.", Toast.LENGTH_LONG).show()

                    ISLIVEITEMACTIVATE = true

                    broadcastUseLiveItemButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                    broadcastUseLiveItemButton.text = "사용됨"
                    broadcastUseLiveItemButton.setOnClickListener {
                        Toast.makeText(this, "이미 아이템을 사용중입니다.", Toast.LENGTH_LONG).show()
                    }

                }else{
                    Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(this, "방송이 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
            }

        }

        //START BROADCAST BUTTON
        broadcastStartButton.setOnClickListener {

            if(!ISBROADCAST) {

                if(preferences.getInt("hp", 100) != 0 &&
                    preferences.getInt("full", 100) != 0 &&
                    preferences.getInt("stress", 0) != 100) {

                    broadcastStartButton.setBackgroundResource(R.drawable.broadcast_asset_onair_activate)
                    broadcast()
                    ISBROADCAST = true

                } else {
                    Toast.makeText(this, "상태를 회복한 후 방송을 시작하세요.", Toast.LENGTH_LONG).show()
                }

            } else {
                //UPLOAD VOD?
                if(broadcastTime >= 15*60 && !preferences.getBoolean("todayVodUploaded", false)){
                    var dbHandler = VodDatabaseHelper(this, null)
                    dbHandler.addVod(nowScreen, broadcastTitle, broadcastTime, GetDateFunc.getDateTime())
                    Toast.makeText(this, "VOD가 업로드되었습니다. 오늘은 더이상 업로드할 수 없습니다.", Toast.LENGTH_LONG).show()
                    preferences.edit().putBoolean("todayVodUploaded", true).apply()
                }
                //SCREEN INITIALIZING
                broadcastScreen.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))
                broadcastStartButton.setBackgroundResource(R.drawable.broadcast_asset_onair_deactivate)
                ISBROADCAST = false
                //END BROADCAST
                broadcastTitleView.isEnabled = true
                broadcastTitleView.setText("방송제목")
                //INFORMATION SEND
                preferences.edit().putInt("totalBroadcastTime", preferences.getInt("totalBroadcastTime", 0)+broadcastTime).apply()
                preferences.edit().putInt("broadcastPopularity", preferences.getInt("broadcastPopularity", 0)+(preferences.getInt("livePopularity", 0)*0.1).toInt()).apply()
                preferences.edit().putInt("totalRecommand", preferences.getInt("totalRecommand", 0)+preferences.getInt("nowRecommand", 0)).apply()
                preferences.edit().putInt("totalViewer", preferences.getInt("totalViewer", 0)+preferences.getInt("nowViewer", 5)+preferences.getInt("nowFan", 0)/3).apply()
                preferences.edit().putInt("loveViewer", preferences.getInt("loveViewer", 0)+preferences.getInt("nowLoveViewer", 0)).apply()
                preferences.edit().putInt("fan", preferences.getInt("fan", 0)+preferences.getInt("nowFan", 0)/3).apply()
                preferences.edit().putInt("simulyCoin", preferences.getInt("simulyCoin", 0)+preferences.getInt("nowSimulyCoin", 0)).apply()
                //INFORMATION CLEAR
                preferences.edit().putInt("livePopularity", 0).apply()
                preferences.edit().putInt("nowRecommand", 0).apply()
                preferences.edit().putInt("nowViewer", 0).apply()
                preferences.edit().putInt("nowLoveViewer", 0).apply()
                preferences.edit().putInt("nowFan", 0).apply()
                preferences.edit().putInt("nowSimulyCoin", 0).apply()
                //TIME CLEAR
                broadcastTime = 0
                //RE-INTIALIZING TEXT
                broadcastTimeView.text = "0 : 00 : 00"
                broadcastViewer.text = "시청자 : 0명"
                broadcastInf1.text = "실시간 인기도 : 0"
                broadcastInf3.text = "받은 시뮬리 코인 : 0개"
                broadcastInf4.text = "획득한 애청자 : 0"
                broadcastInf5.text = "획득한 팬 : 0"
                broadcastInf6.text = "생방송 순위 : 0"
                broadcastInf7.text = "추천 수 : 0"
                //RE-INITIALIZING ITEM BUTTON
                ISEVENTACTIVATE = false
                ISLIVEITEMACTIVATE = false

                broadcastUseEventButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                broadcastUseEventButton.text = "사용"
                broadcastUseEventButton.setOnClickListener {

                    if(ISBROADCAST){

                        if(preferences.getInt("cashCoin", 1000) > 3000){
                            //REFRESH CASH COIN
                            preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-3000).apply()
                            broadcastThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 0)
                            //FUNCTION
                            preferences.edit().putInt("livePopularity", preferences.getInt("livePopularity", 0)+50).apply()
                            broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 0)
                            Toast.makeText(this, "이벤트를 시작했습니다.", Toast.LENGTH_LONG).show()

                            ISLIVEITEMACTIVATE = true

                            broadcastUseEventButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                            broadcastUseEventButton.text = "사용됨"
                            broadcastUseEventButton.setOnClickListener {
                                Toast.makeText(this, "이미 아이템을 사용중입니다.", Toast.LENGTH_LONG).show()
                            }

                        }else{
                            Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
                        }

                    }else{
                        Toast.makeText(this, "방송이 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                    }

                }

                broadcastUseLiveItemButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                broadcastUseLiveItemButton.text = "사용"
                broadcastUseLiveItemButton.setOnClickListener {

                    if(ISBROADCAST){

                        if(preferences.getInt("cashCoin", 1000) > 7000){
                            //CASH COIN REFRESH
                            preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-7000).apply()
                            broadcastThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 0)
                            //FUNCTION
                            preferences.edit().putInt("livePopularity", preferences.getInt("livePopularity", 0)+100).apply()
                            broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 0)
                            Toast.makeText(this, "생방송 홍보를 시작했습니다.", Toast.LENGTH_LONG).show()

                            broadcastUseLiveItemButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                            broadcastUseLiveItemButton.text = "사용됨"
                            broadcastUseLiveItemButton.setOnClickListener {
                                Toast.makeText(this, "이미 아이템을 사용중입니다.", Toast.LENGTH_LONG).show()
                            }

                        }else{
                            Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
                        }

                    }else{
                        Toast.makeText(this, "방송이 시작한 후 사용할 수 있습니다.", Toast.LENGTH_LONG).show()
                    }

                }

            }//else END

        }

        //BOTTOM BUTTON
        broadcastBFirstButton.setOnClickListener {

            if(!ISOPENBFIRSTBUTTON){
                broadcastBFirstButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = true
                ISOPENBSECONDBUTTON = true
                ISOPENBTHIRDBUTTON = true
                ISOPENBFOURTHBUTTON = true

                broadcastBSecondButton.callOnClick()
                broadcastBThirdButton.callOnClick()
                broadcastBFourthButton.callOnClick()

            } else {
                broadcastBFirstButtonLayout.visibility = View.GONE
                ISOPENBFIRSTBUTTON = false
            }

        }

        broadcastBSecondButton.setOnClickListener{

            if(!ISOPENBSECONDBUTTON){
                broadcastBSecondButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = true
                ISOPENBSECONDBUTTON = true
                ISOPENBTHIRDBUTTON = true
                ISOPENBFOURTHBUTTON = true

                broadcastBFirstButton.callOnClick()
                broadcastBThirdButton.callOnClick()
                broadcastBFourthButton.callOnClick()

            } else {
                broadcastBSecondButtonLayout.visibility = View.GONE
                ISOPENBSECONDBUTTON = false
            }

        }

        broadcastBThirdButton.setOnClickListener{

            if(!ISOPENBTHIRDBUTTON){
                broadcastBThirdButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = true
                ISOPENBSECONDBUTTON = true
                ISOPENBTHIRDBUTTON = true
                ISOPENBFOURTHBUTTON = true

                broadcastBFirstButton.callOnClick()
                broadcastBSecondButton.callOnClick()
                broadcastBFourthButton.callOnClick()

            } else {
                broadcastBThirdButtonLayout.visibility = View.GONE
                ISOPENBTHIRDBUTTON = false
            }

        }

        broadcastBFourthButton.setOnClickListener{

            if(!ISOPENBFOURTHBUTTON){
                broadcastBFourthButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = true
                ISOPENBSECONDBUTTON = true
                ISOPENBTHIRDBUTTON = true
                ISOPENBFOURTHBUTTON = true

                broadcastBFirstButton.callOnClick()
                broadcastBSecondButton.callOnClick()
                broadcastBThirdButton.callOnClick()

            } else {
                broadcastBFourthButtonLayout.visibility = View.GONE
                ISOPENBFOURTHBUTTON = false
            }

        }

        broadcastBackButton.setOnClickListener { onBackPressed() }

    }

    fun broadcast(){

        broadcastTitle = broadcastTitleView.text.toString()
        broadcastTitleView.isEnabled = false

        //INITIAL SCREEN
        broadcastScreen.setBackgroundResource(R.drawable.broadcast_asset_screen_1)

        //INITIAL VIEWER
        viewer = (0..10).random()
        preferences.edit().putInt("nowViewer", viewer).apply()
        broadcastViewer.text = "시청자 : "+viewer.toString()+"명"

        //SET LIVE POPULARITY
        preferences.edit().putInt("livePopularity", 0).apply()

        //START CHAT
        chatModel.add(ChatItem("알림","채팅이 시작되었습니다."))
        chatAdapter.notifyDataSetChanged()

        val user = resources.getStringArray(R.array.user)
        val sentence = resources.getStringArray(R.array.chats)

            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    if (ISBROADCAST) {

                        //SCREEN
                        if(broadcastTime%210 == 0){
                            //2-11
                            nowScreen = "broadcast_asset_screen_" + (2..10).random()
                            var resId = getResources().getIdentifier(nowScreen, "drawable", getPackageName());
                            broadcastScreen.setBackgroundResource(resId)
                        }else if(broadcastTime%36 == 0){
                            //2-9
                            nowScreen = "broadcast_asset_screen_" + (2..8).random()
                            var resId = getResources().getIdentifier(nowScreen, "drawable", getPackageName());
                            broadcastScreen.setBackgroundResource(resId)
                        }

                        //CHAT
                        val userRnds = (0..user.size - 1).random()
                        val sentendceRnds = (0..sentence.size - 1).random()
                        chatModel.add(ChatItem(user[userRnds],sentence[sentendceRnds]))
                        chatAdapter.notifyDataSetChanged()

                        //TIMER
                        broadcastTime += 3
                        broadcastTimeView.setText((broadcastTime / 3600).toString() + " : "+(broadcastTime % 3600 / 60).toString() + " : "+(broadcastTime % 3600 % 60).toString())
                        /*
                        if(broadcastTime/60 >= 60){
                            broadcastTimeView.setText((broadcastTime/3600).toString()+" : "+((broadcastTime-3600)/60).toString()+" : "+((broadcastTime-3600)%60).toString())
                        }else if(broadcastTime/60 > 0 && broadcastTime/60 >= 10){
                            broadcastTimeView.setText("0 : "+(broadcastTime/60).toString()+" : "+(broadcastTime%60).toString())
                        }else if(broadcastTime/60 > 0 && broadcastTime/60 < 10){
                            broadcastTimeView.setText("0 : "+"0"+(broadcastTime/60).toString()+" : "+(broadcastTime%60).toString())
                        }else if(broadcastTime >= 10){
                            broadcastTimeView.setText("0 : 00 : "+broadcastTime.toString())
                        }else{
                            broadcastTimeView.setText("0 : 00 : "+"0"+broadcastTime.toString())
                        }
                        */

                        //VIEWER
                        if(broadcastTime%30 == 0){

                            var broadcastPopularity = preferences.getInt("broadcastPopularity", 0)

                            if(broadcastPopularity < 500){

                                viewer = (5..10).random()

                                preferences.edit().putInt("nowViewer", viewer).apply()
                                broadcastViewer.text = "시청자 : "+viewer.toString()+"명"

                            }else{

                                if(preferences.getInt("livePoularity", 0) > 0){
                                    viewer = (5..(
                                            (preferences.getInt("broadcastPopularity", 10)*0.05).toInt()
                                            +(preferences.getInt("livePoularity", 0)*0.05).toInt()
                                            )
                                            ).random()

                                }else{
                                    viewer = (5..(preferences.getInt("broadcastPopularity", 10)*0.1).toInt()).random()
                                }

                                preferences.edit().putInt("nowViewer", viewer).apply()
                                broadcastViewer.text = "시청자 : "+viewer.toString()+"명"

                            }

                        }

                        //LIVE POPULARITY
                        if(broadcastTime%30 == 0){

                            if(preferences.getInt("nowRecommand", 0) != 0){

                                if(ISLIVEITEMACTIVATE){
                                    var livePopularity = preferences.getInt("nowRecommand", 5)*(preferences.getInt("nowViewer", 0)*0.3).toInt()
                                    preferences.edit().putInt("livePopularity", livePopularity+100).apply()
                                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 5).toString()
                                }else{
                                    var livePopularity = preferences.getInt("nowRecommand", 5)*(preferences.getInt("nowViewer", 0)*0.3).toInt()
                                    preferences.edit().putInt("livePopularity", livePopularity).apply()
                                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 5).toString()
                                }

                            }else{

                                if(ISLIVEITEMACTIVATE){
                                    var livePopularity = (preferences.getInt("nowViewer", 5)*0.3).toInt()
                                    preferences.edit().putInt("livePopularity", livePopularity+100).apply()
                                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 5).toString()
                                }else{
                                    var livePopularity = (preferences.getInt("nowViewer", 5)*0.3).toInt()
                                    preferences.edit().putInt("livePopularity", livePopularity).apply()
                                    broadcastInf1.text = "실시간 인기도 : "+preferences.getInt("livePopularity", 5).toString()
                                }
                            }
                        }

                        //STATE
                        if(broadcastTime%60 == 0) {
                            //HP
                            preferences.edit().putInt("hp", preferences.getInt("hp", 100) - 5).apply()
                            broadcastHPState.text = "체력 "+preferences.getInt("hp", 100).toString()
                            //INDICATOR
                            if(preferences.getInt("hp", 100) < 31){
                                broadcastHPIndicator.setBackgroundResource(R.drawable.round_red)
                            } else {
                                broadcastHPIndicator.setBackgroundResource(R.drawable.round_green)
                            }
                            //INDICATE 0
                            if(preferences.getInt("hp", 100) <= 0){
                                preferences.edit().putInt("hp", 0).apply()
                                broadcastStartButton.callOnClick()
                                Toast.makeText(applicationContext,"체력이 한계에 도달했습니다",Toast.LENGTH_LONG).show()
                            }

                            //FULL
                            preferences.edit().putInt("full", preferences.getInt("full", 100) - 3).apply()
                            broadcastFullState.text = "포만감 "+preferences.getInt("full", 100).toString()
                            //INDICATOR
                            if(preferences.getInt("full", 100) < 31){
                                broadcastFullIndicator.setBackgroundResource(R.drawable.round_red)
                            } else {
                                broadcastFullIndicator.setBackgroundResource(R.drawable.round_green)
                            }
                            //INDICATE 0
                            if(preferences.getInt("full", 100) <= 0){
                                preferences.edit().putInt("full", 0).apply()
                                broadcastStartButton.callOnClick()
                                Toast.makeText(applicationContext,"배고픔이 한계에 도달했습니다.",Toast.LENGTH_LONG).show()
                            }

                            //STRESS
                            preferences.edit().putInt("stress", preferences.getInt("stress", 0) + 2).apply()
                            broadcastStressState.text = "스트레스 "+preferences.getInt("stress", 0).toString()
                            //INDICATOR
                            if(preferences.getInt("stress", 0) > 69){
                                broadcastStressIndicator.setBackgroundResource(R.drawable.round_red)
                            } else {
                                broadcastStressIndicator.setBackgroundResource(R.drawable.round_green)
                            }
                            //INDICATE 0
                            if(preferences.getInt("stress", 0) >= 100){
                                preferences.edit().putInt("stress", 100).apply()
                                broadcastStartButton.callOnClick()
                                Toast.makeText(applicationContext,"스트레스가 한계에 도달했습니다",Toast.LENGTH_LONG).show()
                            }
                        }

                        //RECOMMAND
                        if(sentence[sentendceRnds].substring(0,2) == "추천"){
                            preferences.edit().putInt("nowRecommand", preferences.getInt("nowRecommand", 0)+1).apply()
                            preferences.edit().putInt("nowLoveViewer", preferences.getInt("nowLoveViewer", 0)+1).apply()
                        }
                        broadcastInf7.text = "추천수 : "+preferences.getInt("nowRecommand",0).toString()
                        broadcastInf4.text = "획득한 애청자 : "+preferences.getInt("nowLoveViewer",0).toString()

                        //SIMULYCOIN
                        if(sentence[sentendceRnds].substring(0,2) == "코인"){
                            preferences.edit().putInt("nowSimulyCoin", preferences.getInt("nowSimulyCoin", 0)+removeStringNumber(sentence[sentendceRnds]).toInt()).apply()
                            preferences.edit().putInt("nowFan", preferences.getInt("nowFan", 0)+1).apply()

                            val dbHandler =
                                RankingDatabaseHelper(applicationContext, null)
                            dbHandler.addUser(user[userRnds],removeStringNumber(sentence[sentendceRnds]).toInt())
                        }
                        broadcastInf3.text = "받은 시뮬리 코인 : " + preferences.getInt("nowSimulyCoin",0).toString()+ "개"
                        broadcastInf5.text = "획득한 팬 : "+(preferences.getInt("nowFan",0)/2).toString()

                        //REACTION
                        if(ISNORMALREACTIONACTIVATE && NORMAL_REACTION_TIMER != 0){

                            NORMAL_REACTION_TIMER -= 3

                            if(NORMAL_REACTION_TIMER > 9){
                                broadcastReactionTimer.text = "00:"+NORMAL_REACTION_TIMER.toString()
                            }else{
                                broadcastReactionTimer.text = "00:0"+NORMAL_REACTION_TIMER.toString()
                            }

                        }else if(ISNORMALREACTIONACTIVATE && NORMAL_REACTION_TIMER == 0){

                            ISNORMALREACTIONACTIVATE = false
                            broadcastReactionWindow.visibility = View.GONE
                            broadcastNormalReactionUseButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                            broadcastNormalReactionUseButton.text = "사용"

                        }

                        if(ISUNIQUEREACTIONACTIVATE && UNIQUE_REACTION_TIMER != 0){

                            UNIQUE_REACTION_TIMER -= 3

                            if(NORMAL_REACTION_TIMER > 9){
                                broadcastReactionTimer.text = "00:"+UNIQUE_REACTION_TIMER.toString()
                            }else{
                                broadcastReactionTimer.text = "00:0"+UNIQUE_REACTION_TIMER.toString()
                            }

                        }else if(ISUNIQUEREACTIONACTIVATE && UNIQUE_REACTION_TIMER == 0){

                            ISUNIQUEREACTIONACTIVATE = false
                            broadcastReactionWindow.visibility = View.GONE
                            broadcastUniqueReactionUseButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                            broadcastUniqueReactionUseButton.text = "사용"

                        }


                        //LOOP
                        handler.postDelayed(this, 500)

                    } else {
                        chatModel.add(ChatItem("알림", "채팅이 종료되었습니다."))
                        chatAdapter.notifyDataSetChanged()
                    }
                }
            }, 500)

    }


    fun removeStringNumber(str: String): String {
        return str.replace("[^0-9]".toRegex(), "")
    }

    override fun onBackPressed() {

        if(ISBROADCAST) {
            if (COUNT_BACK == 0) {
                Toast.makeText(this, "방송을 정상적으로 종료하지 않으면 인기도가 떨어집니다.", Toast.LENGTH_LONG).show()
                COUNT_BACK = 1
            } else if (COUNT_BACK == 1) {
                preferences.edit().putInt("broadcastPopularity", preferences.getInt("broadcastPopularity", 100)-20).apply()
                startActivity(Intent(this, MainActivity::class.java))
                MainActivity.activity.finish()
                finish()
                //ABNORMAL END BROADCAST
            }
        } else {
            startActivity(Intent(this, MainActivity::class.java))
            MainActivity.activity.finish()
            finish()
        }

    }

}
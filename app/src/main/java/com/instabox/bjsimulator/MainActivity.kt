package com.instabox.bjsimulator

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alomteamd.honbapteamd.func.BitmapConvertFunc
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.instabox.bjsimulator.adapter.DonationListAdapter
import com.instabox.bjsimulator.adapter.TubeListAdapter
import com.instabox.bjsimulator.adapter.VodListAdapter
import com.instabox.bjsimulator.database.RankingDatabaseHelper
import com.instabox.bjsimulator.database.TubeDatabaseHelper
import com.instabox.bjsimulator.database.VodDatabaseHelper
import com.instabox.bjsimulator.func.GetDateFunc
import com.instabox.bjsimulator.item.DonationItem
import com.instabox.bjsimulator.item.TubeItem
import com.instabox.bjsimulator.item.VodItem
import com.instabox.bjsimulator.start.NewStationActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {

        lateinit var activity: MainActivity

    }

    lateinit var preferences: SharedPreferences

    private val GALLERY = 1
    private val CAMERA = 2

    var TITLE:String = ""
    var CATEGORY:Int = -1
    var OPENDATE:String = ""

    var ISOPENBFIRSTBUTTON = 0
    var ISOPENBSECONDBUTTON = 0
    var ISOPENBTHIRDBUTTON = 0
    var ISOPENBFOURTHBUTTON = 0

    var ISOPENVODSETTINGLAYOUT = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activity = this
        preferences = getSharedPreferences("simulator", 0)

        checkDay()

    }

    fun checkDay(){

        if(preferences.getString("today", "null") == "null"){
            preferences.edit().putString("today", GetDateFunc.getDateTime()).apply()
            initializingWidgets()
        }else{

            if(preferences.getString("today", "null") != GetDateFunc.getDateTime()){
                /**NEWDAY**/
                preferences.edit().putBoolean("todayVodUploaded", false).apply()
                preferences.edit().putBoolean("VODCalculated", false).apply()
                preferences.edit().putBoolean("tubeCalculated", false).apply()
                preferences.edit().putInt("todayVodPlay", 0).apply()
                preferences.edit().putInt("todayVodRecommand", 0).apply()
                preferences.edit().putInt("todayTubePlay", 0).apply()

                preferences.edit().putString("today", GetDateFunc.getDateTime()).apply()

                //VOD PLAY & RECOMMAND
                var broadcastPopularity = preferences.getInt("broadcastPopularity", 100)

                var dbHandler = VodDatabaseHelper(this, null)
                if(dbHandler.getVOD()!!.moveToLast()){

                    var c = dbHandler.getVOD()!!
                    c.moveToFirst()

                    if(c.moveToFirst()) {

                        do {

                            var oriPlay = c.getInt(c.getColumnIndex(VodDatabaseHelper.COLUMN_PLAY))
                            var oriRecommand = c.getInt(c.getColumnIndex(VodDatabaseHelper.COLUMN_RECOMMAND))

                            var newPlay = (0..(broadcastPopularity*0.01).toInt()).random()
                            var newRecommand = (0..(broadcastPopularity*0.01).toInt()).random()

                            preferences.edit().putInt("todayVodPlay", preferences.getInt("todayVodPlay", 0)+newPlay).apply()
                            preferences.edit().putInt("todayVodRecommand", preferences.getInt("todayVodRecommand", 0)+newRecommand).apply()

                            dbHandler.updatePlayAndRecommand(oriPlay+newPlay,
                                                             oriRecommand+newRecommand,
                                                              c.getString(c.getColumnIndex(VodDatabaseHelper.COLUMN_DATE)))

                        } while (c.moveToNext())

                        c.close()
                    }

                }

                //TUBE PLAY & RECOMMAND
                var tubeDbHandler = TubeDatabaseHelper(this, null)
                if(tubeDbHandler.getTube()!!.moveToLast()){

                    var c = tubeDbHandler.getTube()!!
                    c.moveToFirst()

                    if(c.moveToFirst()) {

                        do {

                            var oriPlay = c.getInt(c.getColumnIndex(TubeDatabaseHelper.COLUMN_PLAY))

                            var newPlay = (0..(broadcastPopularity*0.01).toInt()).random()

                            preferences.edit().putInt("todayTubePlay", preferences.getInt("todayTubePlay", 0)+newPlay).apply()

                            tubeDbHandler.updatePlay(oriPlay+newPlay,
                                c.getString(c.getColumnIndex(VodDatabaseHelper.COLUMN_DATE)))

                        } while (c.moveToNext())

                        c.close()
                    }

                }

            }

            initializingWidgets()

        }

    }

    fun initializingWidgets(){

        //PROFILE SETTING
        if(preferences.getString("profilePicture","null") != "null"){
            Glide.with(activity)
                .load(BitmapConvertFunc.decoder(preferences.getString("profilePicture","null")))
                .apply(RequestOptions.circleCropTransform())
                .into(mainProfileImage);
        }
        mainProfileImage.setOnClickListener { showPictureDialog() }

        //VIEW SETTING
        TITLE = preferences.getString("stationTitle", "ERROR")!!
        CATEGORY = preferences.getInt("category", -1)
        OPENDATE = preferences.getString("createDate", "ERROR")!!

        //CHECK ERROR
        if(TITLE == "ERROR" || CATEGORY == -1 || OPENDATE == "ERROR"){
            Toast.makeText(this, "데이터를 불러올 수 없습니다", Toast.LENGTH_LONG).show()
            preferences.edit().clear().apply()
            startActivity(Intent(this,NewStationActivity::class.java))
            finish()
        }

        mainTitleView.setText(TITLE+"님의 방송국")
        mainNameView.setText(TITLE)
        mainLoveViewer.text = "애청자 : "+preferences.getInt("loveViewer", 0)
        mainFanViewer.text = "팬 : "+preferences.getInt("fan", 0)
        mainDateView.setText("방송국 개설 일자 : "+OPENDATE)

        //INITIALIZING FIRST LAYOUT
        initializingFirstLayout()

        //TOP BUTTON
        mainFirstButton.setOnClickListener {
            mainFirstLayout.visibility = View.VISIBLE
            mainSecondLayout.visibility = View.GONE
            mainThirdLayout.visibility = View.GONE
            mainFourthLayout.visibility = View.GONE
            mainFifthLayout.visibility = View.GONE

            mainFirstButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            mainSecondButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainThirdButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFourthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFifthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
        }

        mainSecondButton.setOnClickListener{

            if(preferences.getBoolean("isFirstVod", true)){
                Toast.makeText(this, "방송시간이 15분 이상이면 VOD가 업로드됩니다.", Toast.LENGTH_LONG).show()
                preferences.edit().putBoolean("isFirstVod", false).apply()
            }

            initializingSecondLayout()

            mainFirstLayout.visibility = View.GONE
            mainSecondLayout.visibility = View.VISIBLE
            mainThirdLayout.visibility = View.GONE
            mainFourthLayout.visibility = View.GONE
            mainFifthLayout.visibility = View.GONE

            mainFirstButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainSecondButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            mainThirdButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFourthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFifthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))

        }

        mainThirdButton.setOnClickListener{
            mainFirstLayout.visibility = View.GONE
            mainSecondLayout.visibility = View.GONE
            mainThirdLayout.visibility = View.VISIBLE
            mainFourthLayout.visibility = View.GONE
            mainFifthLayout.visibility = View.GONE

            mainFirstButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainSecondButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainThirdButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            mainFourthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFifthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))

            initializingThirdLayout()
        }

        mainFourthButton.setOnClickListener{
            mainFirstLayout.visibility = View.GONE
            mainSecondLayout.visibility = View.GONE
            mainThirdLayout.visibility = View.GONE
            mainFourthLayout.visibility = View.VISIBLE
            mainFifthLayout.visibility = View.GONE

            mainFirstButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainSecondButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainThirdButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFourthButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            mainFifthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))

            //CLOSE BROADCAST BUTTON
            mainCloseBroadcastButton.setOnClickListener {
                //DATABASE CLEAR
                var db1 = RankingDatabaseHelper(this, null)
                var db2 = TubeDatabaseHelper(this, null)
                var db3 = VodDatabaseHelper(this, null)
                db1.deleteAll()
                db2.deleteAll()
                db3.deleteAll()

                //PREFERENCES CLEAR
                preferences.edit().clear().apply()
                startActivity(Intent(this,SplashActivity::class.java))
                finish()
            }

            //BROADCAST INFORMATION
            preferences.edit().putInt("broadcastValue", 100+(preferences.getInt("loveViewer", 0)/20)*80
                    +(preferences.getInt("fan", 0)/50)*30).apply()
            mainBroadcastValue.text = "방송국 가치 : "+preferences.getInt("broadcastValue", 0).toString()
            mainBroadcastPopularity.text = "방송국 인기도 : "+(preferences.getInt("broadcastPopularity", 0)+preferences.getInt("loveViewer", 0)*0.5+preferences.getInt("fan", 0)*0.5).toInt().toString()
            mainTotalRecommand.text = "총 추천수 : "+preferences.getInt("totalRecommand", 0).toString()
            mainTotalViewer.text = "누적 시청자 : "+preferences.getInt("totalViewer", 0).toString()

            /*BROADCAST TIME*/
            var broadcastTime = preferences.getInt("totalBroadcastTime", 0)
            mainTotalBroadcastTime.text = "총 방송시간 : "+(broadcastTime / 3600)+"시간 "+(broadcastTime % 3600 / 60)+"분 "+(broadcastTime % 3600 % 60) + "초"

            //BROADCAST INFORMATION RANK
            setRanks()
        }

        mainFifthButton.setOnClickListener{
            mainFirstLayout.visibility = View.GONE
            mainSecondLayout.visibility = View.GONE
            mainThirdLayout.visibility = View.GONE
            mainFourthLayout.visibility = View.GONE
            mainFifthLayout.visibility = View.VISIBLE

            mainFirstButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainSecondButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainThirdButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFourthButton.setTextColor(ContextCompat.getColor(this, R.color.colorGrey))
            mainFifthButton.setTextColor(ContextCompat.getColor(this, R.color.colorRed))

            initializingFifthLayout()
        }

        //BOTTOM BUTTON
        mainBFirstButton.setOnClickListener {

            if(ISOPENBFIRSTBUTTON == 0){
                mainBFirstButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = 1
                ISOPENBSECONDBUTTON = 1
                ISOPENBTHIRDBUTTON = 1
                ISOPENBFOURTHBUTTON = 1

                mainBSecondButton.callOnClick()
                mainBThirdButton.callOnClick()
                mainBFourthButton.callOnClick()

            } else {
                mainBFirstButtonLayout.visibility = View.GONE
                ISOPENBFIRSTBUTTON = 0
            }

            //STATE
            mainHPState.text = "체력 : " + preferences.getInt("hp", 100).toString()
            mainFullState.text = "포만감 : " + preferences.getInt("full", 100).toString()
            mainStressState.text = "스트레스 : " + preferences.getInt("stress", 0).toString()

            if(preferences.getInt("hp", 100) < 31){
                mainHPIndicator.setBackgroundResource(R.drawable.round_red)
            } else if(preferences.getInt("hp", 100) > 31){
                mainHPIndicator.setBackgroundResource(R.drawable.round_green)
            }

            if(preferences.getInt("full", 100) < 31){
                mainFullIndicator.setBackgroundResource(R.drawable.round_red)
            } else if(preferences.getInt("full", 100) > 31){
                mainFullIndicator.setBackgroundResource(R.drawable.round_green)
            }

            if(preferences.getInt("stress", 0) > 69){
                mainStressIndicator.setBackgroundResource(R.drawable.round_red)
            } else if(preferences.getInt("stress", 100) < 70){
                mainStressIndicator.setBackgroundResource(R.drawable.round_green)
            }

        }

        mainBSecondButton.setOnClickListener{

            if(ISOPENBSECONDBUTTON == 0){
                mainBSecondButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = 1
                ISOPENBSECONDBUTTON = 1
                ISOPENBTHIRDBUTTON = 1
                ISOPENBFOURTHBUTTON = 1

                mainBFirstButton.callOnClick()
                mainBThirdButton.callOnClick()
                mainBFourthButton.callOnClick()

            } else {
                mainBSecondButtonLayout.visibility = View.GONE
                ISOPENBSECONDBUTTON = 0
            }

            //FINANCE
            mainCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 1000).toString()
            mainSimulyView.text = "시뮬리코인 : "+preferences.getInt("simulyCoin", 1000).toString()

        }

        mainBThirdButton.setOnClickListener{

            if(ISOPENBTHIRDBUTTON == 0){
                mainBThirdButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = 1
                ISOPENBSECONDBUTTON = 1
                ISOPENBTHIRDBUTTON = 1
                ISOPENBFOURTHBUTTON = 1

                mainBFirstButton.callOnClick()
                mainBSecondButton.callOnClick()
                mainBFourthButton.callOnClick()

            } else {
                mainBThirdButtonLayout.visibility = View.GONE
                ISOPENBTHIRDBUTTON = 0
            }

            //STORE
            setStoreWidget()

        }

        mainBFourthButton.setOnClickListener{

            if(ISOPENBFOURTHBUTTON == 0){
                mainBFourthButtonLayout.visibility = View.VISIBLE

                ISOPENBFIRSTBUTTON = 1
                ISOPENBSECONDBUTTON = 1
                ISOPENBTHIRDBUTTON = 1
                ISOPENBFOURTHBUTTON = 1

                mainBFirstButton.callOnClick()
                mainBSecondButton.callOnClick()
                mainBThirdButton.callOnClick()

            } else {
                mainBFourthButtonLayout.visibility = View.GONE
                ISOPENBFOURTHBUTTON = 0
            }

            //EXCHANGE TEXT
            mainExchangeSimulyView.text = "시뮬리코인 : "+ preferences.getInt("simulyCoin", 1000).toString()
            mainExchangeCashView.text = "캐시코인 : "+ ((preferences.getInt("simulyCoin", 1000)*60)/100).toString()
        }

        mainBFifthButton.setOnClickListener{

            ISOPENBFIRSTBUTTON = 1
            ISOPENBSECONDBUTTON = 1
            ISOPENBTHIRDBUTTON = 1
            ISOPENBFOURTHBUTTON = 1

            mainBFirstButton.callOnClick()
            mainBSecondButton.callOnClick()
            mainBThirdButton.callOnClick()
            mainBFourthButton.callOnClick()
            startActivity(Intent(this,BroadcastListActivity::class.java))

        }

        //MONEY EXCHANGE BUTTON
        mainExchangeButton.setOnClickListener {

            if(preferences.getInt("simulyCoin", 1000) != 0) {
                preferences.edit().putInt(
                    "cashCoin",
                    preferences.getInt("cashCoin", 1000) + preferences.getInt("simulyCoin", 1000) * 60 / 100
                ).apply()
                preferences.edit().putInt("simulyCoin", 0).apply()
                Toast.makeText(this, "환전되었습니다!", Toast.LENGTH_LONG).show()

                mainExchangeSimulyView.text = "시뮬리코인 : 0"
                mainExchangeCashView.text = "캐시코인 : 0"
            } else {
                Toast.makeText(this, "시뮬리코인이 없습니다", Toast.LENGTH_LONG).show()
            }

        }

        //START BROADCAST BUTTON
        mainBroadcastStartButton.setOnClickListener {

            startActivity(Intent(this,BroadcastActivity::class.java))

        }

    }

    fun showPictureDialog() {

        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("프로필 설정하기")
        val pictureDialogItems = arrayOf("갤러리에서 사진 선택", "카메라", "기본이미지로 변경")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
                2 -> setProfilePictureInitial()
            }
        }
        pictureDialog.show()

    }

    fun choosePhotoFromGallary() {

        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, GALLERY)

    }

    fun takePhotoFromCamera() {

        //여기서 권한을 요청해야함

        //

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)

    }

    fun setProfilePictureInitial(){

        //프로필을 기본 이미지로 설정함
        mainProfileImage.setImageResource(R.drawable.hub_asset_profile_picture_initial)

        //기본 이미지하면 DB에서 profile_url을 null로 만듬

    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/

        if (requestCode == GALLERY)
        {
            if (data != null)
            {
                val contentURI = data!!.data

                try
                {
                    val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)

                    if (bitmap.getWidth() >= bitmap.getHeight()){

                        var dstBmp = Bitmap.createBitmap(
                            bitmap,
                            bitmap.getWidth()/2 - bitmap.getHeight()/2,
                            0,
                            bitmap.getHeight(),
                            bitmap.getHeight()
                        );

                        Glide.with(activity)
                            .load(dstBmp)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mainProfileImage);

                    }else{

                        var dstBmp = Bitmap.createBitmap(
                            bitmap,
                            0,
                            bitmap.getHeight()/2 - bitmap.getWidth()/2,
                            bitmap.getWidth(),
                            bitmap.getWidth()
                        );

                        Glide.with(activity)
                            .load(dstBmp)
                            .apply(RequestOptions.circleCropTransform())
                            .into(mainProfileImage);

                    }

                    //가져온 사진을 String 형태로 Preferences에 저장
                    var editor = preferences.edit()
                    editor.putString("profilePicture", BitmapConvertFunc.encoder(bitmap)).apply()

                }
                catch (e: IOException) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, "Failed!", Toast.LENGTH_SHORT).show()
                }


            }

        }

        else if (requestCode == CAMERA)
        {
            val bitmap = data!!.extras!!.get("data") as Bitmap //tumbnail
            Glide.with(activity)
                .load(bitmap)
                .apply(RequestOptions.circleCropTransform())
                .into(mainProfileImage);

            //가져온 사진을 String 형태로 Preferences에 저장
            var editor = preferences.edit()
            editor.putString("profilePicture", BitmapConvertFunc.encoder(bitmap)).apply()
        }

    }

    fun initializingFirstLayout(){

        var donationModel = ArrayList<DonationItem>()

        val donationAdapter = DonationListAdapter(this, donationModel)

        mainFirstLayout.adapter = donationAdapter

        var dbHandler = RankingDatabaseHelper(this, null)

        if(dbHandler.get10User()!!.moveToLast()){

            var c = dbHandler.get10User()!!
            c.moveToFirst()

            if(c.moveToFirst()) {

                var i = 1

                do {
                    Log.d("TAG", "유저 : "+c.getString(c.getColumnIndex(RankingDatabaseHelper.COLUMN_USER))
                            +" 코인 : "+c.getString(c.getColumnIndex(RankingDatabaseHelper.COLUMN_COIN)))

                    donationModel.add(DonationItem(i.toString()+"등",
                        c.getString(c.getColumnIndex(RankingDatabaseHelper.COLUMN_USER)),
                        c.getString(c.getColumnIndex(RankingDatabaseHelper.COLUMN_COIN))))
                    i++

                } while (c.moveToNext())

                donationAdapter.notifyDataSetChanged()
                c.close()
            }

        }

    }

    fun initializingSecondLayout(){

        var vodModel = ArrayList<VodItem>()

        val vodAdapter = VodListAdapter(this, vodModel)

        mainSecondLayout.adapter = vodAdapter

        var dbHandler = VodDatabaseHelper(this, null)

        if(dbHandler.getVOD()!!.moveToLast()){

            var c = dbHandler.getVOD()!!
            c.moveToFirst()

            if(c.moveToFirst()) {

                do {

                    var thumbnail = c.getString(c.getColumnIndex(VodDatabaseHelper.COLUMN_THUMBNAIL))
                    var title = c.getString(c.getColumnIndex(VodDatabaseHelper.COLUMN_TITLE))
                    var time = c.getInt(c.getColumnIndex(VodDatabaseHelper.COLUMN_TIME))
                    var play = c.getInt(c.getColumnIndex(VodDatabaseHelper.COLUMN_PLAY))
                    var recommand = c.getInt(c.getColumnIndex(VodDatabaseHelper.COLUMN_RECOMMAND))
                    var date = c.getString(c.getColumnIndex(VodDatabaseHelper.COLUMN_DATE))

                    vodModel.add(VodItem(thumbnail,title,time,play,recommand,date))

                } while (c.moveToNext())

                vodAdapter.notifyDataSetChanged()
                c.close()
            }

        }

        vodSettingLayout.setOnClickListener{
            vodSettingLayout.visibility = View.GONE
        }

        mainSecondLayout.setOnItemClickListener { parent, view, position, id ->

            if(ISOPENVODSETTINGLAYOUT == 0){

                vodSettingLayout.visibility = View.VISIBLE

                vodUploadMyTubeButton.setOnClickListener{

                    var tubeDbHandler = TubeDatabaseHelper(activity, null)

                    if(!tubeDbHandler.isExistVod(vodModel[position].date)!!.moveToLast()){
                        tubeDbHandler.addTube(vodModel[position].screen, vodModel[position].title, vodModel[position].time, GetDateFunc.getDateTime(), vodModel[position].date)
                        Toast.makeText(activity, "VOD가 마이튜브에 업로드되었습니다.", Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(activity, "이미 업로드된 VOD입니다.", Toast.LENGTH_LONG).show()
                    }

                    vodSettingLayout.visibility = View.GONE

                }

                vodDeleteVodButton.setOnClickListener {

                    if(vodModel[position].date == GetDateFunc.getDateTime()){
                        preferences.edit().putBoolean("todayVodUploaded", false).apply()
                    }

                    var vodDbHandler = VodDatabaseHelper(activity,null)
                    vodDbHandler.deleteVod(vodModel[position].date)
                    vodModel.removeAt(position)

                    vodSettingLayout.visibility = View.GONE

                    vodAdapter.notifyDataSetChanged()

                }

            }else{

                vodSettingLayout.visibility = View.GONE

            }

        }

    }

    fun initializingThirdLayout(){

        //SET VOD PLAY
        var vodPlayCount = 0
        var vodRecommandCount = 0
        var calculatedVodSimulyCoin = 0

        vodPlayCount = preferences.getInt("todayVodPlay", 0)
        vodRecommandCount = preferences.getInt("todayVodRecommand", 0)

        calculatedVodSimulyCoin = (vodPlayCount/10+vodRecommandCount/10)*100

        //SET VOD TEXT
        mainCalculateVODPlayView.text = "신규 재생수 : "+vodPlayCount.toString()
        mainCalculateVODRecommandView.text = "신규 추천수 : "+vodRecommandCount.toString()
        mainCalculateVODSimulyView.text = calculatedVodSimulyCoin.toString()

        //SET VOD WIDGETS
        if(preferences.getBoolean("VODCalculated", false)){
            //ALREADY VOD CALCULATE
            mainCalculateVODButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
            mainCalculateVODButton.setText("정산됨")
            mainCalculateVODButton.setOnClickListener {}

        } else if(!preferences.getBoolean("VODCalculated", false)){
            //NEED CALCULATE
            if(calculatedVodSimulyCoin > 0){
                mainCalculateVODButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                mainCalculateVODButton.setText("정산")

                mainCalculateVODButton.setOnClickListener {

                    preferences.edit().putInt("simulyCoin", preferences.getInt("simulyCoin", 1000)+calculatedVodSimulyCoin).apply()
                    mainCalculateVODButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                    mainCalculateVODButton.setText("정산됨")
                    mainCalculateVODButton.setOnClickListener {}

                    preferences.edit().putBoolean("VODCalculated", true).apply()

                }

            }else{
                mainCalculateVODButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                mainCalculateVODButton.setText("정산")
                mainCalculateVODButton.setOnClickListener {}
            }

        }

        //GET TUBE PLAY
        var tubePlayCount = 0
        var calculatedTubeSimulyCoin = 0

        tubePlayCount = preferences.getInt("todayTubePlay", 0)

        calculatedTubeSimulyCoin = (tubePlayCount/30)*100

        //SET VOD TEXT
        mainCalculateTubePlayView.text = "신규 재생수 : "+tubePlayCount.toString()
        mainCalculateTubeSimulyView.text = calculatedTubeSimulyCoin.toString()

        //SET TUBE WIDGETS
        if(preferences.getBoolean("tubeCalculated", false)){
            //ALREADY TUBE CALCULATED
            mainCalculateTubeButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
            mainCalculateTubeButton.setText("정산됨")
            mainCalculateTubeButton.setOnClickListener {}

        } else if(!preferences.getBoolean("tubeCalculated", false)){
            //NEED CALCULATE
            if(calculatedTubeSimulyCoin > 0){
                mainCalculateTubeButton.setBackgroundResource(R.drawable.rounded_rectangle_colorprimary)
                mainCalculateTubeButton.setText("정산")

                mainCalculateTubeButton.setOnClickListener {

                    preferences.edit().putInt("simulyCoin", preferences.getInt("simulyCoin", 1000)+calculatedTubeSimulyCoin).apply()
                    mainCalculateTubeButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                    mainCalculateTubeButton.setText("정산됨")
                    mainCalculateTubeButton.setOnClickListener {}

                    preferences.edit().putBoolean("tubeCalculated", true).apply()

                }

            }else{
                mainCalculateTubeButton.setBackgroundResource(R.drawable.rounded_rectangle_greydark)
                mainCalculateTubeButton.setText("정산")
                mainCalculateTubeButton.setOnClickListener {}
            }
        }

    }

    fun initializingFifthLayout(){

        var tubeModel = ArrayList<TubeItem>()

        val tubeAdapter = TubeListAdapter(this, tubeModel)

        mainFifthLayout.adapter = tubeAdapter

        var dbHandler = TubeDatabaseHelper(this, null)

        if(dbHandler.getTube()!!.moveToLast()){

            var c = dbHandler.getTube()!!
            c.moveToFirst()

            if(c.moveToFirst()) {

                do {

                    var thumbnail = c.getString(c.getColumnIndex(TubeDatabaseHelper.COLUMN_THUMBNAIL))
                    var title = c.getString(c.getColumnIndex(TubeDatabaseHelper.COLUMN_TITLE))
                    var time = c.getInt(c.getColumnIndex(TubeDatabaseHelper.COLUMN_TIME))
                    var play = c.getInt(c.getColumnIndex(TubeDatabaseHelper.COLUMN_PLAY))
                    var date = c.getString(c.getColumnIndex(TubeDatabaseHelper.COLUMN_DATE))

                    tubeModel.add(TubeItem(thumbnail,title,time,play,date))

                } while (c.moveToNext())

                tubeAdapter.notifyDataSetChanged()
                c.close()
            }

        }

    }

    fun setStoreWidget(){

        mainBuyCash.setOnClickListener {

        }

        mainThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 1000).toString()

        mainDisplayChairLevel.text = preferences.getInt("chairLevel", 1).toString()
        mainDisplayLightLevel.text = preferences.getInt("lightLevel", 1).toString()
        mainDisplayEquipmentLevel.text = preferences.getInt("equipmentLevel", 1).toString()
        mainDisplayInteriorLevel.text = preferences.getInt("interiorLevel", 1).toString()

        mainDisplayHPLevel.text = preferences.getInt("hp", 100).toString()
        mainDisplayFullLevel.text = preferences.getInt("full", 100).toString()
        mainDisplayStressLevel.text = preferences.getInt("stress", 0).toString()

        mainBuyChair.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 20000){
                preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-20000).apply()
                refreshStoreCashCoinView()

                preferences.edit().putInt("broadcastValue", preferences.getInt("broadcastValue", 10)+100).apply()
                preferences.edit().putInt("chairLevel", preferences.getInt("chairLevel", 1)+1).apply()

                mainDisplayChairLevel.text = (preferences.getInt("chairLevel", 1)).toString()

            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyLight.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 20000){
                preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-20000).apply()
                refreshStoreCashCoinView()

                preferences.edit().putInt("broadcastValue", preferences.getInt("broadcastValue", 10)+100).apply()
                preferences.edit().putInt("lightLevel", preferences.getInt("lightLevel", 1)+1).apply()

                mainDisplayLightLevel.text = (preferences.getInt("lightLevel", 1)).toString()
            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyEquipment.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 20000){
                preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-20000).apply()
                refreshStoreCashCoinView()

                preferences.edit().putInt("broadcastValue", preferences.getInt("broadcastValue", 10)+100).apply()
                preferences.edit().putInt("equipmentLevel", preferences.getInt("equipmentLevel", 1)+1).apply()

                mainDisplayEquipmentLevel.text = (preferences.getInt("equipmentLevel", 1)).toString()
            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyInterior.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 20000){
                preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000)-20000).apply()
                refreshStoreCashCoinView()

                preferences.edit().putInt("broadcastValue", preferences.getInt("broadcastValue", 10)+100).apply()
                preferences.edit().putInt("interiorLevel", preferences.getInt("interiorLevel", 1)+1).apply()

                mainDisplayInteriorLevel.text = (preferences.getInt("interiorLevel", 1)).toString()
            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyShopping.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 700){

                if(preferences.getInt("stress", 0) != 0) {
                    preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000) - 700).apply()
                    refreshStoreCashCoinView()

                    val stress = preferences.getInt("stress", 0)

                    if (stress > 21) {
                        preferences.edit().putInt("stress", preferences.getInt("stress", 0) - 20).apply()
                    } else {
                        preferences.edit().putInt("stress", 0).apply()
                    }

                    mainDisplayStressLevel.text = preferences.getInt("stress", 0).toString()
                }else{
                    Toast.makeText(this, "받은 스트레스가 없습니다.", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyHP.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 400){

                if(preferences.getInt("hp", 100) != 100) {
                    preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000) - 400).apply()
                    refreshStoreCashCoinView()

                    val hp = preferences.getInt("hp", 100)

                    if (hp < 80) {
                        preferences.edit().putInt("hp", preferences.getInt("hp", 0) + 20).apply()
                    } else {
                        preferences.edit().putInt("hp", 100).apply()
                    }

                    mainDisplayHPLevel.text = preferences.getInt("hp", 100).toString()
                }else{
                    Toast.makeText(this, "체력이 가득 찼습니다.", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

        mainBuyMeal.setOnClickListener {

            if(preferences.getInt("cashCoin", 1000) > 400){

                if(preferences.getInt("full", 100) != 100) {
                    preferences.edit().putInt("cashCoin", preferences.getInt("cashCoin", 1000) - 400).apply()
                    refreshStoreCashCoinView()

                    val hp = preferences.getInt("full", 100)

                    if (hp < 80) {
                        preferences.edit().putInt("full", preferences.getInt("full", 0) + 20).apply()
                    } else {
                        preferences.edit().putInt("full", 100).apply()
                    }

                    mainDisplayFullLevel.text = preferences.getInt("full", 100).toString()
                }else{
                    Toast.makeText(this, "포만감이 가득 찼습니다", Toast.LENGTH_LONG).show()
                }

            } else {
                Toast.makeText(this, "캐시코인이 부족합니다.", Toast.LENGTH_LONG).show()
            }

        }

    }

    fun refreshStoreCashCoinView(){
        mainThridCashView.text = "캐시코인 : "+preferences.getInt("cashCoin", 1000).toString()
    }

    fun setRanks(){

        var broadcastValue = preferences.getInt("broadcastValue", 0)
        var broadcastPopularity  = preferences.getInt("broadcastPopularity", 0)
        var totalRecommand = preferences.getInt("totalRecommand", 0)
        var totalBroadcastTime = preferences.getInt("totalBroadcastTime", 0)
        var totalViewer = preferences.getInt("totalViewer", 0)

        //BROADCAST VALUE
        if(broadcastValue == 100){

            var rank = 700
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 1000){

            var rank = (600..700).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 2500){

            var rank = (500..600).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 5000){

            var rank = (400..500).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 10000){

            var rank = (300..400).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 15000){

            var rank = (200..300).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 25000){

            var rank = (100..200).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 35000){

            var rank = (50..100).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else if(broadcastValue < 50000){

            var rank = (10..50).random()
            mainBroadcastValueRank.text = rank.toString()+"위"

        }else{

            var rank = 1
            mainBroadcastValueRank.text = rank.toString()+"위"

        }

        //BROADCAST POPULARITY
        if(broadcastPopularity == 100){

            var rank = 700
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 1000){

            var rank = (600..700).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 2500){

            var rank = (500..600).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 5000){

            var rank = (400..500).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 10000){

            var rank = (300..400).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 15000){

            var rank = (200..300).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 25000){

            var rank = (100..200).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 35000){

            var rank = (50..100).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else if(broadcastPopularity < 50000){

            var rank = (10..50).random()
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }else{

            var rank = 1
            mainBroadcastPopularityRank.text = rank.toString()+"위"

        }

        //TOTAL RECOMMAND
        if(totalRecommand == 0){

            var rank = 700
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 100){

            var rank = (600..700).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 300){

            var rank = (500..600).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 500){

            var rank = (400..500).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 700){

            var rank = (300..400).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 900){

            var rank = (200..300).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 1100){

            var rank = (100..200).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 1300){

            var rank = (50..100).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else if(totalRecommand < 1500){

            var rank = (10..50).random()
            mainTotalRecommandRank.text = rank.toString()+"위"

        }else{

            var rank = 1
            mainTotalRecommandRank.text = rank.toString()+"위"

        }

        //BROADCAST TIME
        if(totalBroadcastTime == 0){

            var rank = 700
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 80*60){

            var rank = (600..700).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 160*60){

            var rank = (500..600).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 240*60){

            var rank = (400..500).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 320*60){

            var rank = (300..400).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 400*60){

            var rank = (200..300).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 480*60){

            var rank = (100..200).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 560*60){

            var rank = (50..100).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else if(totalBroadcastTime < 640*60){

            var rank = (10..50).random()
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }else{

            var rank = 1
            mainTotalBroadcastTimeRank.text = rank.toString()+"위"

        }

        //TOTAL VIEWER
        if(totalViewer == 0){

            var rank = 700
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 50){

            var rank = (600..700).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 100){

            var rank = (500..600).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 1000){

            var rank = (400..500).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 10000){

            var rank = (300..400).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 20000){

            var rank = (200..300).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 30000){

            var rank = (100..200).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 40000){

            var rank = (50..100).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else if(totalViewer < 50000){

            var rank = (10..50).random()
            mainTotalViewerRank.text = rank.toString()+"위"

        }else{

            var rank = 1
            mainTotalViewerRank.text = rank.toString()+"위"

        }

    }
}

package com.instabox.bjsimulator.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instabox.bjsimulator.R
import com.instabox.bjsimulator.item.ChatItem
import android.widget.TextView

class ChatListAdapter(private val activity: Activity, private val listItem: ArrayList<ChatItem>) : BaseAdapter() {

    lateinit var preferences: SharedPreferences

    private var inflater: LayoutInflater? = null

    override fun getCount(): Int {
        return listItem.size
    }

    override fun getItem(location: Int): Any {
        return listItem[location]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var convertView = convertView

        if (inflater == null)
            inflater = activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        if (convertView == null) {
            convertView = inflater!!.inflate(R.layout.chat_item, null)
        }

        val typeface1 = Typeface.createFromAsset(activity.assets, "NanumSquareB.otf")
        val typeface2 = Typeface.createFromAsset(activity.assets, "NanumSquareR.otf")

        preferences = activity.getSharedPreferences("simulator", 0)
        val item = listItem[position]

        val user = convertView?.findViewById<TextView>(R.id.chatItemNameView)
        val sentence = convertView?.findViewById<TextView>(R.id.chatItemSentenceView)
        var simulyIcon = convertView?.findViewById<ImageView>(R.id.chatItemSimulyIcon)

        if(item.sentence == "채팅이 시작되었습니다." || item.sentence == "채팅이 종료되었습니다."){
            user?.setTextColor(ContextCompat.getColor(activity, R.color.colorRanking))
            sentence?.setTextColor(ContextCompat.getColor(activity, R.color.colorRanking))
            simulyIcon?.visibility = View.GONE

            user?.typeface = typeface1
            sentence?.typeface = typeface1
        } else if (item.sentence.substring(0,2) == "코인"){
            user?.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            sentence?.setTextColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            simulyIcon?.visibility = View.VISIBLE

            user?.typeface = typeface1
            sentence?.typeface = typeface1
        } else if (item.sentence.substring(0,2) == "추천"){
            user?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
            sentence?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreen))
            simulyIcon?.visibility = View.GONE

            user?.typeface = typeface1
            sentence?.typeface = typeface1
        }  else if (item.sentence.length > 4 && item.sentence.substring(0,4) == "[광고]"){
            user?.setTextColor(ContextCompat.getColor(activity, R.color.colorAd))
            sentence?.setTextColor(ContextCompat.getColor(activity, R.color.colorAd))
            simulyIcon?.visibility = View.GONE

            user?.typeface = typeface1
            sentence?.typeface = typeface1
        } else {
            user?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreyDark))
            sentence?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreyDark))
            simulyIcon?.visibility = View.GONE

            user?.typeface = typeface2
            sentence?.typeface = typeface2
        }

        user?.text = item.user
        sentence?.text = item.sentence

        return convertView!!
    }

}
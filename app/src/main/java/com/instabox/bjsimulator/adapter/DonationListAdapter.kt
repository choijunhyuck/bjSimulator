package com.instabox.bjsimulator.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instabox.bjsimulator.R
import com.instabox.bjsimulator.item.ChatItem
import android.widget.TextView
import com.instabox.bjsimulator.item.DonationItem

class DonationListAdapter(private val activity: Activity, private val listItem: ArrayList<DonationItem>) : BaseAdapter() {

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
            convertView = inflater!!.inflate(R.layout.donation_item, null)
        }

        val item = listItem[position]

        val ranking = convertView?.findViewById<TextView>(R.id.donationItemRankingView)
        val user = convertView?.findViewById<TextView>(R.id.donationItemUserView)
        val coin = convertView?.findViewById<TextView>(R.id.donationItemCoinView)

        ranking?.text = item.ranking
        user?.text = item.user
        coin?.text = item.coin+"개"

        if(item.ranking.substring(0,2) == "10"){
            ranking?.setBackgroundResource(R.drawable.rounded_rectangle_greydark_stroke)
            ranking?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreyDark))
        }else if(item.ranking.substring(0,1) == "1"){

            ranking?.setBackgroundResource(R.drawable.rounded_rectangle_gold_stroke)
            ranking?.setTextColor(ContextCompat.getColor(activity, R.color.colorGold))
            ranking?.text = "최고존엄"

        }else if(item.ranking.substring(0,1) == "2" || item.ranking.substring(0,1) == "3" ||item.ranking.substring(0,1) == "4"){

            ranking?.setBackgroundResource(R.drawable.rounded_rectangle_red_stroke)
            ranking?.setTextColor(ContextCompat.getColor(activity, R.color.colorRed))

        }else{
            ranking?.setBackgroundResource(R.drawable.rounded_rectangle_greydark_stroke)
            ranking?.setTextColor(ContextCompat.getColor(activity, R.color.colorGreyDark))
        }

        return convertView!!
    }

}
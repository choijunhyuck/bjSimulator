package com.instabox.bjsimulator.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.support.constraint.ConstraintLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instabox.bjsimulator.R
import android.widget.TextView
import com.instabox.bjsimulator.database.TubeDatabaseHelper
import com.instabox.bjsimulator.database.VodDatabaseHelper
import com.instabox.bjsimulator.func.GetDateFunc
import com.instabox.bjsimulator.item.VodItem

class VodListAdapter(private val activity: Activity, private val listItem: ArrayList<VodItem>) : BaseAdapter() {

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
            convertView = inflater!!.inflate(R.layout.vod_item, null)
        }

        val item = listItem[position]

        var screenView = convertView?.findViewById<ImageView>(R.id.vodItemScreen)
        var timeView = convertView?.findViewById<TextView>(R.id.vodItemTimeView)
        var titleView = convertView?.findViewById<TextView>(R.id.vodItemTitleView)
        var playView = convertView?.findViewById<TextView>(R.id.vodItemPlayView)
        var recommandView = convertView?.findViewById<TextView>(R.id.vodItemRecommandView)
        var dateView = convertView?.findViewById<TextView>(R.id.vodItemDateView)

        screenView?.setBackgroundResource(activity.getResources().getIdentifier(item.screen, "drawable", activity.getPackageName()))
        timeView?.text = (item.time / 3600).toString() + " : "+(item.time % 3600 / 60).toString() + " : "+(item.time % 3600 % 60).toString()

        if(item.title.length > 10){
            titleView?.text = item.title.substring(0,9)+".."
        }else{
            titleView?.text = item.title
        }

        playView?.text = item.play.toString()
        recommandView?.text = "받은 추천수 : "+item.recommand.toString()

        dateView?.text = item.date

        //SHOW TITLE
        titleView?.setOnClickListener {
            Toast.makeText(activity, item.title, Toast.LENGTH_LONG).show()
        }

        return convertView!!
    }

}
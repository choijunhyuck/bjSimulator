package com.instabox.bjsimulator.adapter

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instabox.bjsimulator.R
import android.widget.TextView
import com.instabox.bjsimulator.item.TubeItem

class TubeListAdapter(private val activity: Activity, private val listItem: ArrayList<TubeItem>) : BaseAdapter() {

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
            convertView = inflater!!.inflate(R.layout.tube_item, null)
        }

        val item = listItem[position]

        var screenView = convertView?.findViewById<ImageView>(R.id.tubeItemScreen)
        var timeView = convertView?.findViewById<TextView>(R.id.tubeItemTimeView)
        var titleView = convertView?.findViewById<TextView>(R.id.tubeItemTitleView)
        var playView = convertView?.findViewById<TextView>(R.id.tubeItemPlayView)
        var dateView = convertView?.findViewById<TextView>(R.id.tubeItemDateView)

        screenView?.setBackgroundResource(activity.getResources().getIdentifier(item.screen, "drawable", activity.getPackageName()))
        timeView?.text = (item.time / 3600).toString() + " : "+(item.time % 3600 / 60).toString() + " : "+(item.time % 3600 % 60).toString()

        if(item.title.length > 10){
            titleView?.text = item.title.substring(0,9)+".."
        }else{
            titleView?.text = item.title
        }

        playView?.text = item.play.toString()

        dateView?.text = "업로드 일자 : "+item.date

        //SHOW TITLE
        titleView?.setOnClickListener {
            Toast.makeText(activity, item.title, Toast.LENGTH_LONG).show()
        }

        return convertView!!
    }

}
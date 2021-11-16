package com.instabox.bjsimulator.func

import java.text.SimpleDateFormat
import java.util.*

class GetDateFunc{

    companion object{
        fun getDateTime(): String {
            val date = Date()

            val sdfDate = SimpleDateFormat(
                "yyyy.MM.dd",
                Locale.getDefault()
            )

            return sdfDate.format(date)
        }
    }

}
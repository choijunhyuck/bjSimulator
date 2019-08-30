package com.alomteamd.honbapteamd.func

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import java.io.ByteArrayOutputStream
import android.graphics.BitmapFactory

class BitmapConvertFunc : AppCompatActivity() {

    companion object {

        fun encoder(bitmap: Bitmap): String{

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
            val b = baos.toByteArray()
            val temp = Base64.encodeToString(b, Base64.DEFAULT)

            return temp
        }

        fun decoder(base64Str: String): Bitmap? {
            try {
                val encodeByte = Base64.decode(base64Str, Base64.DEFAULT)
                return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.size)
            } catch (e: Exception) {
                e.message

                return null
            }

        }

    }

}
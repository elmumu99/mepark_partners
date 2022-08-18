package com.mrpark1.meparkpartner.util

import android.util.Log
import java.text.DecimalFormat

object CommandUtil{
    fun addCommaNumInt(num: String): String{
        if(num==""){return "0"}
        try{
            val decFormat = DecimalFormat("#,###")
            Log.d("TEST@","addCommaNumInt  return :: ${decFormat.format(num.toInt())}")
            return decFormat.format(num.toInt())
        }catch (e: Exception){
            Log.d("TEST@","addCommaNumInt  num :: $num")
            return num
        }
    }
}
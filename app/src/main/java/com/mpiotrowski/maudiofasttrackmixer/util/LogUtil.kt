package com.mpiotrowski.maudiofasttrackmixer.util

import android.util.Log

class LogUtil {
    companion object{
        private const val TAG = "MPdebug"
        fun d(message: String){
            Log.d(TAG,message)
        }
    }
}
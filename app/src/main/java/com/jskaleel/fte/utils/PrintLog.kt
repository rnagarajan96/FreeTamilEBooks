package com.jskaleel.fte.utils

import android.util.Log

class PrintLog {
    companion object {
        fun info(log: String) {
            if(log.length > 4000) {
                info(log.substring(4000))
            }else {
                Log.d("FTELog", log)
            }
        }
    }
}
package com.stayhome.user.Utils

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity

class fortest : AppCompatActivity() {
    var dialog: Dialog? = null
    private fun dialog() {
        dialog = object : Dialog(this@fortest) {
            override fun onBackPressed() {
                //do your stuff
            }
        }
    }
}
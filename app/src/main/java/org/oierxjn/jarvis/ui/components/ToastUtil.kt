package org.oierxjn.jarvis.ui.components

import android.content.Context
import android.widget.Toast

object ToastUtil {
    fun showShort(context: Context, msg: String) {
        showToast(context, msg, Toast.LENGTH_SHORT)
    }
    fun showLong(context: Context, msg: String) {
        showToast(context, msg, Toast.LENGTH_LONG)
    }
    fun showToast(context: Context, msg: String, duration: Int) {
        Toast.makeText(context.applicationContext, msg, duration).show()
    }
}
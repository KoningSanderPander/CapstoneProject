package nl.svdoetelaar.capstoneproject.util

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


open class MyApplication : Application() {

    companion object {
        private var context: Context? = null
        val appContext: Context?
            get() = context

        fun closeKeyboard(activity: Activity) {
            val inputMethodManager =
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = if (activity.currentFocus == null) {
                View(activity)
            } else {
                activity.currentFocus
            }

            inputMethodManager.hideSoftInputFromWindow(view!!.windowToken, 0)

        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
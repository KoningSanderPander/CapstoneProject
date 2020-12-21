package nl.svdoetelaar.capstoneproject.util

import android.app.Application
import android.content.Context


open class MyApplication : Application() {

    companion object {
        private var context: Context? = null
        val appContext: Context?
            get() = context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
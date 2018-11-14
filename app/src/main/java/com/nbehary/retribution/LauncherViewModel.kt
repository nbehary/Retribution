package com.nbehary.retribution

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

class LauncherViewModel (application: Application) : AndroidViewModel(application) {

    var colorTheme: LiveData<ColorTheme>

    init {
        colorTheme = ColorTheme(application)
    }
}
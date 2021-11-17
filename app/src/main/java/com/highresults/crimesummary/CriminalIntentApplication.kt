package com.highresults.crimesummary

import android.app.Application
import com.highresults.crimesummary.database.CrimeRepository

class CriminalIntentApplication : Application()
{
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}
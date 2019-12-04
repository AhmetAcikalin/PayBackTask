package com.ahmet.acikalin.paybacktask.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.ahmet.acikalin.paybacktask.R
import com.ahmet.acikalin.paybacktask.databinding.ActivityMainBinding
import com.ahmet.acikalin.paybacktask.utils.NetworkCheck
import okhttp3.Cache


class MainActivity : AppCompatActivity() {


    companion object {
        lateinit var cache: Cache
        var hasNetwork: Boolean = false
    }
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil
            .setContentView(this, R.layout.activity_main)


        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        cache = Cache(cacheDir, Int.MAX_VALUE.toLong())
        hasNetwork = NetworkCheck.hasNetwork(this)!!

    }


}

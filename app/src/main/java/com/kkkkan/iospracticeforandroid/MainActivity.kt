package com.kkkkan.iospracticeforandroid

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kkkkan.iospracticeforandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding =
            DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        if (savedInstanceState == null) {
            val fragTop = FragTop.getNewInstance()
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, fragTop)
                commit()
            }
        }
    }
}

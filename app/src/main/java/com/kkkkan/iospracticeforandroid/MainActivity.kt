package com.kkkkan.iospracticeforandroid

import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.kkkkan.iospracticeforandroid.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
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

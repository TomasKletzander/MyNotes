package cz.dmn.display.mynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityMainBinding
import dagger.Module
import dagger.android.AndroidInjection

class MainActivity : AppCompatActivity() {

    @Module
    abstract class InjectionModule

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        setSupportActionBar(binding.toolbar)
    }
}

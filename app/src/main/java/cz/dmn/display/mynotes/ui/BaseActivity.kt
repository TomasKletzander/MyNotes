package cz.dmn.display.mynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.ui.main.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjection

abstract class BaseActivity : AppCompatActivity() {

    @Module
    abstract class InjectionModule {

        @Binds
        @PerActivity
        internal abstract fun bindAppCompatActivity(activity: MainActivity): AppCompatActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }
}
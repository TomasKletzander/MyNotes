package cz.dmn.display.mynotes

import android.app.Activity
import android.app.Application
import cz.dmn.display.mynotes.di.DaggerApplicationComponent
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class MyNotesApplication : Application(), HasActivityInjector {

    @Inject
    internal lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        DaggerApplicationComponent.builder().create(this).inject(this)
        super.onCreate()
    }

    override fun activityInjector() = activityInjector
}
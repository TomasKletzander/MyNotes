package cz.dmn.display.mynotes.di

import cz.dmn.display.mynotes.ui.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = [MainActivity.InjectionModule::class])
    internal abstract fun contributeMainActivity(): MainActivity
}
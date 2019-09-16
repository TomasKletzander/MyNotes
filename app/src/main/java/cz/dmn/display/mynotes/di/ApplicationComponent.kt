package cz.dmn.display.mynotes.di

import cz.dmn.display.mynotes.MyNotesApplication
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ActivitiesModule::class])
interface ApplicationComponent : AndroidInjector<MyNotesApplication> {

    @Component.Builder
    abstract class Builder : AndroidInjector.Builder<MyNotesApplication>()
}
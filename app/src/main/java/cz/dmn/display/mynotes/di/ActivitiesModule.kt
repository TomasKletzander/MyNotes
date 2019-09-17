package cz.dmn.display.mynotes.di

import cz.dmn.display.mynotes.ui.main.MainActivity
import cz.dmn.display.mynotes.ui.note.NoteActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = [MainActivity.InjectionModule::class])
    @PerActivity
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [NoteActivity.InjectionModule::class])
    @PerActivity
    internal abstract fun contributeNoteActivity(): NoteActivity
}
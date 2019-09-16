package cz.dmn.display.mynotes.di

import androidx.room.Room
import cz.dmn.display.mynotes.MyNotesApplication
import cz.dmn.display.mynotes.db.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Module(includes = [AndroidInjectionModule::class])
abstract class ApplicationModule {

    @Module
    companion object {

        @Provides
        @JvmStatic
        @Singleton
        internal fun provideNotesDatabase(application: MyNotesApplication)
            = Room.databaseBuilder(application, NotesDatabase::class.java, "Notes.db").build()
    }
}
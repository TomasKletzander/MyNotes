package cz.dmn.display.mynotes.di

import androidx.room.Room
import cz.dmn.display.mynotes.MyNotesApplication
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NotesDatabase
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

        @Provides
        @JvmStatic
        @Singleton
        internal fun provideNotesApi() = Retrofit.Builder()
            .baseUrl("https://private-anon-a6bbcc58f3-note10.apiary-mock.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotesApi::class.java)
    }
}
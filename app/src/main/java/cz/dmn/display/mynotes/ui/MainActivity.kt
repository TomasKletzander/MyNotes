package cz.dmn.display.mynotes.ui

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityMainBinding
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.mvvm.NotesViewModel
import cz.dmn.display.mynotes.toUiModel
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Module
    abstract class InjectionModule {

        @Binds
        @PerActivity
        internal abstract fun bindAppCompatActivity(activity: MainActivity): AppCompatActivity

        @Module
        companion object {

            @Provides
            @JvmStatic
            @PerActivity
            internal fun provideLayoutInflater(activity: MainActivity) = LayoutInflater.from(activity)

            @Provides
            @JvmStatic
            @PerActivity
            internal fun provideRecycler(activity: MainActivity) = activity.binding.notes
        }
    }

    class ViewModelFactory(private val viewModelProvider: Provider<NotesViewModel>) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NotesViewModel::class.java)) {
                return viewModelProvider.get() as T
            } else {
                throw RuntimeException()
            }
        }
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: NotesViewModel
    @Inject internal lateinit var viewModelProvider: Provider<NotesViewModel>
    @Inject internal lateinit var notesDecorator: NotesDecorator
    @Inject internal lateinit var notesAdapterLazy: Lazy<NotesAdapter>
    private val notesAdapter by lazy(LazyThreadSafetyMode.NONE) { notesAdapterLazy.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.notes.adapter = notesAdapter
        binding.notes.addItemDecoration(notesDecorator)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(viewModelProvider)).get(NotesViewModel::class.java)
        viewModel.data.observe(this, Observer<List<NoteDbEntity>> {
            notesAdapter.updateModels(it.map { dbEntity -> dbEntity.toUiModel() })
            notesAdapter.notifyDataSetChanged()
        })
    }
}

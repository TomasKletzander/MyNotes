package cz.dmn.display.mynotes.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.viewModelScope
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityMainBinding
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.mvvm.NotesDataConverter
import cz.dmn.display.mynotes.mvvm.NotesViewModel
import cz.dmn.display.mynotes.navigator.Navigator
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_TEXT
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseActivity() {

    companion object {
        private const val REQUEST_NEW_NOTE = 100
    }

    @Module(includes = [BaseActivity.InjectionModule::class])
    abstract class InjectionModule {

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
    @Inject internal lateinit var navigator: Navigator
    @Inject internal lateinit var notesDataConverter: NotesDataConverter
    private val notesAdapter by lazy(LazyThreadSafetyMode.NONE) { notesAdapterLazy.get() }
    private var editingNoteId = -1L
    private var scrollToEditingPosition = false

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.notes.adapter = notesAdapter
        binding.notes.addItemDecoration(notesDecorator)
        binding.fab.setOnClickListener { addNote() }
        viewModel = ViewModelProviders.of(this, ViewModelFactory(viewModelProvider)).get(NotesViewModel::class.java)
        viewModel.data.observe(this, Observer<List<NoteDbEntity>> {
            notesAdapter.updateModels(it.map { dbEntity -> notesDataConverter.toUiModel(dbEntity) })
            notesAdapter.notifyDataSetChanged()
            if (editingNoteId >= 0 && scrollToEditingPosition) {
                val index = notesAdapter.findPositionOfId(editingNoteId)
                binding.notes.smoothScrollToPosition(index)
                scrollToEditingPosition = false
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_NEW_NOTE -> {
                if (resultCode == RESULT_OK && data != null) {
                    insertNewNote(data.getStringExtra(EXTRA_NOTE_TEXT))
                }
            }
        }
    }

    private fun addNote() {
        navigator.navigateToNote(REQUEST_NEW_NOTE)
    }

    private fun insertNewNote(text: String) {
        viewModel.viewModelScope.launch(Dispatchers.Main) {
            editingNoteId = withContext(Dispatchers.IO) {
                viewModel.addNote(text)
            }
            scrollToEditingPosition = true
        }
    }
}

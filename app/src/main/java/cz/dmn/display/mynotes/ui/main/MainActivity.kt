package cz.dmn.display.mynotes.ui.main

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityMainBinding
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.mvvm.NotesDataConverter
import cz.dmn.display.mynotes.mvvm.NotesViewModel
import cz.dmn.display.mynotes.mvvm.NotesViewModel.Status.*
import cz.dmn.display.mynotes.navigator.Navigator
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_ID
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_TEXT
import cz.dmn.display.mynotes.ui.BaseActivity
import dagger.Binds
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : BaseActivity(),
    NoteClickListener {

    companion object {
        private const val REQUEST_NOTE = 100
    }

    @Module(includes = [BaseActivity.InjectionModule::class])
    abstract class InjectionModule {

        @Binds
        @PerActivity
        internal abstract fun bindNoteClickListener(activity: MainActivity): NoteClickListener

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
                @Suppress("UNCHECKED_CAST")
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
    @Inject internal lateinit var swipeToDeleteHandler: SwipeToDeleteHandler
    private val notesAdapter by lazy(LazyThreadSafetyMode.NONE) { notesAdapterLazy.get() }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        binding.notes.adapter = notesAdapter
        binding.notes.addItemDecoration(notesDecorator)
        binding.fab.setOnClickListener { addNote() }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.synchronize()
        }
        ItemTouchHelper(swipeToDeleteHandler).attachToRecyclerView(binding.notes)

        viewModel = ViewModelProviders.of(this,
            ViewModelFactory(viewModelProvider)
        ).get(NotesViewModel::class.java)
        viewModel.data.observe(this, Observer<List<NoteDbEntity>> {
            notesAdapter.updateModels(it.map { dbEntity -> notesDataConverter.toUiModel(dbEntity) })
            notesAdapter.notifyDataSetChanged()
        })
        viewModel.status.observe(this, Observer<NotesViewModel.Status> {
            when (it) {
                Success -> binding.swipeRefresh.isRefreshing = false
                Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Snackbar.make(binding.root, R.string.sync_error, Snackbar.LENGTH_LONG).show()
                }
            }
        })
        if (savedInstanceState == null) {
            viewModel.synchronize()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_NOTE -> {
                if (resultCode == RESULT_OK && data != null) {
                    if (data.getLongExtra(EXTRA_NOTE_ID, -1L) == -1L) {
                        insertNewNote(data.getStringExtra(EXTRA_NOTE_TEXT) ?: "")
                    } else {
                        updateNote(data.getLongExtra(EXTRA_NOTE_ID, -1L), data.getStringExtra(EXTRA_NOTE_TEXT) ?: "")
                    }
                }
            }
        }
    }

    override fun onNoteClick(id: Long) {
        lifecycleScope.launch(Dispatchers.Default) {
            viewModel.data.value?.find { it.id == id }?.let {
                withContext(Dispatchers.Main) {
                    navigator.navigateToNote(REQUEST_NOTE, id = it.id, text = it.text)
                }
            }
        }
    }

    private fun addNote() {
        navigator.navigateToNote(REQUEST_NOTE)
    }

    private fun insertNewNote(text: String) {
        viewModel.addNote(text)
    }

    private fun updateNote(id: Long, text: String) {
        viewModel.viewModelScope.launch(Dispatchers.IO) {
            viewModel.updateNote(id, text)
        }
    }
}

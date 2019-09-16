package cz.dmn.display.mynotes.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityMainBinding
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.mvvm.NotesViewModel
import dagger.Module
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Module
    abstract class InjectionModule

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

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_main
        )
        setSupportActionBar(binding.toolbar)
        viewModel = ViewModelProviders.of(this, ViewModelFactory(viewModelProvider)).get(NotesViewModel::class.java)
        viewModel.data.observe(this, Observer<List<NoteDbEntity>> {

        })
    }
}

package cz.dmn.display.mynotes.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityNoteBinding
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_TEXT
import dagger.Module

class NoteActivity : BaseActivity() {

    @Module(includes = [BaseActivity.InjectionModule::class])
    abstract class InjectionModule

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note)
        binding.save.setOnClickListener {
            setResult(RESULT_OK, Intent().putExtra(EXTRA_NOTE_TEXT, binding.noteText.text.toString()))
            finishAfterTransition()
        }
        binding.cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finishAfterTransition()
        }
    }
}
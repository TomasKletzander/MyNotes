package cz.dmn.display.mynotes.ui.note

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.databinding.ActivityNoteBinding
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_ID
import cz.dmn.display.mynotes.navigator.Navigator.Companion.EXTRA_NOTE_TEXT
import cz.dmn.display.mynotes.ui.BaseActivity
import dagger.Module

class NoteActivity : BaseActivity() {

    @Module(includes = [BaseActivity.InjectionModule::class])
    abstract class InjectionModule

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note)
        intent.getStringExtra(EXTRA_NOTE_TEXT)?.let {
            binding.noteText.setText(it)
        }
        binding.save.setOnClickListener {
            val response = Intent().putExtra(EXTRA_NOTE_TEXT, binding.noteText.text.toString())
            intent.getLongExtra(EXTRA_NOTE_ID, -1L).let {
                if (it != -1L) {
                    response.putExtra(EXTRA_NOTE_ID, it)
                }
            }
            setResult(RESULT_OK, response)
            finishAfterTransition()
        }
        binding.cancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finishAfterTransition()
        }
    }
}
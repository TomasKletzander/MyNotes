package cz.dmn.display.mynotes.navigator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.ui.NoteActivity
import javax.inject.Inject

@PerActivity
class Navigator @Inject constructor(private val activity: AppCompatActivity) {

    companion object {
        const val EXTRA_NOTE_TEXT = "noteText"
    }

    fun navigateToNote(requestCode: Int) {
        activity.startActivityForResult(Intent(activity, NoteActivity::class.java), requestCode)
    }
}
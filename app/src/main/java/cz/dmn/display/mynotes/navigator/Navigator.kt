package cz.dmn.display.mynotes.navigator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.ui.note.NoteActivity
import javax.inject.Inject

@PerActivity
class Navigator @Inject constructor(private val activity: AppCompatActivity) {

    companion object {
        const val EXTRA_NOTE_ID = "noteId"
        const val EXTRA_NOTE_TEXT = "noteText"
    }

    fun navigateToNote(requestCode: Int, id: Long? = null, text: String? = null) {
        val intent = Intent(activity, NoteActivity::class.java)
        id?.let { intent.putExtra(EXTRA_NOTE_ID, it) }
        text?.let { intent.putExtra(EXTRA_NOTE_TEXT, it) }
        activity.startActivityForResult(intent, requestCode)
    }
}
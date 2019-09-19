package cz.dmn.display.mynotes.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDatabase
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val database: NotesDatabase,
    private val synchronizer: NotesSynchronizer,
    private val coroutineContextProvider: CoroutineContextProvider
) : ViewModel() {

    enum class Status {
        Idle,
        Loading,
        Success,
        Error
    }

    val data = database.notes.allLive()

    private val internalStatus = MutableLiveData<Status>()
    val status: LiveData<Status> = internalStatus

    fun addNote(text: String) = viewModelScope.launch(coroutineContextProvider.Main) {
        database.notes.insert(NoteDbEntity(null, -1, text))
        synchronize()
    }

    fun updateNote(id: Long, text: String) = viewModelScope.launch(coroutineContextProvider.Main) {
        val note = database.notes.get(id)
        val newNote = note?.copy(text = text, dirty = true)
        newNote?.let {
            database.notes.update(it)
            synchronize()
        }
    }

    fun removeNoteById(id: Long) = viewModelScope.launch(coroutineContextProvider.Main) {
        database.notes.get(id)?.let {
            database.notes.update(it.copy(deleted = true))
            synchronize()
        }
    }

    fun synchronize() = viewModelScope.launch(coroutineContextProvider.Main) {
        try {
            internalStatus.value = Status.Loading
            synchronizer.synchronize()
            internalStatus.value = Status.Success
        } catch (e: Throwable) {
            internalStatus.value = Status.Error
            internalStatus.value = Status.Idle
        }
    }
}
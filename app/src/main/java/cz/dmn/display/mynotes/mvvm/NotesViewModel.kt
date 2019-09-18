package cz.dmn.display.mynotes.mvvm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val database: NotesDatabase,
    private val api: NotesApi,
    private val notesDataConverter: NotesDataConverter
) : ViewModel() {

    enum class Status {
        Loading,
        Success,
        Error
    }

    val data = database.notes.allLive()

    private val internalStatus = MutableLiveData<Status>()
    val status: LiveData<Status> = internalStatus

    fun addNote(text: String) = viewModelScope.launch(Dispatchers.Main) {
        database.notes.insert(NoteDbEntity(null, -1, text))
    }

    fun updateNote(id: Long, text: String) = viewModelScope.launch(Dispatchers.Main) {
        data.value?.find { it.id == id }?.copy(text = text, dirty = true)?.let {
            database.notes.update(it)
        }
    }

    fun removeNoteById(id: Long) = viewModelScope.launch(Dispatchers.IO) {
        database.notes.get(id)?.let {
            database.notes.delete(it)
        }
    }

    fun synchronize() = viewModelScope.launch(Dispatchers.IO) {
        try {
            internalStatus.postValue(Status.Loading)

            //  Save all notes to be processed
            val allNotes = mutableListOf<NoteDbEntity>()
            this@NotesViewModel.data.value?.let { allNotes.addAll(it) }

            //  Send new notes
            val newNotes = allNotes.filter { it.serverId < 0L }
            newNotes.forEach {
                val apiData = api.insertNote(notesDataConverter.toApiModel(it))
                database.notes.update(it.copy(serverId = apiData.id))
            }
            allNotes.removeAll(newNotes)

            //  Send dirty notes
            val dirtyNotes = allNotes.filter { it.dirty }
            dirtyNotes.forEach {
                api.updateNote(it.serverId, notesDataConverter.toApiModel(it))
                database.notes.update(it.copy(dirty = false))
            }
            allNotes.removeAll(dirtyNotes)

            //  Delete removed notes

            //  Process server notes
            val apiNotes = api.getNotes()
            apiNotes.forEach { apiNote ->
                if ((this@NotesViewModel.data.value?.find { it.serverId == apiNote.id }) == null) {
                    database.notes.insert(notesDataConverter.toDbEntity(apiNote))
                }
            }
            internalStatus.postValue(Status.Success)
        } catch (e: Throwable) {
            Log.e("API Error", e.toString())
            internalStatus.postValue(Status.Error)
        }
    }
}
package cz.dmn.display.mynotes.mvvm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDbAdapter
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class NotesViewModel @Inject constructor(
    private val dbAdapter: NotesDbAdapter,
    private val api: NotesApi
) : ViewModel() {

    enum class Status {
        Loading,
        Success,
        Error
    }

    val data = liveData(Dispatchers.IO) {
        internalStatus.postValue(Status.Loading)
        emitSource(dbAdapter.data)
        try {
            dbAdapter.updateData(api.getNotes())
            internalStatus.postValue(Status.Success)
        } catch (e: Throwable) {
            internalStatus.postValue(Status.Error)
        }
    }

    private val internalStatus = MutableLiveData<Status>()
    val status: LiveData<Status> = internalStatus

    fun addNote(text: String) = dbAdapter.addNote(NoteDbEntity(null, -1, text))
}
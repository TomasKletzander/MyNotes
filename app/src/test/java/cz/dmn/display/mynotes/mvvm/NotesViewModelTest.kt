package cz.dmn.display.mynotes.mvvm

import androidx.lifecycle.LiveData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import cz.dmn.display.mynotes.api.NoteApiModel
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDbAdapter
import cz.dmn.display.mynotes.shouldEqual
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NotesViewModelTest {

    companion object {
        const val NOTE_ID = 1L
        const val NOTE = "Note"
    }

    private val mainThreadSurrogate = newSingleThreadContext("UI thread")
    private lateinit var notesViewModel: NotesViewModel
    @Mock internal lateinit var dbAdapter: NotesDbAdapter
    @Mock internal lateinit var dbData: LiveData<List<NoteDbEntity>>
    @Spy internal lateinit var notesDataConverter: NotesDataConverter
    val api = mock<NotesApi> {
        onBlocking { getNotes() }.thenReturn(emptyList())
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
        whenever(dbAdapter.data).thenReturn(dbData)
        notesViewModel = NotesViewModel(dbAdapter, api, notesDataConverter)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun setupData() {
        verify(dbAdapter).data
    }

    @Test
    fun addNote() {
        runBlockingTest {
            notesViewModel.addNote(NOTE)
            argumentCaptor<NoteDbEntity> {
                verify(dbAdapter).addNote(capture())
                firstValue.id shouldEqual null
                firstValue.serverId shouldEqual -1L
                firstValue.text shouldEqual NOTE
                firstValue.dirty shouldEqual false
            }
        }
    }

    @Test
    fun updateNote() {
        runBlockingTest {
            notesViewModel.updateNote(NOTE_ID, NOTE)
            argumentCaptor<NoteDbEntity> {
                verify(dbAdapter).update(capture())
                firstValue.id shouldEqual NOTE_ID
                firstValue.serverId shouldEqual null
            }
        }
    }
}
package cz.dmn.display.mynotes.mvvm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import cz.dmn.display.mynotes.TestCoroutineContextProvider
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDao
import cz.dmn.display.mynotes.db.NotesDatabase
import cz.dmn.display.mynotes.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ObsoleteCoroutinesApi
@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NotesViewModelTest {

    companion object {
        const val NOTE_ID = 1L
        const val NOTE = "Note"
    }

    @Rule @JvmField val rule = InstantTaskExecutorRule()
    private lateinit var notesViewModel: NotesViewModel
    @Mock internal lateinit var database: NotesDatabase
    @Mock internal lateinit var notesDao: NotesDao
    @Mock internal lateinit var synchronizer: NotesSynchronizer

    @Before
    fun setUp() {
        whenever(database.notes).thenReturn(notesDao)
        notesViewModel = NotesViewModel(database, synchronizer, TestCoroutineContextProvider())
    }

    @Test
    fun setupData() {
        verify(notesDao).allLive()
    }

    @Test
    fun addNote() = runBlockingTest {
        notesViewModel.addNote(NOTE)
        argumentCaptor<NoteDbEntity> {
            verify(notesDao).insert(capture())
            firstValue.id shouldEqual null
            firstValue.serverId shouldEqual -1L
            firstValue.text shouldEqual NOTE
            firstValue.dirty shouldEqual false
        }
    }

    @Test
    fun updateNoteWhichIsNotFoundInDatabase() = runBlockingTest {
        notesViewModel.updateNote(NOTE_ID, NOTE)
        verify(notesDao, never()).update(any())
    }

    @Test
    fun updateNote() = runBlockingTest {
        whenever(notesDao.get(NOTE_ID)).thenReturn(NoteDbEntity(NOTE_ID, -1, ""))
        notesViewModel.updateNote(NOTE_ID, NOTE)
        argumentCaptor<NoteDbEntity>().apply {
            verify(notesDao).update(capture())
            firstValue.id shouldEqual NOTE_ID
            firstValue.text shouldEqual NOTE
        }
    }

    @Test
    fun removeNoteByIdWhichIsNotFoundInDatabase() = runBlockingTest {
        notesViewModel.removeNoteById(NOTE_ID)
        verify(notesDao, never()).delete(any())
    }

    @Test
    fun removeNoteById() = runBlockingTest {
        whenever(notesDao.get(NOTE_ID)).thenReturn(NoteDbEntity(NOTE_ID, -1, NOTE))
        notesViewModel.removeNoteById(NOTE_ID)
        argumentCaptor<NoteDbEntity>().apply {
            verify(notesDao).update(capture())
            firstValue.id shouldEqual NOTE_ID
            firstValue.deleted shouldEqual true
        }
    }
}
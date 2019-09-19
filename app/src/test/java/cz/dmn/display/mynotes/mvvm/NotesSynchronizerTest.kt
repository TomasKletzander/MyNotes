package cz.dmn.display.mynotes.mvvm

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import cz.dmn.display.mynotes.TestCoroutineContextProvider
import cz.dmn.display.mynotes.api.NoteApiModel
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDao
import cz.dmn.display.mynotes.db.NotesDatabase
import cz.dmn.display.mynotes.shouldEqual
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class NotesSynchronizerTest {

    private lateinit var notesSynchronizer: NotesSynchronizer
    @Mock internal lateinit var api: NotesApi
    @Mock internal lateinit var database: NotesDatabase
    @Mock internal lateinit var notesDao: NotesDao
    private val notesDataConverter = NotesDataConverter()
    private val idGeneratorSeed = 5L
    private var idGenerator = idGeneratorSeed

    @Before
    fun setUp() {
        whenever(database.notes).thenReturn(notesDao)
        notesSynchronizer = NotesSynchronizer(api, database, notesDataConverter,
            TestCoroutineContextProvider())
    }

    @Test
    fun synchronize() = runBlockingTest {
        whenever(api.insertNote(any())).thenAnswer {
            val dataThere = it.getArgument<NoteApiModel>(0)
            dataThere.copy(id = idGenerator++)
        }
        whenever(api.getNotes()).thenReturn(listOf(
            NoteApiModel(1L, "To be added to database"),
            NoteApiModel(2L, "To be updated in database"),
            NoteApiModel(3L, "To be updated from database"),
            NoteApiModel(4L, "To be deleted in API")
        ))
        whenever(notesDao.all()).thenReturn(listOf(
            NoteDbEntity(1L, -1L, "To be added to server", false, false),
            NoteDbEntity(2L, 2L, "To be updated from API", false, false),
            NoteDbEntity(3L, 3L, "To be updated in API", true, false),
            NoteDbEntity(4L, 4L, "To be deleted in API", false, true),
            NoteDbEntity(5L, 5L, "To be deleted in database because it's no longer in API",
                false, true)
        ))
        notesSynchronizer.synchronize()
        verify(api).getNotes()
        verify(notesDao).all()
        argumentCaptor<NoteApiModel>().apply {
            verify(api).insertNote(capture())
            firstValue.apply {
                firstValue.id shouldEqual -1L
                firstValue.title shouldEqual "To be added to server"
            }
        }
        argumentCaptor<NoteApiModel>().apply {
            verify(api).updateNote(eq(3L), capture())
            firstValue.apply {
                firstValue.title shouldEqual "To be updated in API"
            }
        }
        argumentCaptor<Long>().apply {
            verify(api).deleteNote(capture())
            firstValue shouldEqual 4L
        }

        argumentCaptor<NoteDbEntity>().apply {

            verify(notesDao).insert(capture())
            allValues[0].apply {
                id shouldEqual null
                serverId shouldEqual 1L
                text shouldEqual "To be added to database"
                dirty shouldEqual false
                deleted shouldEqual false
            }
        }
        argumentCaptor<NoteDbEntity>().apply {
            verify(notesDao, times(3)).update(capture())
            allValues[0].apply {
                id shouldEqual 2L
                serverId shouldEqual 2L
                text shouldEqual "To be updated in database"
                dirty shouldEqual false
                deleted shouldEqual false
            }
            allValues[1].apply {
                id shouldEqual 4L
                deleted shouldEqual true
            }
            allValues[2].apply {
                id shouldEqual 1L
                serverId shouldEqual idGeneratorSeed
                text shouldEqual "To be added to server"
                dirty shouldEqual false
                deleted shouldEqual false
            }
        }
        argumentCaptor<NoteDbEntity>().apply {
            verify(notesDao).delete(capture())
            firstValue.id shouldEqual 5L
        }
        verify(notesDao).clearDeleted()
        verifyNoMoreInteractions(api)
        verifyNoMoreInteractions(notesDao)
    }
}
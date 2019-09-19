package cz.dmn.display.mynotes.mvvm

import cz.dmn.display.mynotes.TestCoroutineContextProvider
import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NotesDatabase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NotesSynchronizerTest {

    private lateinit var notesSynchronizer: NotesSynchronizer
    @Mock internal lateinit var api: NotesApi
    @Mock internal lateinit var database: NotesDatabase
    @Mock internal lateinit var notesDataConverter: NotesDataConverter

    @Before
    fun setUp() {
        notesSynchronizer = NotesSynchronizer(api, database, notesDataConverter,
            TestCoroutineContextProvider())
    }

    @Test
    fun synchronize() {
    }
}
package cz.dmn.display.mynotes.mvvm

import cz.dmn.display.mynotes.api.NoteApiModel
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class NotesDataConverterTest {

    companion object {
        private const val ID = 2L
        private const val SERVER_ID = 1L
        private const val TITLE = "Title"
        private const val TEXT = "This is a note text"
        private const val NOTE_CONTENT = "$TITLE\n$TEXT"
    }

    private lateinit var notesDataConverter: NotesDataConverter

    @Before
    fun setUp() {
        notesDataConverter = NotesDataConverter()
    }

    @Test
    fun toDbEntity() {
        val apiModel = NoteApiModel(SERVER_ID, NOTE_CONTENT)
        val dbEntity = notesDataConverter.toDbEntity(apiModel)
        dbEntity.id shouldEqual null
        dbEntity.serverId shouldEqual SERVER_ID
        dbEntity.text shouldEqual NOTE_CONTENT
    }

    @Test
    fun toUiModel() {
        val dbEntity = NoteDbEntity(ID, SERVER_ID, NOTE_CONTENT)
        val uiModel = notesDataConverter.toUiModel(dbEntity)
        uiModel.id shouldEqual ID
        uiModel.title shouldEqual TITLE
        uiModel.text shouldEqual TEXT
    }

    @Test
    fun toUiModelShortContent() {
        val dbEntity = NoteDbEntity(ID, SERVER_ID, TEXT)
        val uiModel = notesDataConverter.toUiModel(dbEntity)
        uiModel.title shouldEqual TEXT
        uiModel.text shouldEqual ""
    }

    @Test
    fun toUiModelNullId() {
        val dbEntity = NoteDbEntity(null, SERVER_ID, NOTE_CONTENT)
        val uiModel = notesDataConverter.toUiModel(dbEntity)
        uiModel.id shouldEqual -1L
    }

    @Test
    fun toApiModel() {
        val dbEntity = NoteDbEntity(null, SERVER_ID, NOTE_CONTENT)
        val apiModel = notesDataConverter.toApiModel(dbEntity)
        apiModel.id shouldEqual SERVER_ID
        apiModel.title shouldEqual NOTE_CONTENT
    }
}
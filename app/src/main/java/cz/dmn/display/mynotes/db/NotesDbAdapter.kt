package cz.dmn.display.mynotes.db

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDbAdapter @Inject constructor(
    private val database: NotesDatabase
) {

    val data = database.notes.queryAll()

    suspend fun addNote(note: NoteDbEntity) = database.notes.insert(note)

    suspend fun update(note: NoteDbEntity) = database.notes.update(note)

    suspend fun delete(note: NoteDbEntity) = database.notes.delete(note)

    suspend fun getNoteImmediately(id: Long) = database.notes.queryNoteImmediately(id)
}
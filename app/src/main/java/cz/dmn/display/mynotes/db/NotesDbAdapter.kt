package cz.dmn.display.mynotes.db

import cz.dmn.display.mynotes.api.NoteApiModel
import cz.dmn.display.mynotes.mvvm.NotesDataConverter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDbAdapter @Inject constructor(
    private val database: NotesDatabase,
    private val notesDataConverter: NotesDataConverter
) {

    val data = database.notes.queryAll()

    fun updateData(data: List<NoteApiModel>) {
        database.notes.deleteAll()
        database.notes.insert(data.map { notesDataConverter.toDbEntity(it) })
    }

    fun addNote(note: NoteDbEntity) = database.notes.insert(note)
}
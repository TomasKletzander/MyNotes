package cz.dmn.display.mynotes.db

import cz.dmn.display.mynotes.api.NoteApiModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDbAdapter @Inject constructor(private val database: NotesDatabase) {

    val data = database.notes.queryAll()

    fun updateData(data: List<NoteApiModel>) {
        database.notes.deleteAll()
        database.notes.insert(data.map { NoteDbEntity(null, it.id, it.title) })
    }
}
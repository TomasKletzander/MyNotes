package cz.dmn.display.mynotes.mvvm

import cz.dmn.display.mynotes.api.NotesApi
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.db.NotesDatabase
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesSynchronizer @Inject constructor(
    private val api: NotesApi,
    private val database: NotesDatabase,
    private val notesDataConverter: NotesDataConverter,
    private val coroutineContextProvider: CoroutineContextProvider
) {

    suspend fun synchronize() {

        //  Retrieve data from API and database in IO thread
        val (apiNotes, dbNotes) = withContext(coroutineContextProvider.IO) {
            Pair(api.getNotes(), database.notes.all())
        }

        //  Prepare updates in computational thread
        val apiInsert = mutableListOf<NoteDbEntity>()
        val apiUpdate = mutableListOf<NoteDbEntity>()
        val apiDelete = mutableListOf<NoteDbEntity>()
        val dbInsert = mutableListOf<NoteDbEntity>()
        val dbUpdate = mutableListOf<NoteDbEntity>()
        val dbDelete = mutableListOf<NoteDbEntity>()
        withContext(coroutineContextProvider.Default) {

            //  Insert to database all API notes not existing in database already
            apiNotes.filterNot { apiNote ->
                dbNotes.any { dbNote -> dbNote.serverId == apiNote.id }
            }.mapTo(dbInsert) {
                notesDataConverter.toDbEntity(it)
            }
            //  Delete from database all notes that are not within API models but are not new (have serverId)
            dbNotes.filterTo(dbDelete) { dbNote ->
                apiNotes.none { apiNote -> apiNote.id == dbNote.serverId } && dbNote.serverId >= 0
            }
            //  Update in database all notes that exist in API data and are not dirty (edited locally)
            dbNotes.map {
                dbNote ->
                Pair(dbNote, apiNotes.firstOrNull { apiNote -> apiNote.id == dbNote.serverId })
            }.filter {
                !it.first.dirty && it.second != null
            }.mapTo(dbUpdate) {
                it.first.copy(text = it.second!!.title)
            }

            //  POST all notes from local database that don't have server ID
            dbNotes.filterTo(apiInsert) { dbNote -> dbNote.serverId < 0 }
            //  Update all notes from local database that are found in API data and have dirty flag
            dbNotes.filterTo(apiUpdate) { dbNote ->
                dbNote.dirty && apiNotes.any { apiNote -> apiNote.id == dbNote.serverId }
            }
            //  Delete all notes that are marked in local database as deleted and have server ID
            dbNotes.filterTo(apiDelete) {
                dbNote -> dbNote.deleted && dbNote.serverId >= 0 &&
                apiNotes.any { apiNote -> apiNote.id == dbNote.serverId }
            }
        }

        //  Update database and send API updates in IO thread
        withContext(coroutineContextProvider.IO) {

            dbInsert.forEach { database.notes.insert(it) }
            dbUpdate.forEach { database.notes.update(it) }
            dbDelete.forEach { database.notes.delete(it) }

            apiInsert.forEach {
                val apiModel = api.insertNote(notesDataConverter.toApiModel(it))
                database.notes.update(it.copy(serverId = apiModel.id))
            }
            apiUpdate.forEach { api.updateNote(it.serverId, notesDataConverter.toApiModel(it)) }
            apiDelete.forEach { api.deleteNote(it.serverId) }

            database.notes.clearDeleted()
        }
    }
}
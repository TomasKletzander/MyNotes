package cz.dmn.display.mynotes.api

import retrofit2.http.GET

interface NotesApi {

    @GET("/notes")
    suspend fun getNotes(): List<NoteApiModel>
}
package cz.dmn.display.mynotes.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApi {

    @GET("/notes")
    suspend fun getNotes(): List<NoteApiModel>

    @POST("/notes")
    suspend fun insertNote(@Body data: NoteApiModel): NoteApiModel

    @PUT("/notes/:id")
    suspend fun updateNote(@Path("id") id: Long, @Body data: NoteApiModel): NoteApiModel
}
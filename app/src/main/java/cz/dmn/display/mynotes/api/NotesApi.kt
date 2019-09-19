package cz.dmn.display.mynotes.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotesApi {

    @GET("/notes")
    suspend fun getNotes(): List<NoteApiModel>

    @POST("/notes")
    suspend fun insertNote(@Body data: NoteApiModel): NoteApiModel

    @PUT("/notes/{id}")
    suspend fun updateNote(@Path("id") id: Long, @Body data: NoteApiModel): NoteApiModel

    @DELETE("/notes/{id}")
    suspend fun deleteNote(@Path("id") id: Long): Response<ResponseBody>
}
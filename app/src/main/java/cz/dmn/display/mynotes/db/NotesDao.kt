package cz.dmn.display.mynotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {

    @Query("select * from notes where deleted=0")
    fun allLive(): LiveData<List<NoteDbEntity>>

    @Query("select * from notes")
    suspend fun all(): List<NoteDbEntity>

    @Query("delete from notes")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun delete(note: NoteDbEntity)

    @Insert
    suspend fun insert(data: List<NoteDbEntity>)

    @Insert
    suspend fun insert(note: NoteDbEntity): Long

    @Update
    suspend fun update(note: NoteDbEntity)

    @Query("select * from notes where id=:id")
    suspend fun get(id: Long): NoteDbEntity?

    @Query("delete from notes where deleted=1")
    suspend fun clearDeleted()
}
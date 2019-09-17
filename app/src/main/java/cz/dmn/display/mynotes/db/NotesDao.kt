package cz.dmn.display.mynotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface NotesDao {

    @Query("select * from notes")
    fun queryAll(): LiveData<List<NoteDbEntity>>

    @Query("delete from notes")
    suspend fun deleteAll(): Int

    @Insert
    suspend fun insert(data: List<NoteDbEntity>)

    @Insert
    suspend fun insert(note: NoteDbEntity): Long

    @Update
    suspend fun update(note: NoteDbEntity)
}
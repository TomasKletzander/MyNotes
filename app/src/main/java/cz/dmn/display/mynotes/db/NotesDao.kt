package cz.dmn.display.mynotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NotesDao {

    @Query("select * from notes")
    fun queryAll(): LiveData<List<NoteDbEntity>>

    @Query("delete from notes")
    fun deleteAll()

    @Insert
    fun insert(data: List<NoteDbEntity>)
}
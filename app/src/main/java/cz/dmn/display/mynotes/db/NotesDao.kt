package cz.dmn.display.mynotes.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface NotesDao {

    @Query("select * from notes")
    fun queryAll(): LiveData<List<NoteDbEntity>>
}
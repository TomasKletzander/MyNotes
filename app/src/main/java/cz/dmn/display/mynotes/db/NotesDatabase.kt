package cz.dmn.display.mynotes.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NoteDbEntity::class], version = 1)
abstract class NotesDatabase : RoomDatabase() {
    abstract val notes: NotesDao
}
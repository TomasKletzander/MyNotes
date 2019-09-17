package cz.dmn.display.mynotes.mvvm

import cz.dmn.display.mynotes.api.NoteApiModel
import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.ui.NoteUiModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesDataConverter @Inject constructor() {

    fun toDbEntity(apiModel: NoteApiModel) = NoteDbEntity(null, apiModel.id, apiModel.title)

    fun toUiModel(dbEntity: NoteDbEntity): NoteUiModel {
        val parts = dbEntity.text.split('\r', '\n', limit = 2)
        return NoteUiModel(dbEntity.id ?: -1, parts[0], if (parts.size == 2) parts[1] else "")
    }
}
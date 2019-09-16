package cz.dmn.display.mynotes

import cz.dmn.display.mynotes.db.NoteDbEntity
import cz.dmn.display.mynotes.ui.NoteUiModel

fun NoteDbEntity.toUiModel(): NoteUiModel {
    val parts = text.split(" ", limit = 2)
    return NoteUiModel(parts[0], if (parts.size == 2) parts[1] else "")
}
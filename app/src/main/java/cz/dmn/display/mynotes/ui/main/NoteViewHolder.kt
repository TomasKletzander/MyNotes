package cz.dmn.display.mynotes.ui.main

import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.databinding.ItemNoteBinding

class NoteViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

    var id = 0L

    fun bind(model: NoteUiModel) {
        id = model.id
        binding.title.text = model.title
        binding.text.text = model.text
    }
}
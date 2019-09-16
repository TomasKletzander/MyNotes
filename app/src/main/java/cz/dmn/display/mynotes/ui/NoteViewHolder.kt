package cz.dmn.display.mynotes.ui

import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.databinding.ItemNoteBinding

class NoteViewHolder (private val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: NoteUiModel) {
        binding.title.text = model.title
        binding.text.text = model.text
    }
}
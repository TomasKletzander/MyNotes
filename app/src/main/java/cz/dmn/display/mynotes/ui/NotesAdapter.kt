package cz.dmn.display.mynotes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.databinding.ItemNoteBinding
import cz.dmn.display.mynotes.di.PerActivity
import javax.inject.Inject

@PerActivity
class NotesAdapter @Inject constructor(
    private val inflater: LayoutInflater,
    private val recycler: RecyclerView,
    private val noteClickListener: NoteClickListener
) : RecyclerView.Adapter<NoteViewHolder>() {

    private val models = mutableListOf<NoteUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
        = NoteViewHolder(ItemNoteBinding.inflate(inflater, recycler, false)).apply {
        binding.root.setOnClickListener { noteClickListener.onNoteClick(id) }
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) = holder.bind(models[position])

    override fun getItemCount() = models.size

    fun updateModels(models: List<NoteUiModel>) {
        this.models.clear()
        this.models.addAll(models)
    }

    fun findPositionOfId(id: Long) = models.indexOfFirst { it.id == id }
}
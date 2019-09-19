package cz.dmn.display.mynotes.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.databinding.ItemNoteBinding
import cz.dmn.display.mynotes.di.PerActivity
import dagger.Lazy
import javax.inject.Inject

@PerActivity
class NotesAdapter @Inject constructor(
    private val inflater: LayoutInflater,
    private val recyclerLazy: Lazy<RecyclerView>,
    private val noteClickListener: NoteClickListener
) : RecyclerView.Adapter<NoteViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val models = mutableListOf<NoteUiModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        NoteViewHolder(
            ItemNoteBinding.inflate(
                inflater,
                recyclerLazy.get(),
                false
            )
        ).apply {
            binding.root.setOnClickListener { noteClickListener.onNoteClick(id) }
        }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) =
        holder.bind(models[position])

    override fun getItemCount() = models.size

    override fun getItemId(position: Int) = models[position].id

    fun updateModels(models: List<NoteUiModel>) {
        this.models.clear()
        this.models.addAll(models)
        notifyDataSetChanged()
    }

    operator fun get(index: Int) = models[index]

    fun removeModelAt(index: Int) {
        models.removeAt(index)
        notifyItemRemoved(index)
    }
}
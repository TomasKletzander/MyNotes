package cz.dmn.display.mynotes.ui.main

import android.graphics.Canvas
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.di.PerActivity
import cz.dmn.display.mynotes.mvvm.NotesViewModel
import javax.inject.Inject

@PerActivity
class SwipeToDeleteHandler @Inject constructor(
    private val activity: AppCompatActivity,
    private val viewModel: NotesViewModel,
    private val adapter: NotesAdapter
) : ItemTouchHelper.SimpleCallback(
    0,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
) {

    private val icon = DrawableCompat.wrap(
        AppCompatResources.getDrawable(activity, R.drawable.trash_can)!!).also {
            DrawableCompat.setTint(
                it,
                ResourcesCompat.getColor(activity.resources, R.color.colorAccent, activity.theme)
            )
        }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        viewModel.removeNoteById(adapter[position].id)
        adapter.removeModelAt(position)
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconLeft = (
            if (dX > 0) {
                itemView.left + dX / 5
            } else if (dX < 0) {
                itemView.right - icon.intrinsicWidth + dX / 5
            } else return
            ).toInt()
        val iconTop = (itemView.top + itemView.bottom - icon.intrinsicHeight) / 2
        icon.setBounds(iconLeft, iconTop,
            iconLeft + icon.intrinsicWidth, iconTop + icon.intrinsicHeight)
        icon.draw(canvas)
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
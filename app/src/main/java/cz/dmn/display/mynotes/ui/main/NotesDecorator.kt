package cz.dmn.display.mynotes.ui.main

import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import cz.dmn.display.mynotes.R
import cz.dmn.display.mynotes.di.PerActivity
import dagger.Lazy
import javax.inject.Inject

@PerActivity
class NotesDecorator @Inject constructor(
    activity: AppCompatActivity,
    private val notesAdapterLazy: Lazy<NotesAdapter>
) : RecyclerView.ItemDecoration() {

    private val margin = activity.resources.getDimensionPixelSize(R.dimen.margin_large)

    override fun getItemOffsets(outRect: Rect, itemPosition: Int, parent: RecyclerView) {
        outRect.left = margin
        outRect.right = margin
        outRect.top = margin
        if (itemPosition == notesAdapterLazy.get().itemCount - 1) {
            outRect.bottom = margin
        }
    }
}
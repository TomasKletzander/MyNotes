package cz.dmn.display.mynotes.ui.main

import android.content.res.Resources
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.nhaarman.mockitokotlin2.whenever
import cz.dmn.display.mynotes.shouldEqual
import dagger.Lazy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.Mockito.anyInt

@RunWith(MockitoJUnitRunner::class)
class NotesDecoratorTest {

    companion object {
        private const val MARGIN = 4
        private const val SIZE = 2
    }

    private lateinit var notesDecorator: NotesDecorator
    @Mock internal lateinit var activity: AppCompatActivity
    @Mock internal lateinit var resources: Resources
    @Mock internal lateinit var notesAdapterLazy: Lazy<NotesAdapter>
    @Mock internal lateinit var notesAdapter: NotesAdapter
    @Mock internal lateinit var recycler: RecyclerView
    @Mock internal lateinit var outRect: Rect

    @Before
    fun setUp() {
        whenever(activity.resources).thenReturn(resources)
        whenever(resources.getDimensionPixelSize(anyInt())).thenReturn(MARGIN)
        whenever(notesAdapterLazy.get()).thenReturn(notesAdapter)
        whenever(notesAdapter.itemCount).thenReturn(SIZE)
        notesDecorator = NotesDecorator(activity, notesAdapterLazy)
    }

    @Test
    fun getItemOffsets() {

        //  1st item has offsets everywhere except bottom
        outRect.left = 0
        outRect.top = 0
        outRect.right = 0
        outRect.bottom = 0
        notesDecorator.getItemOffsets(outRect, 0, recycler)
        outRect.left shouldEqual MARGIN
        outRect.top shouldEqual MARGIN
        outRect.right shouldEqual MARGIN
        outRect.bottom shouldEqual MARGIN

        //  Last item should have offsets everywhere
        outRect.left = 0
        outRect.top = 0
        outRect.right = 0
        outRect.bottom = 0
        notesDecorator.getItemOffsets(outRect, 1, recycler)
        outRect.left shouldEqual MARGIN
        outRect.top shouldEqual MARGIN
        outRect.right shouldEqual MARGIN
        outRect.bottom shouldEqual MARGIN
    }
}
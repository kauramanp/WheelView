package com.aman.wheelview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: Amanpreet Kaur
 * @Date: 30-04-2025 12:43
 */

/**
 * A custom wheel-style picker view similar to iOS-style spinners.
 * Shows a vertically scrollable list of items where the center item is considered "selected".
 * Supports smooth scrolling with snap-to-center behavior and center item highlighting.
 *
 * @param context Context in which the view is running
 * @param attrs Optional XML attributes
 */
class CustomWheelPicker @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val recyclerView: RecyclerView
    private val layoutManager: LinearLayoutManager
    private val snapHelper = LinearSnapHelper()

    private var currentIndex = 0
    private var values: List<NameDataClass> = emptyList()
    private lateinit var adapter: WheelPickerAdapter

    /**
     * Called when the centered value changes during scroll.
     */
    var onValueChanged: ((NameDataClass) -> Unit)? = null

    /**
     * Called when a user clicks on any item in the list.
     */
    var onValueClicked: ((Int) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_wheel_picker, this, true)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        snapHelper.attachToRecyclerView(recyclerView)

        // Respond to scroll events to track the centered item in real-time
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(rv, dx, dy)
                val view = snapHelper.findSnapView(layoutManager) ?: return
                val pos = layoutManager.getPosition(view)

                if (pos != RecyclerView.NO_POSITION && pos != currentIndex) {
                    val oldIndex = currentIndex
                    currentIndex = pos

                    // Ensure adapter update is safe during scroll
                    rv.post {
                        adapter.notifyItemChanged(oldIndex)
                        adapter.notifyItemChanged(currentIndex)
                    }

                    onValueChanged?.invoke(values[pos])
                }
            }

            //to highlight the current item only
            /*  override fun onScrollStateChanged(rv: RecyclerView, newState: Int) {
                  if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                      val view = snapHelper.findSnapView(layoutManager) ?: return
                      val pos = recyclerView.getChildAdapterPosition(view)
                      if (pos != RecyclerView.NO_POSITION && pos != currentIndex) {
                          currentIndex = pos
                          onValueChanged?.invoke(values[pos])
                          adapter.notifyDataSetChanged()
                      }
                  }
              }*/

        })
    }

    /**
     * Sets the list of items to show in the wheel picker.
     * Automatically scrolls to the first item and adjusts layout height based on item height.
     */
    fun setItems(items: List<NameDataClass>) {
        values = items
        currentIndex = 0

        adapter = WheelPickerAdapter(
            items = values,
            getCenterPosition = { currentIndex },
            onItemClicked = { itemIndex -> onValueClicked?.invoke(itemIndex) }
        )

        recyclerView.adapter = adapter
        recyclerView.scrollToPosition(currentIndex)

        // Dynamically set RecyclerView height to show 3 items without hardcoding
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val firstChild = recyclerView.getChildAt(0) ?: return
                    val itemHeight = firstChild.height

                    if (itemHeight > 0) {
                        val visibleCount = 3
                        val desiredHeight = itemHeight * visibleCount

                        recyclerView.layoutParams = recyclerView.layoutParams.apply {
                            height = desiredHeight
                        }

                        // Padding ensures the center item stays in the middle
                        recyclerView.setPadding(0, itemHeight, 0, itemHeight)
                    }
                }
            }
        )
    }

    /**
     * Returns the currently selected (centered) item.
     */
    fun getValue(): NameDataClass = values.getOrElse(currentIndex) { NameDataClass() }

    /**
     * Updates the list contents without resetting scroll.
     * Use this to refresh the list after changes.
     */
    fun updateItems(items: List<NameDataClass>) {
        values = items
        adapter.notifyDataSetChanged()
    }
}


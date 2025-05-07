package com.aman.wheelview

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: Amanpreet Kaur
 * @Date: 30-04-2025 12:38
 */
/**
 * Adapter for a vertical wheel-style RecyclerView picker.
 * Highlights and animates the center item using scaling and opacity.
 *
 * @param items List of items to show.
 * @param getCenterPosition Lambda that returns the current center item index.
 * @param onItemClicked Callback for item click events.
 */
class WheelPickerAdapter(
    private val items: List<NameDataClass>,
    private val getCenterPosition: () -> Int,
    private inline val onItemClicked: (Int) -> Unit,
) : RecyclerView.Adapter<WheelPickerAdapter.ViewHolder>() {

    /**
     * ViewHolder for individual wheel picker items.
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tvName)
        val imageTick: ImageView = view.findViewById(R.id.imageTick)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wheel_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val centerIndex = getCenterPosition()
        val item = items[position]

        // Set text
        holder.textView.text = item.name
        holder.textView.setTextColor(if (position == centerIndex) Color.BLACK else Color.GRAY)
        holder.textView.setTypeface(null, if (position == centerIndex) Typeface.BOLD else Typeface.NORMAL)
        holder.textView.textSize = if (position == centerIndex) 22f else 18f

        // Show/hide tick icon
        holder.imageTick.visibility = if (item.isSelected) View.VISIBLE else View.GONE

        // Handle item click
        holder.itemView.setOnClickListener {
            if (position == centerIndex)
                onItemClicked.invoke(position)
        }

        // Determine target alpha and scale based on position
        val (targetAlpha, targetScale) = when (position) {
            centerIndex -> 1f to 1.0f
            centerIndex - 1, centerIndex + 1 -> 0.5f to 0.9f
            else -> 0.2f to 0.85f
        }

        // Animate alpha and scale
        holder.itemView.animate()
            .alpha(targetAlpha)
            .scaleX(targetScale)
            .scaleY(targetScale)
            .setDuration(200)
            .start()
    }

    override fun getItemCount(): Int = items.size
}

package com.jskaleel.fte.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.entities.LocalBooks

class HomeListAdapter(
    private val mContext: Context,
    private val listener: BookClickListener,
    private val booksList: MutableList<LocalBooks>,
    private val type: Int
) : RecyclerView.Adapter<BookViewHolder>() {
    private var previousClickedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.book_list_item, parent, false)
        val lp = view.layoutParams as StaggeredGridLayoutManager.LayoutParams
        lp.height = if (type == 1) parent.measuredHeight / 3 else (parent.measuredHeight / 2.5).toInt()
        view.layoutParams = lp
        return BookViewHolder(mContext, view)
    }

    override fun getItemCount(): Int {
        return booksList.size
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val bookItem = booksList[position]
        holder.bindData(bookItem)
        holder.itemView.setOnClickListener {
            if (previousClickedPosition == position) {
                return@setOnClickListener
            }
            if (previousClickedPosition != -1) {
                booksList[previousClickedPosition].isExpanded = false
                notifyItemChanged(previousClickedPosition)
            }
            previousClickedPosition = position
            val expanded = bookItem.isExpanded
            bookItem.isExpanded = !expanded
            notifyItemChanged(position)
        }
    }

    fun loadBooks(books: List<LocalBooks>) {
        booksList.clear()
        booksList.addAll(books)
        notifyDataSetChanged()
    }

    fun clearBooks() {
        booksList.clear()
        notifyDataSetChanged()
    }
}
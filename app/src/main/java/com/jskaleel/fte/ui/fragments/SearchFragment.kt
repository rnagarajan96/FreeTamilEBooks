package com.jskaleel.fte.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.jskaleel.fte.R
import com.jskaleel.fte.database.AppDatabase
import com.jskaleel.fte.database.entities.LocalBooks
import com.jskaleel.fte.ui.base.BookClickListener
import com.jskaleel.fte.ui.base.BookListAdapter
import com.jskaleel.fte.utils.DeviceUtils
import com.jskaleel.fte.utils.PrintLog
import com.jskaleel.fte.utils.downloader.DownloadUtil
import kotlinx.android.synthetic.main.fragment_search.*
import android.app.AlertDialog
import android.util.Log
import android.view.*

import org.geometerplus.android.fbreader.util.FBReaderPercentUtils
import com.jskaleel.fte.ui.activities.MainActivity

import android.widget.PopupMenu


class SearchFragment : Fragment(), BookClickListener {
    override fun bookItemClickListener(adapterPosition: Int, book: LocalBooks) {
        PrintLog.info("Search adapterPosition $adapterPosition ${book.title}")
        if (book.isDownloaded) {
            DownloadUtil.openSavedBook(mContext, book)
        } else {
            if (book.downloadId == 0L) {
                val downloadID = DownloadUtil.queueForDownload(mContext, book)
                adapter.updateDownloadId(adapterPosition, downloadID)
            }
        }
    }

    private lateinit var adapter: BookListAdapter
    private lateinit var searchHandler: Handler
    private lateinit var mContext: Context
    private var filterType = 1
    private val triggerNewService = 1001
    private val TitleName = arrayOf(
        "நூல் பெயர்","நூல் ஆசிரியர்"
    )


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolBar.setNavigationOnClickListener {
            DeviceUtils.hideSoftKeyboard(activity!!)
            activity!!.findNavController(R.id.navHostFragment).navigateUp()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rvSearchList.setHasFixedSize(true)
        adapter = BookListAdapter(mContext, this@SearchFragment, mutableListOf(), 2)
        val layoutManger = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
        rvSearchList.layoutManager = layoutManger
        rvSearchList.adapter = adapter

        edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_menu_search, 0)

        val appDataBase = AppDatabase.getAppDatabase(mContext)
        searchHandler = @SuppressLint("HandlerLeak")
        object : Handler() {
            override fun handleMessage(msg: Message?) {
                if (msg != null) {
                    if (msg.what == triggerNewService && edtSearch != null && isAdded) {
                        val query = edtSearch.text.toString()
                        when (filterType) {
                            1 -> {
                                val books = appDataBase.localBooksDao().getBooksByTitle("%$query%")
                                PrintLog.info("Books by Title $books")
                                loadBooks(books)
                            }
                            2 -> {
                                val books = appDataBase.localBooksDao().getBooksByAuthor("%$query%")
                                PrintLog.info("Books by Author $books")
                                loadBooks(books)
                            }
                        }
                    }
                }
            }
        }

        edtSearch.tag = "FILTER"
        edtSearch.setOnTouchListener(OnTouchListener { _, event ->
            val drawableRight = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= edtSearch.right - edtSearch.compoundDrawables[drawableRight].bounds.width()) {
                    when (edtSearch.tag) {
                        "CLEAR" -> {
                            edtSearch.text?.clear()
                        }
                        "FILTER" -> {
                            val popup = PopupMenu(activity, searchView,Gravity.END)
                            popup.inflate(R.menu.popup_filter)
                            val query = edtSearch.text.toString()
                            popup.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                                override fun onMenuItemClick(item: MenuItem): Boolean {
                                    when (item.getItemId()) {
                                        R.id.title -> {
                                            filterType = 1
                                            val books = appDataBase.localBooksDao().getBooksByTitle("%$query%")
                                            PrintLog.info("Books by Title $books")
                                            loadBooks(books)
                                            return true
                                        }
                                        R.id.author -> {
                                            filterType = 2
                                            val books = appDataBase.localBooksDao().getBooksByAuthor("%$query%")
                                            PrintLog.info("Books by Author $books")
                                            loadBooks(books)
                                            return true
                                        }
                                        else -> return false
                                    }
                                }
                            })
                            popup.show()
                        }
                    }
                    return@OnTouchListener true
                }
            }
            false
        })

        edtSearch.doAfterTextChanged { edt ->
            if (edt != null) {
                val query = edt.toString().trim()
                if (query.isNotBlank() && query.isNotEmpty()) {
                    if (query.length > 3) {
                        PrintLog.info("Text ${edt.toString()}")

                        searchHandler.removeMessages(triggerNewService)
                        searchHandler.sendEmptyMessageDelayed(triggerNewService, 1000)
                    }
                    edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_clear_black_24dp, 0)
                    edtSearch.tag = "CLEAR"
                } else {
                    edtSearch.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_tune_black_24dp, 0)
                    edtSearch.tag = "FILTER"
                    searchHandler.removeMessages(triggerNewService)
                    adapter.clearBooks()
                }
            }
        }
    }

    private fun loadBooks(books: List<LocalBooks>) {
        adapter.loadBooks(books)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }
}
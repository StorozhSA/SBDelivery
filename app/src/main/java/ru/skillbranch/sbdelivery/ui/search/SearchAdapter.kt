package ru.skillbranch.sbdelivery.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.sbdelivery.databinding.ItemSearchHistoryBinding

class SearchAdapter(
    private val listenerOnClickText: (String) -> Unit,
    private val listenerOnClickClose: (String) -> Unit
) : ListAdapter<String, SearchAdapter.Holder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemSearchHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: Holder, pos: Int) =
        holder.bind(getItem(pos), listenerOnClickText, listenerOnClickClose)

    class Holder(private val vb: ItemSearchHistoryBinding) : RecyclerView.ViewHolder(vb.root) {
        fun bind(
            item: String,
            listenerOnClickText: (String) -> Unit,
            listenerOnClickClose: (String) -> Unit
        ) {
            vb.historyTvText.text = item
            vb.historyTvText.setOnClickListener {
                listenerOnClickText(item)
            }
            vb.historyIvClose.setOnClickListener {
                listenerOnClickClose(item)
            }
            // itemView.setOnClickListener { listener(item) }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(o: String, n: String) = (o == n)
            override fun areContentsTheSame(o: String, n: String) = (o == n)
        }
    }
}

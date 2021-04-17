package tech.tucano.pmp_politie.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_topic.view.*
import tech.tucano.pmp_politie.DataModel.SearchModel
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.ItemTopicBinding


class SearchListAdapter (private val searchlist: List<SearchModel>, private val clickListener: (SearchModel) -> Unit)
    : RecyclerView.Adapter<SearchListAdapter.SearchListViewHolder>() {

    class SearchListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun databind(searchModel: SearchModel, clickListener: (SearchModel) -> Unit) {
            itemView.tvTopic.text = searchModel.articleText
            itemView.setOnClickListener { clickListener(searchModel)}
            itemView.imageButton.setOnClickListener({ clickListener(searchModel)})
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_topic,
            parent,
            false
        )
        return SearchListViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchListViewHolder, position: Int) {
       holder.databind(searchlist[position], clickListener)
    }

    override fun getItemCount(): Int {
        return searchlist.size
    }


}
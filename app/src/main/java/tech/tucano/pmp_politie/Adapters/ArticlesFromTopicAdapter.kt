package tech.tucano.pmp_politie.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_topic.view.*
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.ItemTopicBinding

class ArticlesFromTopicAdapter(private val topics: List<String>, private val clickListener: (String) -> Unit) :
    RecyclerView.Adapter<ArticlesFromTopicAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemTopicBinding.bind(itemView)

        fun databind(topic: String, clickListener: (String) -> Unit) {
            binding.tvTopic.text = topic
            itemView.setOnClickListener { clickListener(topic)}
            itemView.imageButton.setOnClickListener({ clickListener(topic)})
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_topic,
                parent,
                false
            )
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return topics.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(topics[position], clickListener)
    }
}
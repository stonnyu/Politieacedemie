package tech.tucano.pmp_politie.Adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import tech.tucano.pmp_politie.DataModel.Article
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.ItemRecentBinding

class ArticleAdapter(
    private val articles: List<Article>,
    private val clickListener: (Article) -> Unit
) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ItemRecentBinding.bind(itemView)

        fun databind(article: Article, clickListener: (Article) -> Unit) {
            binding.tvTitle.text = article.articleTitle
            itemView.setOnClickListener { clickListener(article) }

            if(article.articleBannerName != null)
                loadBanner(article.articleBannerName!!)
        }

        private fun loadBanner(articleBanner: String) {
            if(articleBanner.isEmpty())
                return

            val storageReference = Firebase.storage.reference

            storageReference.child("thumb/$articleBanner").downloadUrl.addOnSuccessListener {

            }.addOnFailureListener {
                // Handle any errors
            }

            storageReference.child("thumb/$articleBanner").getBytes(Long.MAX_VALUE).addOnSuccessListener {
                binding.ivPreview.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }.addOnFailureListener {
                // Handle any errors
            }
        }
    }

    /**
     * Creates and returns a ViewHolder object, inflating a standard layout called simple_list_item_1.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_recent,
                parent,
                false
            )
        )
    }

    /**
     * Returns the size of the list
     */
    override fun getItemCount(): Int {
        return articles.size
    }

    /**
     * Called by RecyclerView to display the data at the specified position.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.databind(articles[position], clickListener)
    }
}
package tech.tucano.pmp_politie.UI.Main

import android.app.AlertDialog.Builder
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_article.*
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentArticleBinding

const val ARG_RECENT_ARTICLE_TITLE = "arg_recent_article_title"
const val ARG_RECENT_ARTICLE_TEXT = "arg_recent_article_text"
const val ARG_RECENT_ARTICLE_BANNER = "arg_recent_article_banner"
const val ARG_RECENT_ARTICLE_ID = "arg_recent_article_id"
const val ARG_RECENT_ARTICLE_TOPIC = "arg_recent_article_topic"

class ArticleFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentArticleBinding
    private var articleId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentArticleBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        
        articleId = arguments?.getString(ARG_RECENT_ARTICLE_ID)

        setupArticlePage()

        if(requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)
                .getString(getString(R.string.guest), false.toString()) == true.toString())
                editingSection.visibility = View.GONE

        btnEdit.setOnClickListener {
            editArticle()
        }
        fabBack.setOnClickListener{
            findNavController().popBackStack()
        }

        // Setup article delete confirmation
        btnDelete.setOnClickListener{
            val alertDialog = Builder(requireContext())
            alertDialog.setMessage(getString(R.string.warning_delete_article))
            alertDialog.setNegativeButton(getString(R.string.no)) { _, _ -> }
            alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ -> deleteArticle()}
            alertDialog.show()
        }
    }

    private fun deleteArticle() {
        val database = FirebaseDatabase.getInstance()
        database.getReference(getString(R.string.db_article)).child(articleId!!).removeValue()

        val topic = requireArguments().getString(ARG_RECENT_ARTICLE_TOPIC)

        // Remove the article and it's data
        if(topic!!.contains(getString(R.string.db_confidential_identifier)))
            database.getReference(getString(R.string.db_confidential_topic))
                .child(topic.replace(getString(R.string.db_confidential_identifier), ""))
                .child(getString(R.string.db_topic_articles))
                .child(articleId!!).removeValue()
        else
            database.getReference(getString(R.string.db_topic))
                .child(topic)
                .child(getString(R.string.db_topic_articles))
                .child(articleId!!).removeValue()

        // Remove the articles banner
        deleteImage()

        findNavController().popBackStack()
    }

    private fun deleteImage() {
        val imageRef = requireArguments().getString(ARG_RECENT_ARTICLE_BANNER)
        if (imageRef.isNullOrEmpty())
            return

        val progressDialog = Builder(requireContext())
        progressDialog.setTitle(getString(R.string.add_article_uploading))
        progressDialog.create().show()

        // Give the image a random and set it's firebase reference
        val ref: StorageReference = Firebase.storage.reference.child("thumb/$imageRef")

        // Upload the image to firebase
        ref.delete()
            .addOnSuccessListener {
            }
            .addOnFailureListener { e ->
            }
    }

    private fun editArticle() {
        val database = FirebaseDatabase.getInstance()
        val topic = requireArguments().getString(ARG_RECENT_ARTICLE_TOPIC)

        if(topic!!.contains(getString(R.string.db_confidential_identifier))) {
            database.getReference(getString(R.string.db_confidential_topic))
                .child(topic.replace(getString(R.string.db_confidential_identifier), ""))
                .child(getString(R.string.db_topic_articles))
                .child(articleId!!).removeValue()
        }
        else {
            database.getReference(getString(R.string.db_topic))
                .child(topic)
                .child(getString(R.string.db_topic_articles))
                .child(articleId!!).removeValue()
        }

        navController.navigate(R.id.addArticleFragment, requireArguments())
    }

    private fun setupArticlePage() {
        val recentArticleTitle = arguments?.getString(ARG_RECENT_ARTICLE_TITLE)
        val recentArticleText = arguments?.getString(ARG_RECENT_ARTICLE_TEXT)
        articleId = arguments?.getString(ARG_RECENT_ARTICLE_ID)

        binding.tvTitle.text = recentArticleTitle
        binding.tvArticleText.setMarkDownText(recentArticleText)

        arguments?.getString(ARG_RECENT_ARTICLE_BANNER)?.let { loadBanner(it) }
    }

    /**
     * Load an article's the banner from firebase
     * @param articleBanner path of the image in firebase
     */
    private fun loadBanner(articleBanner: String) {
        if (articleBanner.isEmpty())
            return

        val storageReference = Firebase.storage.reference

        storageReference.child("thumb/$articleBanner").downloadUrl.addOnSuccessListener {

        }.addOnFailureListener {
            // Handle any errors
        }

        storageReference.child("thumb/$articleBanner").getBytes(Long.MAX_VALUE)
            .addOnSuccessListener {
                binding.ivBanner.setImageBitmap(BitmapFactory.decodeByteArray(it, 0, it.size))
            }.addOnFailureListener {
            // Handle any errors
        }
    }
}
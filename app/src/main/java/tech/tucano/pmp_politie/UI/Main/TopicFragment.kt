package tech.tucano.pmp_politie.UI.Main

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_topic_to_articles.*
import tech.tucano.pmp_politie.Adapters.ArticlesFromTopicAdapter
import tech.tucano.pmp_politie.DataModel.Article
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentTopicToArticlesBinding
import java.lang.NullPointerException


const val ARG_RECENT_TOPIC_TITLE = "arg_recent_topic_title"

class TopicFragment: Fragment() {

    private lateinit var navController: NavController
    private lateinit var binding: FragmentTopicToArticlesBinding
    private val articleTitlesITopics = arrayListOf<String>()
    private val articleIds = arrayListOf<String>()
    private val topicAdapter = ArticlesFromTopicAdapter(articleTitlesITopics){ portal: String ->
        articleClicked(
            portal
        )
    }

    private var database = FirebaseDatabase.getInstance()
    private var articleReference = database.reference.child("Article")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTopicToArticlesBinding.inflate(layoutInflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topicDataList: RecyclerView = binding.rvArticleList

        topicDataList.adapter = topicAdapter

        navController = findNavController()

        fabBack.setOnClickListener{
            findNavController().popBackStack()
        }

        initViews()
    }

    /**
     * Everything on the Topicfragment layout will be initialized.
     */
    private fun initViews() {
        var width = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            (context as Activity?)!!.windowManager.currentWindowMetrics.bounds.width()
        } else {
            val displayMetrics = DisplayMetrics()
            (context as Activity?)!!.windowManager
                .defaultDisplay
                .getMetrics(displayMetrics)

            width = displayMetrics.widthPixels
        }

        if(width > 1080)
            binding.rvArticleList.layoutManager = GridLayoutManager(requireActivity(), 3)
        else
            binding.rvArticleList.layoutManager = GridLayoutManager(requireActivity(), 1)

        binding.rvArticleList.adapter = topicAdapter
        topicAdapter.notifyDataSetChanged()
        val topicTitle = arguments?.getString(ARG_RECENT_TOPIC_TITLE)

        this@TopicFragment.articleTitlesITopics.clear()
        setTopics()
    }

    /**
     * The recyclerview items are getting loaded with different article titles on the specific topic
     * , which is already given by the HomeFragment.
     */
    private fun setTopics() {
        val topicTitle = arguments?.getString(ARG_RECENT_TOPIC_TITLE)

        val topics = HomeFragment().getAuthenticatedTopicData()
        for (topic in topics){
            if (topic.topicTitle == topicTitle) {
                try {
                    val articles = topic.topicArticles as HashMap<*, *>

                    for (article in articles) {
                        articleTitlesITopics.add(article.toString().split('=')[1])
                        articleIds.add(article.toString().split('=')[0])
                    }

                    binding.tvNoArticles.visibility = View.GONE
                } catch (e: NullPointerException) {
                    binding.rvArticleList.visibility = View.GONE
                }

                break
            }
        }
    }

    /**
     * The recyclerview items are clickable and sending
     * the parameter with it in a form of an argumentsto the next fragment.
     */
    private fun articleClicked(title: String){
        val args = Bundle()
        var article: Article

        for(i in 0 until articleTitlesITopics.size){
            if(articleTitlesITopics[i] == title){
                articleReference.child(articleIds[i]).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        article = Article(dataSnapshot)

                        args.putString(ARG_RECENT_ARTICLE_TITLE, article.articleTitle)
                        args.putString(ARG_RECENT_ARTICLE_TEXT, article.articleText)
                        args.putString(ARG_RECENT_ARTICLE_BANNER, article.articleBannerName)
                        args.putString(ARG_RECENT_ARTICLE_ID, article.id)
                        args.putString(ARG_RECENT_ARTICLE_TOPIC, article.articleTopic)

                        findNavController().navigate(R.id.action_topicFragment_to_articleFragment, args)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        
                    }
                })

                break
            }
        }
    }

}
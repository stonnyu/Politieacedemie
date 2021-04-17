package tech.tucano.pmp_politie.UI.Main

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import tech.tucano.pmp_politie.Adapters.ArticleAdapter
import tech.tucano.pmp_politie.Adapters.TopicAdapter
import tech.tucano.pmp_politie.DataModel.*
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentHomeBinding
import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment(), Observer {
    private lateinit var mPrefs: SharedPreferences
    private lateinit var binding: FragmentHomeBinding
    private val auth = FirebaseAuth.getInstance()

    private val topics = arrayListOf<Topic>()
    private var recentArticles = arrayListOf<Article>()

    private val topicAdapter = TopicAdapter(topics) { portal: Topic ->
        topicClicked(
            portal
        )
    }
    private var articleAdapter = ArticleAdapter(recentArticles) { portal: Article ->
        recentArticleClicked(
            portal
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPrefs =
            requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)

        val articleDataList: RecyclerView = binding.rvRecent
        val topicDataList: RecyclerView = binding.rvTopic

        articleDataList.adapter = articleAdapter
        topicDataList.adapter = topicAdapter

        ArticleModel.addObserver(this)
        TopicModel.addObserver(this)
        ConfidentialTopicModel.addObserver(this)


        binding.fabSearch.setOnClickListener {
            findNavController().navigate(R.id.searchFragment)
        }

        arguments?.putString(ARG_RECENT_ARTICLE_ID, "")

        initViews()
    }

    /**
     * Pass topic data to the next fragment.
     */
    private fun topicClicked(topic: Topic) {
        val args = Bundle()
        args.putString(ARG_RECENT_TOPIC_TITLE, topic.topicTitle)
        findNavController().navigate(R.id.action_homeFragment_to_topic_articlesfragment, args)
    }

    /**
     * Pass article data to the next fragment.
     */
    private fun recentArticleClicked(article: Article) {
        val args = Bundle()
        args.putString(ARG_RECENT_ARTICLE_TITLE, article.articleTitle)
        args.putString(ARG_RECENT_ARTICLE_TEXT, article.articleText)
        args.putString(ARG_RECENT_ARTICLE_BANNER, article.articleBannerName)
        args.putString(ARG_RECENT_ARTICLE_ID, article.id)
        args.putString(ARG_RECENT_ARTICLE_TOPIC, article.articleTopic)

        findNavController().navigate(R.id.action_homeFragment_to_articleFragment, args)
    }

    /**
     * If there is an authenticated person the classified topics will load otherwise
     * the public topics will load.
     */
    fun getAuthenticatedTopicData(): ArrayList<Topic> {
        val user = auth.currentUser
        val topics: ArrayList<Topic> = ArrayList()

        return if (user != null) {
            topics.addAll(TopicModel.getData()!!)
            topics.addAll(ConfidentialTopicModel.getData()!!)
            topics
        } else {
            TopicModel.getData()!!
        }
    }

    /**
     * Fill the recyclerviews on the homescreen.
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

        if (width > 1080)
            binding.rvTopic.layoutManager = GridLayoutManager(requireActivity(), 3)
        else
            binding.rvTopic.layoutManager = GridLayoutManager(requireActivity(), 1)

        binding.rvTopic.adapter = topicAdapter

        binding.rvRecent.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        )
        binding.rvRecent.adapter = articleAdapter

        this@HomeFragment.recentArticles.clear()
        this@HomeFragment.topics.clear()

        topics.addAll(getAuthenticatedTopicData())

        topicAdapter.notifyDataSetChanged()

        setRecentArticles()
    }

    /**
     * If there is an authenticated person the classified articles will load otherwise
     * the public articles will load.
     */
    private fun setRecentArticles() {
        recentArticles.clear()

        val articleData = ArticleModel.getData()!!
        val user = auth.currentUser

        if (articleData != null) {
            for (article in articleData) {
                if (article.recent) {
                    if (article.classified && (user != null && mPrefs.getString(getString(R.string.guest),
                            false.toString()) == false.toString()
                    ))
                        recentArticles.add(article)
                    else if (!article.classified)
                        recentArticles.add(article)
                }
            }
        }

        articleAdapter.notifyDataSetChanged()
    }

    /**
     * Update topics and recent articles if there is a change in the database.
     */
    override fun update(p0: Observable?, p1: Any?) {
        this@HomeFragment.recentArticles.clear()
        this@HomeFragment.topics.clear()

        val articleData = ArticleModel.getData()!!

        if (articleData != null) {
            for (article in articleData) {
                if (article.recent) {
                    if (article.classified && (auth.currentUser != null && mPrefs.getString(getString(
                            R.string.guest), false.toString()) == false.toString()
                    ))
                        recentArticles.add(article)
                    else if (!article.classified)
                        recentArticles.add(article)
                }
            }
        }

        val topicData = getAuthenticatedTopicData()
        if (topicData != null) {
            topics.addAll(topicData)
        }

        articleAdapter.notifyDataSetChanged()
        topicAdapter.notifyDataSetChanged()
    }
}
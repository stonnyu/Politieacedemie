package tech.tucano.pmp_politie.UI.Main

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*
import tech.tucano.pmp_politie.Adapters.SearchListAdapter
import tech.tucano.pmp_politie.DataModel.Article
import tech.tucano.pmp_politie.DataModel.SearchModel
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentSearchBinding
import java.util.*
import kotlin.collections.ArrayList


class SearchFragment: Fragment() {
    private lateinit var mPrefs: SharedPreferences

    private lateinit var navController: NavController
    private lateinit var binding: FragmentSearchBinding

    private val searchList = arrayListOf<SearchModel>()
    private val searchListAdapter = SearchListAdapter(searchList){ portal: SearchModel ->
        searchArticleCLicked(
            portal
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
        ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = findNavController()
        mPrefs =
            requireContext().getSharedPreferences(getString(R.string.guest), Context.MODE_PRIVATE)
        //setup recyclerview
        search_list.layoutManager = LinearLayoutManager(this.context)
        search_list.adapter = searchListAdapter

        this.context?.let { showKeyboard(it) }

        //Listens to edit text.
        search_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val searchText: String = search_field.text.toString()
                //Search in firestore
                searchinFirestore(searchText)
            }
        })
    }

    /**
     * Shows keyboard
     */
    private fun showKeyboard(context: Context) {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
            InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        search_field.requestFocusFromTouch()
    }

    /**
     * searches in firestore with query
     */
    private  fun searchinFirestore(searchText: String){

        val suggestions: ArrayList<SearchModel> = ArrayList()
        val tagsReferences = FirebaseDatabase.getInstance().getReference().child("Article")
        tagsReferences.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val article = Article(snapshot)
                    try {
                        if (article.articleTitle!!.toLowerCase(Locale.ROOT)
                                .contains(searchText.toLowerCase())
                        ) {
                            if (article.classified) {
                                if (article.classified && (mPrefs.getString(getString(R.string.guest),
                                        false.toString()) == false.toString()
                                            )
                                ) {
                                    suggestions.add(
                                        SearchModel(
                                            article.articleTitle!!,
                                            article.id
                                        )
                                    )
                                }
                            } else {
                                suggestions.add(
                                    SearchModel(
                                        article.articleTitle!!,
                                        article.id
                                    )
                                )
                            }
                            Log.v("TAG", Article(snapshot).articleTitle!!)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                searchList.clear()
                searchList.addAll(suggestions)
                searchListAdapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }


    private fun searchArticleCLicked(title: SearchModel){
        val args = Bundle()
        var article: Article



        FirebaseDatabase.getInstance().getReference().child("Article").child(title.articleID).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    article = Article(dataSnapshot)
                    args.putString(ARG_RECENT_ARTICLE_TITLE, article.articleTitle)
                    args.putString(ARG_RECENT_ARTICLE_TEXT, article.articleText)
                    args.putString(ARG_RECENT_ARTICLE_BANNER, article.articleBannerName)
                    args.putString(ARG_RECENT_ARTICLE_ID, article.id)
                    args.putString(ARG_RECENT_ARTICLE_TOPIC, article.articleTopic)
                    hideKeyboard()
                    findNavController().navigate(R.id.articleFragment, args)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })


        }

    }


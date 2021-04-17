package tech.tucano.pmp_politie.DataModel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG: String = "ArticleModel"

object ArticleModel : Observable() {
    private var mValueDataListener: ValueEventListener? = null
    private var mArticleList: ArrayList<Article>? = ArrayList()

    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("Article")
    }

    init {
       reset()
    }

    fun reset() {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        Log.i(TAG, "Data init. Line 24")

        mValueDataListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    Log.i(TAG, "Data is updated. Line 29")
                    val data: ArrayList<Article> = ArrayList()
                    if (dataSnapshot != null) {
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            try {
                                data.add(Article(snapshot))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        mArticleList = data
                        Log.i(TAG, "Data updated. There are " + mArticleList!!.size + " entrees in the cache.")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                if (p0 != null) {
                    Log.i(TAG, "Line 51 data update cancelled, error = ${p0.message}")
                }
            }
        }
        getDatabaseRef()?.addValueEventListener(mValueDataListener as ValueEventListener)
    }

    /**
     * Get an article
     * @param id The id of a possible article
     * @return if an article is found return the article else return null
     */
    fun getArticle(id: String): Article {
        for(article in mArticleList!!){
            if(article.id == id)
                return article
        }
        return Article(null)
    }

    fun getData() : ArrayList<Article>? {
        return mArticleList
    }

}
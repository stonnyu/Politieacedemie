package tech.tucano.pmp_politie.DataModel

import android.util.Log
import com.google.firebase.database.DataSnapshot

class Topic (snapshot: DataSnapshot) {
    lateinit var topicTitle: String
    lateinit var topicID: String
    var topicArticles: Any? = null


    init {
        try {
            val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>
            topicID = snapshot.key ?: ""
            topicTitle = data["topicTitle"] as String
            topicArticles = data["topicArticles"]
            Log.i("TAG", topicArticles.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun toString(): String {
        return topicTitle
    }
}
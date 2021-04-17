package tech.tucano.pmp_politie.DataModel

import android.util.Log
import com.google.firebase.database.DataSnapshot
import kotlin.properties.Delegates

class Article (snapshot: DataSnapshot?) {
    lateinit var id: String
    var articleTitle: String? = ""
    lateinit var articleText: String
    var articleTopic: String? = null
    var articleBannerName: String? = null
    var classified: Boolean = false
    var recent: Boolean = false

    init {
        try {
            val data: HashMap<String, Any> = snapshot!!.value as HashMap<String, Any>
            id = snapshot.key ?: ""
            articleTitle = data["articleTitle"] as String
            articleText = data["articleText"] as String
            classified = data["class"] as Boolean
            recent = data["recent"] as Boolean
            articleTopic = data["articleTopic"] as String?
            articleBannerName = data["imagePath"] as String?
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
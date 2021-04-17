package tech.tucano.pmp_politie.DataModel

import android.util.Log
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList

private const val TAG: String = "ConfidentialTopicModel"

object ConfidentialTopicModel : Observable() {
    private var mValueDataListener: ValueEventListener? = null
    private var mTopicList: ArrayList<Topic>? = ArrayList()

    private fun getDatabaseRef() : DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("Confidential Topic")
    }

    init {
        reset()
    }

    fun reset() {
        if (mValueDataListener != null) {
            getDatabaseRef()?.removeEventListener(mValueDataListener!!)
        }
        mValueDataListener = null
        Log.i(TAG, "Data init. Line 23")

        mValueDataListener = object: ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                try {
                    Log.i(TAG, "Data is updated. Line 27")
                    val data: ArrayList<Topic> = ArrayList()
                    if (dataSnapshot != null) {
                        for (snapshot: DataSnapshot in dataSnapshot.children) {
                            try {
                                data.add(Topic(snapshot))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        mTopicList = data
                        Log.i(TAG, "Data updated. " + mTopicList!!.size + " entrees in cache.")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                if (p0 != null) {
                    Log.i(TAG, "Line 49 data update cancelled, error = ${p0.message}")
                }
            }
        }
        getDatabaseRef()?.addValueEventListener(mValueDataListener as ValueEventListener)
    }

    fun getData() : ArrayList<Topic>? {
        return mTopicList
    }

}
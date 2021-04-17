package tech.tucano.pmp_politie.UI.Main

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder.createSource
import android.graphics.ImageDecoder.decodeBitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_add_article.*
import tech.tucano.pmp_politie.DataModel.Article
import tech.tucano.pmp_politie.DataModel.ArticleModel
import tech.tucano.pmp_politie.DataModel.ConfidentialTopicModel
import tech.tucano.pmp_politie.DataModel.TopicModel
import tech.tucano.pmp_politie.R
import tech.tucano.pmp_politie.databinding.FragmentAddArticleBinding
import java.io.IOException
import java.util.*

private const val PICK_IMAGE_REQUEST = 71

class AddArticleFragment : Fragment() {
    private lateinit var binding: FragmentAddArticleBinding

    private var database = FirebaseDatabase.getInstance()
    private var articleReference = database.getReference("Article")
    private var id: String? = null

    private var filePath: Uri? = null
    private var storage = Firebase.storage
    private var imageRef: String? = null

    private lateinit var navController: NavController
    private var topic: String? = null
    private var imageChanged = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddArticleBinding.inflate(layoutInflater, container, false)

        setupDropDown()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        binding.confidentialSwitch.setOnCheckedChangeListener { _, _ ->
            setupDropDown()
        }

        binding.btnSave.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setMessage(getString(R.string.warning_new_article))
            alertDialog.setNegativeButton(getString(R.string.no)) { _, _ -> }
            alertDialog.setPositiveButton(getString(R.string.yes)) { _, _ ->
                addArticle()
                navController.navigate(R.id.action_addArticleFragment_to_homeFragment)
            }
            alertDialog.create().show()
        }

        binding.btnBanner.setOnClickListener {
            chooseImage()
        }

        fabBack2.setOnClickListener {
            findNavController().popBackStack()
        }

        setup()
    }

    private fun setup() {
        val id = arguments?.getString(ARG_RECENT_ARTICLE_ID)

        // Check if we want to edit or add an article
        if (id.isNullOrEmpty()) {
            Log.d("AddArticleFragment", "Article id was null")
            return
        }

        this.id = id

        // Load the article's data
        val article = ArticleModel.getArticle(id)

        binding.etTitle.setText(article.articleTitle)
        binding.etText.setText(article.articleText)

        binding.confidentialSwitch.isChecked = article.classified

        if (!article.articleBannerName.isNullOrEmpty()) {
            loadBanner(article.articleBannerName!!)
            imageRef = article.articleBannerName
        }

        binding.recentSwitch.isChecked = article.recent
        binding.confidentialSwitch.isChecked = article.classified

        // Set the topic
        for(i in 0 until binding.dynamicSpinner.count) {
            if (binding.dynamicSpinner.getItemAtPosition(i).toString()
                    .equals(article.articleTopic!!, ignoreCase = true)) {
                binding.dynamicSpinner.setSelection(i)
                break
            }
        }
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

    private fun setupDropDown() {
        val dynamicSpinner = binding.dynamicSpinner

        val topics = if (binding.confidentialSwitch.isChecked) {
            ConfidentialTopicModel.getData()!!
        } else {
            TopicModel.getData()!!
        }

        // Link an adapter to the dropdown/spinner
        dynamicSpinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            topics
        )

        // Perform an action when an item in the dropdown has been clicked
        dynamicSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: View,
                position: Int, id: Long,
            ) {
                topic = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Topic Dropdown", "No Item Selected In The Dropdown")
            }
        }
    }

    private fun uploadImage() {
        if (filePath == null)
            return

        val progressDialog =    AlertDialog.Builder(requireContext())
        progressDialog.setTitle(getString(R.string.add_article_uploading))
        progressDialog.create().show()

        // Give the image a random and set it's firebase reference
        imageRef = UUID.randomUUID().toString() + ".bmp"
        val ref: StorageReference = storage.reference.child("thumb/$imageRef")

        // Upload the image to firebase
        ref.putFile(filePath!!)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), R.string.add_article_uploaded, Toast.LENGTH_SHORT)
                    .show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(),
                    "${R.string.add_article_failed} ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                progressDialog.setMessage("${R.string.add_article_uploaded} ${progress.toInt()}%")
            }
    }

    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,
            getString(R.string.add_article_select_image)), PICK_IMAGE_REQUEST)

        // Since we can article we want to check if an image has changed so we won't create duplicates
        imageChanged = true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
                val bitmap = if (android.os.Build.VERSION.SDK_INT >= 29)
                    decodeBitmap(createSource(requireContext().contentResolver, filePath!!))
                else
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath!!)

                Log.i("bitmap", filePath.toString())
                binding.ivBanner.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun addArticle() {
        val article = Article(null)

        // Check if we are editing an article or creating a new article
        val newArticle = if (id.isNullOrEmpty())
            articleReference.child("").push()
        else
            articleReference.child(id!!)

        article.articleTitle = binding.etTitle.text.toString()
        article.articleText = binding.etText.text.toString()
        article.recent = binding.recentSwitch.isChecked

        newArticle.child(getString(R.string.db_article_title)).setValue(article.articleTitle)
        newArticle.child(getString(R.string.db_article_text)).setValue(article.articleText)
        newArticle.child(getString(R.string.db_recent)).setValue(article.recent)
        newArticle.child(getString(R.string.db_classified))
            .setValue(binding.confidentialSwitch.isChecked)

        // Check wetter a article is confidential or public
        if (binding.confidentialSwitch.isChecked) {
            newArticle.child(getString(R.string.db_article_topic))
                .setValue("${getString(R.string.db_confidential_identifier)}$topic")
        } else {
            newArticle.child(getString(R.string.db_article_topic)).setValue(topic!!)
        }

        if (imageRef.isNullOrEmpty() || imageChanged)
            uploadImage()
        newArticle.child(getString(R.string.db_image_path)).setValue(imageRef)

        val newTopicArticle = if (binding.confidentialSwitch.isChecked) {
            database.getReference(getString(R.string.db_confidential_topic)).child(topic!!)
                .child(getString(R.string.db_topic_articles))
        } else {
            database.getReference(getString(R.string.db_topic)).child(topic!!)
                .child(getString(R.string.db_topic_articles))
        }

        newTopicArticle.child(newArticle.key.toString()).setValue(article.articleTitle)
    }
}
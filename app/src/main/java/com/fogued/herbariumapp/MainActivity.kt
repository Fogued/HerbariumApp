package com.fogued.herbariumapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance().reference
    private var progressBar: ProgressBar? = null
    private var imagePreview: ImageView? = null
    private var filePath: Uri? = null
    private var nameEditText: EditText? = null
    private var locationEditText: EditText? = null
    private var descriptionEditText: EditText? = null

    private val PICK_IMAGE_GALLERY_CODE = 78
    private val CAMERA_PERMISSION_REQUEST_CODE = 12345
    private val CAMERA_PICTURE_REQUEST_CODE = 56789
    private val STORAGE_PERMISSION_REQUEST_CODE = 101

    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: FloatingActionButton
    private lateinit var adapter: ImageAdapter
    private var isGridView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)

        fetchImageUrlsFromFirebase { imageUrlList ->
            val items = imageUrlList.map { imageUrl ->
                ImageItem(imageUrl, "Description 1", "Description 2", "Description 3")
            }
            adapter = ImageAdapter(items, isGridView)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        }

        val selectButton = findViewById<Button>(R.id.selectButton)
        val uploadButton = findViewById<Button>(R.id.uploadButton)
        val galleryButton = findViewById<Button>(R.id.galleryButton)

        imagePreview = findViewById<ImageView>(R.id.imagePreview)
        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        nameEditText = findViewById<EditText>(R.id.nameEditText)
        locationEditText = findViewById<EditText>(R.id.locationEditText)
        descriptionEditText = findViewById<EditText>(R.id.descriptionEditText)

        selectButton.setOnClickListener { showImageSelectedDialog() }

        uploadButton.setOnClickListener {
            val name = nameEditText?.text.toString()
            val location = locationEditText?.text.toString()
            val description = descriptionEditText?.text.toString()
            if (filePath != null) {
                progressBar!!.visibility = View.VISIBLE
                uploadPlantImage(filePath!!, name, location, description, {
                    Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility = View.GONE
                    nameEditText?.text?.clear()
                    locationEditText?.text?.clear()
                    descriptionEditText?.text?.clear()
                }, { exception ->
                    Toast.makeText(this, "Image upload failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                    progressBar!!.visibility = View.GONE
                })
            } else {
                Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
            }
        }

        galleryButton.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    DownloadUploadedImages::class.java
                )
            )
        }
    }

    private fun fetchImageUrlsFromFirebase(onComplete: (List<String>) -> Unit) {
        val imageUrlList = mutableListOf<String>()
        database.child("plants").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val imageUrl = data.child("imageUrl").getValue(String::class.java)
                    if (imageUrl != null) {
                        imageUrlList.add(imageUrl)
                    }
                }
                onComplete(imageUrlList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Failed to load images: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadPlantImage(
        uri: Uri,
        name: String,
        location: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageReference = FirebaseStorage.getInstance().reference
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "plant_images/$timestamp.jpg"
        val fileReference = storageReference.child(fileName)

        fileReference.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { downloadUri ->
                    val plantInfo = hashMapOf(
                        "imageUrl" to downloadUri.toString(),
                        "name" to name,
                        "location" to location,
                        "description" to description,
                        "timestamp" to timestamp
                    )

                    database.child("plants").push().setValue(plantInfo)
                        .addOnSuccessListener {
                            onSuccess()
                            imagePreview?.visibility = View.GONE
                        }
                        .addOnFailureListener { e -> onFailure(e) }
                }.addOnFailureListener { e -> onFailure(e) }
            }.addOnFailureListener { e -> onFailure(e) }
    }

    private fun showImageSelectedDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image")
        builder.setMessage("Please select an option")
        builder.setPositiveButton("Camera") { dialog, _ ->
            checkCameraPermission()
            dialog.dismiss()        }
        builder.setNeutralButton("Cancel") { dialog, _ -> dialog.dismiss() }
        builder.setNegativeButton("Gallery") { dialog, _ ->
            selectFromGallery()
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        } else {
            selectFromGallery()
        }
    }

    private fun checkCameraPermission() {
        if ((ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(
                    Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), CAMERA_PERMISSION_REQUEST_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission is required to take photos", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectFromGallery()
            } else {
                Toast.makeText(this, "Storage permission is required to select images", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, CAMERA_PICTURE_REQUEST_CODE)
        }
    }

    private fun selectFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Image"),
            PICK_IMAGE_GALLERY_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_GALLERY_CODE && resultCode == RESULT_OK) {
            if (data == null || data.data == null) return
            try {
                filePath = data.data
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagePreview?.setImageBitmap(bitmap)
                imagePreview?.visibility = View.VISIBLE
            } catch (e: Exception) {
            }
        } else if (requestCode == CAMERA_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
            val extras = data!!.extras
            val bitmap = extras!!["data"] as Bitmap?
            imagePreview?.setImageBitmap(bitmap)
            filePath = getImageUri(applicationContext, bitmap!!)
            imagePreview?.visibility = View.VISIBLE
        }
    }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "title", null)
        return Uri.parse(path)
    }
}
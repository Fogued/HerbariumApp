package com.fogued.herbariumapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DownloadUploadedImages : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var toggleButton: FloatingActionButton
    private lateinit var adapter: ImageAdapter
    private var isGridView = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        recyclerView = findViewById(R.id.recyclerView)
        toggleButton = findViewById(R.id.toggleButton)

        fetchImageUrlsFromFirebase { imageItems ->
            adapter = ImageAdapter(imageItems, isGridView)
            recyclerView.adapter = adapter
            recyclerView.layoutManager = GridLayoutManager(this, 2)
        }

        toggleButton.setOnClickListener {
            isGridView = !isGridView
            recyclerView.layoutManager = if (isGridView) {
                GridLayoutManager(this, 2)
            } else {
                LinearLayoutManager(this)
            }
            adapter.setViewType(isGridView)
        }
    }

    private fun fetchImageUrlsFromFirebase(onComplete: (List<ImageItem>) -> Unit) {
        val imageItems = mutableListOf<ImageItem>()
        val database = FirebaseDatabase.getInstance().reference
        database.child("plants").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val imageUrl = data.child("imageUrl").getValue(String::class.java)
                    val name = data.child("name").getValue(String::class.java) ?: ""
                    val location = data.child("location").getValue(String::class.java) ?: ""
                    val description = data.child("description").getValue(String::class.java) ?: ""
                    if (imageUrl != null) {
                        imageItems.add(ImageItem(imageUrl, name, location, description))
                    }
                }
                onComplete(imageItems)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DownloadUploadedImages, "Failed to load images: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
package com.android.projectpbp.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.android.projectpbp.DatabaseHelper
import com.android.projectpbp.Model.Menu
import com.android.projectpbp.R
import com.android.projectpbp.databinding.ActivityCreateMenuBinding
import java.io.IOException
import java.io.InputStream

class CreateMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateMenuBinding
    private lateinit var db : DatabaseHelper
    private lateinit var list : Spinner
    private var selectedImage: Bitmap? = null

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        list = findViewById(R.id.kategoriMenu)
        ArrayAdapter.createFromResource(
            this,
            R.array.kategori_menu,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            list.adapter = adapter
        }

        db = DatabaseHelper(this)


        binding.imageView.setOnClickListener {
            openGallery()
        }



        binding.buttonAdd.setOnClickListener{
            val nama = binding.namaMenu.text.toString()
            val kategori = list.selectedItem.toString()
            val harga = binding.hargaMenu.text.toString().toInt()
            val bitmap: Bitmap? = selectedImage?.let { resizeImage(it, 250, 250) }

            val menu = Menu(0, nama,kategori, harga, bitmap)
            db.insertMenu(menu)
            finish()
            Toast.makeText(this, "Menu Added", Toast.LENGTH_SHORT).show()
        }
        val callback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                val intent = Intent(this@CreateMenuActivity, ListMenuActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun resizeImage(originalBitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri: Uri? = data.data
            try {
                if (uri != null) {
                    val inputStream: InputStream? = contentResolver.openInputStream(uri)
                    // Konversi InputStream menjadi Bitmap
                    selectedImage = BitmapFactory.decodeStream(inputStream)
                    // Tampilkan gambar di ImageView
                    binding.imageView.setImageBitmap(selectedImage)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }
}
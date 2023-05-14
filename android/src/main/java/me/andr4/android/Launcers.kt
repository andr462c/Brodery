package me.andr4.android

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import coil.ImageLoader
import coil.request.ImageRequest
import java.util.*

class Launcher(
    private val main: MainActivity,
){
    private var CameraLauncherFunction : (Bitmap) -> Unit = {}
    private val CameraLauncher = main.registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) {
        it?.let { CameraLauncherFunction(it) }
    }

    private var GalleryLauncherFunction : (Bitmap) -> Unit = {}
    private val GalleryLauncher = main.registerForActivityResult(
        ActivityResultContracts.GetContent()) { uri: Uri? ->
        val loader = ImageLoader(main.baseContext)
        val req = ImageRequest.Builder(main.baseContext)
            .data(uri) // demo link
            .target { result ->
                val bitmap = (result as BitmapDrawable).bitmap

                bitmap?.let { GalleryLauncherFunction(it) }

            }
            .build()

        loader.enqueue(req)
    }

    fun takePhoto(handleBitmap: (Bitmap) -> Unit) {
        CameraLauncherFunction = handleBitmap
        CameraLauncher.launch()
    }

    fun loadImageFromGallery(handleBitmap : (Bitmap) -> Unit) {
        GalleryLauncherFunction = handleBitmap
        GalleryLauncher.launch("image/*")
    }
}
package me.andr4.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

class AppState_dep(
    val main: MainActivity,
    var bitmapImageList: MutableList<InternalBitmapImage>,
    val context: Context,
    val launcher: Launcher
){
    init {
        initImageList()
        loadBitmapsFromInternalStorageIntoView()
    }

    private fun isImagefile(file: File) : Boolean {
        return file.isFile && file.canRead() && file.name.endsWith(".png")
    }

    private fun initImageList() {
        val files = context.filesDir.listFiles()
        files?.forEach {
            if (isImagefile(it)) {
                val image = InternalBitmapImage(it.name, mutableStateOf(null))
                addInternalBitmapImage(image)
            }
        } ?: {}
    }

    var bit : Bitmap? = null

    fun AddImageFromCamera() {
        launcher.takePhoto { handleNewBitmap(it) }
    }

    fun addImageFromGallery() {
        launcher.loadImageFromGallery { handleNewBitmap(it) }
    }

    fun handleNewBitmap(bitmap: Bitmap) {
        val name = UUID.randomUUID().toString()
        saveBitmapToInternalStorage(name, bitmap)

        val bitmapImage = InternalBitmapImage(name, mutableStateOf(bitmap))
        addInternalBitmapImage(bitmapImage)
    }

    fun addInternalBitmapImage(bitmapImage: InternalBitmapImage) {
        bitmapImageList.add(bitmapImage)
    }

    private fun getImagefiles() : List<File>?{
        return context.filesDir.listFiles()?.filter { isImagefile(it) }
    }


    fun loadBitmapsFromInternalStorageIntoView(){
        //TODO update to be more efficient. only loading nessesary
        println("size ${bitmapImageList.size}")
        getImagefiles()?.forEach {
            loadBitmapFromInternalStorageIntoView(it)
        }
    }

    fun loadBitmapFromInternalStorageIntoView(file: File){
        MainScope().launch {
            loadBitmapFromInternalStorage(file)?.let {
                val image = bitmapImageList.find { bitmap ->
                    bitmap.name == it.name
                }
                if (image != null){
                    image.bitmap.value = it.bitmap.value
                } else {
                    throw Exception("Unexpected case in load into view")
                }
            }
        }
    }

    private suspend fun loadBitmapFromInternalStorage(file: File) : InternalBitmapImage? {
        val image = withContext(Dispatchers.IO) {
            var bitmapImage: InternalBitmapImage? = null
            if (isImagefile(file)) {
                val bytes = file.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bit = bitmap
                bitmapImage = InternalBitmapImage(file.name, mutableStateOf(bitmap))
            }
            bitmapImage
        }
        return image
    }

    private suspend fun loadBitmapsFromInternalStorage() : List<InternalBitmapImage> {
        return withContext(Dispatchers.IO) {
            val files = getImagefiles()
            files?.map {
                val bytes = it.readBytes()
                val bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.size)
                InternalBitmapImage(it.name, mutableStateOf(bitmap))
            } ?: listOf()
        }
    }

    fun deleteFileFromInternalStorage(filename: String) : Boolean {
        return try {
            context.deleteFile(filename)
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun saveBitmapToInternalStorage(filename: String, bitmap: Bitmap) {
        MainScope().launch {
            val isSaved = savePNG(filename,bitmap)
            if (!isSaved) {
                Toast.makeText(context,"Failed to save photo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context,"Photo Saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun savePNG(filename: String, bitmap: Bitmap) :Boolean {
        return withContext(Dispatchers.IO){
            try {
                context.openFileOutput("$filename.png", Context.MODE_PRIVATE).use {
                    val compressed = bitmap.compress(Bitmap.CompressFormat.PNG,100, it)
                    if (!compressed) throw IOException("Could not save bitmap")
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }
}


@Composable
fun rememberAppState(
    main: MainActivity,
    bitmapImageList: MutableList<InternalBitmapImage> = remember { mutableStateListOf() },
    context:Context = LocalContext.current,
    launcher: Launcher
) = remember {
    AppState_dep(main, bitmapImageList, context, launcher)
}
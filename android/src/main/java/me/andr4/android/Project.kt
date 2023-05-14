package me.andr4.android

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.*

enum class ProjectType {
    CROSS_STITCH, OTHER
}

class Project(
    //TODO: have original bitmap and modified
    private var internalBitmap: InternalBitmapImage,
    private var type: ProjectType = ProjectType.CROSS_STITCH
) {
    fun getName(): String {
        return internalBitmap.name
    }

    fun updateBitmap(bitmap: Bitmap?) {
        internalBitmap.bitmap.value = bitmap
    }

    fun getBitmap(): Bitmap? {
        return internalBitmap.bitmap.value
    }
}

data class InternalBitmapImage(
    val name: String,
    //TODO Make these remember
    val bitmap: MutableState<Bitmap?>
)

class Projects(
    val main: MainActivity,
    var projects: SnapshotStateList<Project> = mutableStateListOf(),
) {
    val launcher = main.launcher

    @Composable
    fun loadBitmaps() {
        val files = getImagefiles()
        files?.let {
            initBitmapsToNull(it)
            loadBitmapsFromInternalStorageIntoView(it)
        }
    }

    fun startProject(): Project {
        val name = UUID.randomUUID().toString()
        val bitmapImage = InternalBitmapImage(name, mutableStateOf(null))
        return Project(bitmapImage)
    }

    fun addImageFromCameraAndNavigate(project: Project, appState: AppState) {
        launcher.takePhoto {
            appState.navController.popBackStack()
            appState.navigateToEditProject(project)
            project.updateBitmap(it)
            saveToInternalStorage(project)
        }
    }

    fun addImageFromGalleryAndNavigate(project: Project, appState: AppState) {
        launcher.loadImageFromGallery {
            appState.navController.popBackStack()
            appState.navigateToEditProject(project)
            project.updateBitmap(it)
            saveToInternalStorage(project)
        }
    }

    fun saveToInternalStorage(project: Project): Boolean {
        val bitmap = project.getBitmap() ?: return false

        saveBitmapToInternalStorage(project.getName(),bitmap)
        return true
    }

    fun startNewProjectFromCamera(appState: AppState){
        val project = startProject()
        addImageFromCameraAndNavigate(project, appState)

        add(project)
        //TODO still need to save
    }

    fun startNewProjectFromGallery(appState: AppState){
        val project = startProject()
        addImageFromGalleryAndNavigate(project, appState)

        add(project)
        //TODO still need to save
    }

    fun add(project: Project) {
        projects.add(0,project)
    }

    fun addImageFromCamera() {
        launcher.takePhoto { handleNewBitmap(it) }
    }

    fun addImageFromGallery() {
        launcher.loadImageFromGallery { handleNewBitmap(it) }
    }

    private fun handleNewBitmap(bitmap: Bitmap) {
        val name = UUID.randomUUID().toString()
        saveBitmapToInternalStorage(name, bitmap)

        val bitmapImage = InternalBitmapImage(name, mutableStateOf(bitmap))
        addInternalBitmapImage(bitmapImage)
    }

    private fun saveBitmapToInternalStorage(filename: String, bitmap: Bitmap) {
        MainScope().launch {
            val isSaved = savePNG(filename,bitmap)
            if (!isSaved) {
                Toast.makeText(main.baseContext,"Failed to save photo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(main.baseContext,"Photo Saved successfully", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun savePNG(filename: String, bitmap: Bitmap) :Boolean {
        return withContext(Dispatchers.IO){
            try {
                main.baseContext.openFileOutput("$filename.png", Context.MODE_PRIVATE).use {
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

    private fun loadBitmapsFromInternalStorageIntoView(files: List<File>){
        files.forEach {
            loadBitmapFromInternalStorageIntoView(it)
        }
    }

    fun loadBitmapFromInternalStorageIntoView(file: File){
        MainScope().launch {
            loadBitmapFromInternalStorage(file)?.let {
                val project = projects.find { project ->
                    project.getName() == it.name
                }
                if (project != null){
                    project.updateBitmap(it.bitmap.value)
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
                bitmapImage = InternalBitmapImage(file.name, mutableStateOf(bitmap))
            }
            bitmapImage
        }
        return image
    }

    private fun initBitmapsToNull(files: List<File>) {
        files.forEach {
            val image = InternalBitmapImage(it.name, mutableStateOf(null))
            addInternalBitmapImage(image)
        }
    }

    private fun getImagefiles() : List<File>?{
        return main.baseContext.filesDir.listFiles()?.filter { isImagefile(it) }
    }

    //TODO: add projects instead of images
    fun addInternalBitmapImage(bitmapImage: InternalBitmapImage) {
        val project = Project(bitmapImage)
        projects.add(0,project)
    }

    private fun isImagefile(file: File) : Boolean {
        return file.isFile && file.canRead() && file.name.endsWith(".png")
    }
}
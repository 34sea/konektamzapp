package com.example.konekta_mz_app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

object ImagePicker {

    fun createImageFile(context: Context): File {
        val storageDir = context.cacheDir
        return File.createTempFile("IMG_${UUID.randomUUID()}_", ".jpg", storageDir)
    }

    fun getUriForFile(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun copyUriToFile(context: Context, uri: Uri, destFile: File): Boolean {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun compressImage(file: File, maxWidth: Int = 1024, quality: Int = 80): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (bitmap.width > maxWidth) {
            newWidth = maxWidth
            newHeight = (maxWidth / ratio).toInt()
        } else {
            newWidth = bitmap.width
            newHeight = bitmap.height
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        FileOutputStream(file).use { out ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        bitmap.recycle()
        scaledBitmap.recycle()
        return file
    }
}

@Composable
fun rememberImagePickerLauncher(
    onImageSelected: (Uri?) -> Unit
): androidx.activity.result.ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        onImageSelected(uri)
    }
}

@Composable
fun rememberCameraLauncher(
    onImageCaptured: (Boolean) -> Unit
): androidx.activity.result.ActivityResultLauncher<Uri> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        onImageCaptured(success)
    }
}

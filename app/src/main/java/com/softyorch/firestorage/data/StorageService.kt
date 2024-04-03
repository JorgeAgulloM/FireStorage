package com.softyorch.firestorage.data

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageService @Inject constructor(
    private val storage: FirebaseStorage,
    @ApplicationContext private val context: Context
) {

    private val fakeUserId = "a98ha789dgyva087ga09a8fya98f"

    fun basicExample() {
        val reference = storage.reference.child("/example/other_user.png")
        Log.i("LOGTAG", "name ${reference.name}") //other_user.png
        Log.i("LOGTAG", "parent ${reference.parent}") //example/other_user.png
        Log.i(
            "LOGTAG",
            "bucket ${reference.bucket}"
        ) //https://console.firebase.google.com/u/0/project/firestorage-87f72/storage/firestorage-87f72.appspot.com/files/~2Fexample?hl=es
    }

    fun uploadBasicImage(uri: Uri) {
        val reference = storage.reference.child(fakeUserId).child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

    suspend fun uploadAndDownloadImage(uri: Uri): Uri =
        suspendCancellableCoroutine { cancellableContinuation ->
            val reference = storage.reference.child("/download/${uri.lastPathSegment}")
            reference.putFile(uri, setMetaData())
                .addOnSuccessListener {
                    downloadImage(it, cancellableContinuation)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }

    private fun removeImage(pathImage: String): Boolean {
        val reference = storage.reference.child(pathImage)
        return reference.delete().isSuccessful
    }

    private fun uploadImageWithProgress(uri: Uri): Double {
        val reference = storage.reference.child(fakeUserId).child("/${uri.lastPathSegment}")
        var progress: Double = 0.0
        reference.putFile(uri).addOnProgressListener { uploadTask ->
            progress = (100.0 * uploadTask.bytesTransferred) / uploadTask.totalByteCount
        }
        return progress
    }

    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        cancellableContinuation: CancellableContinuation<Uri>
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener { uri -> cancellableContinuation.resume(uri) }
            .addOnFailureListener { cancellableContinuation.resumeWithException(it) }
    }

    private suspend fun getMetaData(reference: StorageReference): StorageMetadata {
        val response = reference.metadata.await()

        response.customMetadataKeys.forEach { key ->
            response.getCustomMetadata(key)?.let {  value ->
                Log.i("LOGTAG", "Para la key $key el valor es $value")
            }
        }

        return response
    }

    private fun setMetaData(): StorageMetadata = storageMetadata {
        val name = context.packageName
        val version = context.packageManager.getPackageInfo(name, 0).versionName

        contentType = "image/jpeg"
        setCustomMetadata("date", System.currentTimeMillis().toString())
        setCustomMetadata("app", name)
        setCustomMetadata("version", version)
        setCustomMetadata("brand", Build.BRAND)
        setCustomMetadata("device", Build.MODEL)
        setCustomMetadata("sdk", Build.VERSION.SDK_INT.toString())
    }
}

package com.softyorch.firestorage.data

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageService @Inject constructor(private val storage: FirebaseStorage) {

    private val fakeUserId = "a98ha789dgyva087ga09a8fya98f"

    fun basicExample() {
        val reference = storage.reference.child("/example/other_user.png")
        Log.i("LOGTAG", "name ${reference.name}") //other_user.png
        Log.i("LOGTAG", "parent ${reference.parent}") //example/other_user.png
        Log.i("LOGTAG", "bucket ${reference.bucket}") //https://console.firebase.google.com/u/0/project/firestorage-87f72/storage/firestorage-87f72.appspot.com/files/~2Fexample?hl=es
    }

    fun uploadBasicImage(uri: Uri) {
        val reference = storage.reference.child(fakeUserId).child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }

    suspend fun uploadAndDownloadImage(uri: Uri): Uri =
        suspendCancellableCoroutine { cancellableContinuation ->
            val reference = storage.reference.child("/download/${uri.lastPathSegment}")
            reference.putFile(uri)
                .addOnSuccessListener {
                    downloadImage(it, cancellableContinuation)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }


    private fun downloadImage(
        uploadTask: UploadTask.TaskSnapshot,
        cancellableContinuation: CancellableContinuation<Uri>
    ) {
        uploadTask.storage.downloadUrl
            .addOnSuccessListener { uri -> cancellableContinuation.resume(uri) }
            .addOnFailureListener { cancellableContinuation.resumeWithException(it) }
    }
}

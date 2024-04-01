package com.softyorch.firestorage.data

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class StorageService @Inject constructor(private val storage: FirebaseStorage) {

    fun basicExample() {
        val reference = storage.reference.child("/example/other_user.png")
        Log.i("LOGTAG", "name ${reference.name}") //other_user.png
        Log.i("LOGTAG", "parent ${reference.parent}") //example/other_user.png
        Log.i("LOGTAG", "bucket ${reference.bucket}") //https://console.firebase.google.com/u/0/project/firestorage-87f72/storage/firestorage-87f72.appspot.com/files/~2Fexample?hl=es
    }

    fun uploadBasicImage(uri: Uri) {
        val reference = storage.reference.child(uri.lastPathSegment.orEmpty())
        reference.putFile(uri)
    }
}
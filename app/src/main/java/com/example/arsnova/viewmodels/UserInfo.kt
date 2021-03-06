package com.example.arsnova.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot

class UserInfo : ViewModel(){
    private val mutableImageUri = MutableLiveData<Uri>()
    private val mutableUserDocument = MutableLiveData<DocumentSnapshot>()
    private val mutableEventDocument = MutableLiveData<DocumentSnapshot>()
    val imageUri : LiveData<Uri> get() = mutableImageUri
    val userDocument : LiveData<DocumentSnapshot> get() = mutableUserDocument
    val eventDocument : LiveData<DocumentSnapshot> get() = mutableEventDocument

    fun setUri(uri: Uri) {
        mutableImageUri.value = uri
    }

    fun setDocument(document: DocumentSnapshot) {
        mutableUserDocument.value = document
    }

    fun setEvent(document: DocumentSnapshot) {
        mutableEventDocument.value = document
    }

    fun getDocument() : DocumentSnapshot? {
        return userDocument.value
    }

    fun getImageUri() : Uri? {
        return mutableImageUri.value
    }

    fun getEvent() : DocumentSnapshot? {
        return mutableEventDocument.value
    }
}
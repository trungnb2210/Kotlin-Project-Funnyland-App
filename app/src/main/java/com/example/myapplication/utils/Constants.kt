package com.example.myapplication.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {
    //USE WHENEVER INPUT IMAGES : ALSO FOR WHEN INPUTTING IMAGES FOR
    const val USERS: String = "users"
    const val FUNNYLAND_PREFERENCES: String = "FunnylandPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val READ_STORAGE_PERMISSION_CODE= 1
    const val PICK_IMAGE_REQUEST_CODE = 1

    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"
    const val MOBILE: String = "phoneNumber"
    const val FIRSTNAME: String = "firstName"
    const val LASTNAME: String = "lastName"
    const val IMAGE: String = "image"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity:Activity, uri: Uri?):String? {

        /* MimeTypeMap: Two-way map that maps MIME-types to file extensions and so forth
        * getSingleton():Get the singleton instance of MimeTypeMap
        * getExtensionFromMimeType: Return the registered extension for the given MIME type
        * contentResolver.getType: Return the MIME type of the given content URL
        */
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}
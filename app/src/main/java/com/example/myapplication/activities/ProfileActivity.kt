package com.example.myapplication.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.example.myapplication.firestore.FirestoreClass

import com.example.myapplication.models.User
import com.example.myapplication.utils.Constants
import com.example.myapplication.utils.GlideLoader
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.et_email
import kotlinx.android.synthetic.main.activity_profile.et_first_name
import kotlinx.android.synthetic.main.activity_profile.et_last_name
import kotlinx.android.synthetic.main.activity_register.*
import java.io.IOException
import java.util.jar.Manifest

class ProfileActivity : BaseActivity(), View.OnClickListener {

    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? = null

    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        et_first_name.isEnabled = true
        //if isEnabled = true then user will not be able to edit the text field in the prf_activity
        et_first_name.setText(mUserDetails.firstName)

        et_last_name.isEnabled = true
        et_last_name.setText(mUserDetails.lastName)

        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)

        iv_user_photo.setOnClickListener(this@ProfileActivity)
        btn_save.setOnClickListener(this@ProfileActivity)
    }

    override fun onClick(v:View?){
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this, android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED) {
                        Constants.showImageChooser(this)
                        //showErrorSnackBar("You already have the storage permission", false)
                    } else {
                        ActivityCompat.requestPermissions(this,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE)
                    }
                }

                R.id.btn_save ->{

                    showProgressDialog(resources.getString(R.string.please_wait))

                    FirestoreClass().uploadImage(this,mSelectedImageFileUri)

                    //TODO Save btn will still update picture without having the phone number filled out
                    if (validateUserProfileDetails()){

                        val userHashMap = HashMap<String,Any>()

                        //already validated the phone number in the xml file where input text type is set to 'phone'
                        val phoneNumber = et_mobile_number.text.toString().trim{ it <= ' '}
                        showErrorSnackBar("Valid details. Updating...",false)

                        if (phoneNumber.isNotEmpty()){
                            userHashMap[Constants.MOBILE] = phoneNumber.toLong()
                        }
                        //TODO don't trim phone number for 0 if turns to LONG phone number might have to be a string

                        val firstName = et_first_name.text.toString().trim{ it <= ' '}
                        showErrorSnackBar("Valid details. Updating...",false)

                        if (firstName.isNotEmpty()){
                            userHashMap[Constants.FIRSTNAME] = firstName
                        }

                        val lastName = et_last_name.text.toString().trim{ it <= ' '}
                        showErrorSnackBar("Valid details. Updating...",false)

                        if (lastName.isNotEmpty()){
                            userHashMap[Constants.LASTNAME] = lastName
                        }

                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().updateUserProfileData(this,userHashMap)
                    }
                }
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()
        Toast.makeText(
            this@ProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@ProfileActivity,MainActivity::class.java))
        finish()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            //If permission is granted
            if (grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Constants.showImageChooser(this)

            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        //try because using file location path to input image, in case incorrect path/errors
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this).loadUserPicture(mSelectedImageFileUri!!,iv_user_photo)
                    } catch (e:IOException){
                        e.printStackTrace()
                        Toast.makeText(
                            this@ProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails():Boolean{
        return when {
            TextUtils.isEmpty(et_first_name.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_mobile_number.text.toString().trim{it<= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_mobile_number),true)
                false
            } else -> {
                true
            }
        }
    }

    fun imageUploadSuccess(imageURL:String){
        hideProgressDialog()
        Toast.makeText(
            this@ProfileActivity,
            "Your image is uploaded successfully. Its respective URL is $imageURL",
            Toast.LENGTH_SHORT
        ).show()
    }
}
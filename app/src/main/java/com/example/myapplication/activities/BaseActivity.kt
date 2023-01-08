package com.example.myapplication.activities

import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.myapplication.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.dialogue_progress.*

open class BaseActivity : AppCompatActivity() {
    fun showErrorSnackBar (message: String, errorMessage: Boolean){
        val jnSnackBar = Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG)
        val snackBarView = jnSnackBar.view

        if (errorMessage) {
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity, R.color.colorSnackBarError
                )
            )
        }else{
            snackBarView.setBackgroundColor(
                ContextCompat.getColor(
                    this@BaseActivity, R.color.colorSnackBarSuccess
                )
            )
        }
        jnSnackBar.show()
    }

    private lateinit var mProgressDialog: Dialog

    fun showProgressDialog(text: String){
        mProgressDialog=Dialog(this)
        mProgressDialog.setContentView(R.layout.dialogue_progress)
        mProgressDialog.tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}
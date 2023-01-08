package com.example.myapplication.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.TextView
import com.example.myapplication.R
import com.example.myapplication.firestore.FirestoreClass
import com.example.myapplication.models.User
import com.example.myapplication.utils.Constants
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_email
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : BaseActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        tv_forgot_password.setOnClickListener(this)
        btn_login.setOnClickListener(this)
        tv_register.setOnClickListener(this)
    }

    fun userLoggedInSuccess(user: User){
        hideProgressDialog()

        Log.i("First Name: ", user.firstName)
        Log.i("Last Name: ", user.lastName)
        Log.i("Email: ", user.email)

        val user2 = Firebase.auth.currentUser
        if (user2!!.isEmailVerified) {
            if (user.profileCompleted == 0) {
                val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                intent.putExtra(Constants.EXTRA_USER_DETAILS, user)
                startActivity(intent)
            } else {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            }
        } else {
            showErrorSnackBar("Please verify your account first by following the link sent by the verification email", true)
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onClick(v: View?){
        if (v != null) {
            when (v.id) {
                R.id.tv_forgot_password -> {
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.btn_login -> {
                    loginRegisteredUser()
                }
                R.id.tv_register -> {
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when{
            TextUtils.isEmpty(et_email.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }
    private fun loginRegisteredUser(){
        if (validateLoginDetails()) {

            showProgressDialog(resources.getString(R.string.please_wait))

            val email: String = et_email.text.toString().trim{ it <= ' '}
            val password: String = et_password.text.toString().trim{ it <= ' '}

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{ task ->

                        if (task.isSuccessful){
                            FirestoreClass().getUserDetails(this@LoginActivity)

                        } else {
                            hideProgressDialog()
                            showErrorSnackBar(task.exception!!.message.toString(),true)
                        }
                    }
        }
    }
}
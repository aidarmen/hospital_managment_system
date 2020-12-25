package com.example.hospital_managment_system.resetPassword

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.register.RegisterActivity
import com.example.hospital_managment_system.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth

class ResetPasswordActivity : AppCompatActivity() {

    private var email : EditText? = null
    private var btnresetPass: Button?=null
    private var btnback:Button?=null
    private var progressbar: ProgressBar? = null
    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        email = findViewById<EditText>(R.id.email3)
        btnresetPass = findViewById<Button>(R.id.btn_reset_password3)
        btnback = findViewById<Button>(R.id.sign_in_button3)
        progressbar = findViewById<ProgressBar>(R.id.progressBar3)

        auth = FirebaseAuth.getInstance()


//
        btnback!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@ResetPasswordActivity, LoginActivity::class.java))
            finish()
        })
        btnresetPass!!.setOnClickListener(View.OnClickListener {
            val email = email!!.text.toString().trim()
            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"Enter your email ",Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            progressbar!!.setVisibility(View.VISIBLE)

            auth!!.sendPasswordResetEmail(email)
                .addOnCompleteListener(OnCompleteListener {
                        task ->
                    if (task.isSuccessful){
                        Toast.makeText(this@ResetPasswordActivity,"We have to sent you instraction in your email",Toast.LENGTH_LONG).show()
                    }else{
                        Toast.makeText(this@ResetPasswordActivity,"Failed t sent to reset Email",Toast.LENGTH_SHORT ).show()
                    }
                    progressbar!!.setVisibility(View.GONE)
                })

        })
    }
}

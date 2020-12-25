package com.example.hospital_managment_system.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.hospital_managment_system.MainActivity
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.doctorSide.MainActivityDoctor
import com.example.hospital_managment_system.register.RegisterDoctorActivity
import com.example.hospital_managment_system.register.RegisterPatientActivity
import com.example.hospital_managment_system.resetPassword.ResetPasswordActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class LoginActivity : AppCompatActivity() {

    private var inputEmail: EditText? = null
    private var inputPassword:EditText? = null
    private var btnSignup :Button? =null
    private var btnSignupDoctor :Button? =null
    private var btnLogin :Button?=null
    private var btnReset:Button? =null
    private var progressBar:ProgressBar?=null
    private var auth:FirebaseAuth?=null


    // this listener will be called when there is change in firebase user session
    internal var authListener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {


            //[]
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val rootRef = FirebaseDatabase.getInstance().reference


            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child("Users").child("Patients").hasChild(uid)) {
                        startActivity(Intent(this@LoginActivity, MainActivityDoctor::class.java))
                        finish()
                    } else if (p0.child("Users").child("Doctors").hasChild(uid)) {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
            //[]
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_login)



        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                //[]
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val rootRef = FirebaseDatabase.getInstance().reference


                rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.child("Users").child("Patients").hasChild(uid)) {
                            startActivity(Intent(this@LoginActivity, MainActivityDoctor::class.java))
                            finish()
                        } else if (p0.child("Users").child("Doctors").hasChild(uid)) {
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                //[]


            }
        }

        setContentView(R.layout.activity_login)
        inputEmail = findViewById<EditText>(R.id.email)
        inputPassword = findViewById<EditText>(R.id.password)
        btnSignup  = findViewById<Button>(R.id.sign_up_button1)
        btnLogin = findViewById<Button>(R.id.login)
        progressBar = findViewById<ProgressBar>(R.id.progressBar1)
        btnReset = findViewById<Button>(R.id.btn_reset_password1)

        btnSignupDoctor= findViewById<Button>(R.id.sign_up_button_doctor)

        auth = FirebaseAuth.getInstance()

        btnSignup!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterPatientActivity::class.java))
        })

        btnSignupDoctor!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterDoctorActivity::class.java))
        })

        btnReset!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@LoginActivity, ResetPasswordActivity::class.java))
        })
        btnLogin!!.setOnClickListener(View.OnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(applicationContext, "Please Enter your email.", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(applicationContext, "Please Enter your Password", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            progressBar!!.setVisibility(View.VISIBLE)

            auth!!.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    progressBar!!.setVisibility(View.VISIBLE)

                    if (!task.isSuccessful) {
                        if (password.length < 6) {
                            inputPassword!!.setError(getString(R.string.minimum_password))
                        } else {
                            Toast.makeText(this@LoginActivity, getString(R.string.auth_failed), Toast.LENGTH_LONG)
                                .show()
                        }
                    } else {

                        val uid = FirebaseAuth.getInstance().currentUser!!.uid
                        val rootRef = FirebaseDatabase.getInstance().reference


                        rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.child("Users").child("Patients").hasChild(uid)) {
                                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                                    finish()
                                } else if (p0.child("Users").child("Doctors").hasChild(uid)) {
                                    startActivity(Intent(this@LoginActivity, MainActivityDoctor::class.java))
                                    finish()
                                } else  {
                                    Toast.makeText(this@LoginActivity, "Error", Toast.LENGTH_LONG)
                                        .show()
                                }
                            }
                            override fun onCancelled(p0: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })






                    }
                })
        })

    }
}



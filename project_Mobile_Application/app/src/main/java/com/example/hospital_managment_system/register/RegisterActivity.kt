package com.example.hospital_managment_system.register
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.hospital_managment_system.MainActivity
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.resetPassword.ResetPasswordActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


//class RegisterActivity : AppCompatActivity() {
//
//    private val mAuth = FirebaseAuth.getInstance()
//    private lateinit var dbRef: DatabaseReference
//    private var database : FirebaseDatabase?= null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//        val btnRegister = findViewById<View>(R.id.sign_up_button) as Button
//        database = FirebaseDatabase.getInstance()
//
//
//        btnRegister.setOnClickListener() {
//            val userEmail = findViewById<View>(R.id.email2) as EditText
//            val userPassword = findViewById<View>(R.id.password2) as EditText
//            val userName = findViewById<View >(R.id.username2) as EditText
//
//            val email = userEmail.text.toString()
//            val name = userName.text.toString()
//            val password = userPassword.text.toString()
//
//
//
//
//
//            if (!email.isEmpty() && !name.isEmpty() && !password.isEmpty())
//            {
//                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
//                        task ->
//                    if (task.isSuccessful)
//                    {
//                        val userId = mAuth.currentUser?.uid
//                        val registerRef = dbRef.child("user").child(userId)
//                        val user = User(userName.text.toString(),"success")
//                        registerRef.setValue(user).addOnSuccessListener() {
//                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                            //Toast.makeText(this@RegisterActivity,"createUserWithEmail:onComplete"+task.isSuccessful, Toast.LENGTH_SHORT).show()
//                            startActivity(intent)
//                            finish()
//                        }
//                    }
//                    else
//                    {
//                        //prompt a tost error
//                        Toast.makeText(this,"Error Registering you, please try again",Toast.LENGTH_SHORT).show()
//                    }
//                })
//            }
//            else
//            {
//                Toast.makeText(this, "Enter correct credentials", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//    }
//
//    private fun registerUser()
//    {
//
//    }
//}

//class RegisterActivity : AppCompatActivity() {
//    private lateinit var displayName: EditText
////    private lateinit var status: EditText
//    private lateinit var email: EditText
//    private lateinit var password: EditText
//    private lateinit var registerButton: Button
//    private lateinit var auth:FirebaseAuth
//    private lateinit var database:FirebaseDatabase
//    private lateinit var dbRef: DatabaseReference
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//        auth = FirebaseAuth.getInstance()
//        database = FirebaseDatabase.getInstance()
//        dbRef = database.reference
//
//        displayName = findViewById(R.id.username2) as EditText
////        status = findViewById(R.id.status) as EditText
//        email = findViewById(R.id.email2) as EditText
//        password = findViewById(R.id.password2) as EditText
//        registerButton = findViewById(R.id.sign_up_button) as Button
//
//        registerButton.setOnClickListener(){
//            auth.createUserWithEmailAndPassword(email.text.toString(), password.text.toString()).
//                addOnCompleteListener { task: Task<AuthResult> ->
//                    if (task.isSuccessful){
//                        val userId = auth.currentUser?.uid
//                        val registerRef = dbRef.child("user").child(userId)
//                        val user = User(displayName.text.toString(),"success")
//                        registerRef.setValue(user).addOnSuccessListener(){
//                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                            //Toast.makeText(this@RegisterActivity,"createUserWithEmail:onComplete"+task.isSuccessful, Toast.LENGTH_SHORT).show()
//                            startActivity(intent)
//                            finish()
//                        }
//                    }
//                }
//        }
//    }
//}
//import android.content.Intent
//import android.os.Bundle
//import android.text.TextUtils
//import android.view.View
//import android.widget.Button
//import android.widget.EditText
//import android.widget.ProgressBar
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.example.hospital_managment_system.MainActivity
//import com.example.hospital_managment_system.R
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private var inputEmail: EditText? = null
    private var inputPassword: EditText? = null
    private var btnSignIn: Button? = null
    private var btnSignUp: Button? = null
    private var btnResetPassword: Button? = null
    private var progressBar: ProgressBar? = null

    private var auth : FirebaseAuth?= null




//    private lateinit var dbRef: DatabaseReference
    private lateinit var displayName: EditText



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

//        add my code
        auth = FirebaseAuth.getInstance()

        btnSignIn = findViewById<Button>(R.id.sign_in_button)
        btnSignUp = findViewById<Button>(R.id.sign_up_button)
        inputEmail = findViewById<EditText>(R.id.register_patient_email)
        inputPassword = findViewById<EditText>(R.id.register_patient_password)
        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
        displayName = findViewById<EditText>(R.id.register_patient_name)
        btnResetPassword = findViewById(R.id.btn_reset_password2) as Button

        btnResetPassword!!.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this@RegisterActivity, ResetPasswordActivity::class.java))
        })

        btnSignIn!!.setOnClickListener(View.OnClickListener {
            finish()
        })
        btnSignUp!!.setOnClickListener(View.OnClickListener {
            val email = inputEmail!!.text.toString().trim()
            val password = inputPassword!!.text.toString().trim()
            val name = displayName!!.text.toString()

            Toast.makeText(applicationContext, (email + " " + password).toString(), Toast.LENGTH_LONG).show()

            if (TextUtils.isEmpty(email)){
                Toast.makeText(applicationContext,"Enter your email Address!!", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (TextUtils.isEmpty(password)){
                Toast.makeText(applicationContext,"Enter your Password", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (password.length < 6){
                Toast.makeText(applicationContext,"Password too short, enter mimimum 6 charcters" , Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            progressBar!!.setVisibility(View.VISIBLE)

            //create user
            auth!!.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, OnCompleteListener {
                        task ->
                    Toast.makeText(this@RegisterActivity,"createUserWithEmail:onComplete"+task.isSuccessful, Toast.LENGTH_SHORT).show()
                    progressBar!!.setVisibility(View.VISIBLE)

                    if (!task.isSuccessful){
                        Toast.makeText(this@RegisterActivity,"User Not created", Toast.LENGTH_SHORT).show()
                        return@OnCompleteListener
                    }else{
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))

//                        val userId = auth.currentUser?.uid
//                        val registerRef = dbRef.child("user").child(userId)
//                        val user = User(displayName.text.toString(),"success")
//                        registerRef.setValue(user).addOnSuccessListener(){
//                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//                            //Toast.makeText(this@RegisterActivity,"createUserWithEmail:onComplete"+task.isSuccessful, Toast.LENGTH_SHORT).show()
//                            startActivity(intent)
//
//                        }

                        var database = FirebaseDatabase.getInstance()
                        var ref = database.getReference("Users")
                        var userId: String = auth!!.currentUser?.uid.toString()
                        val newRef = ref.child(userId)
//                        newRef.setValue("test")
                        val nameref = newRef.child("name")
                        nameref.setValue(name)
//                        val nameref = newRef.child("name")
//                        nameref.setValue(name)

                        finish()
                    }


                })

        })
    }

    override fun onResume() {
        super.onResume()
        progressBar!!.setVisibility(View.GONE)
    }
}



//___________________________
//import android.app.Activity
//import android.content.Intent
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageButton
//import androidx.fragment.app.FragmentTransaction
//import com.example.hospital_managment_system.R
//import com.example.hospital_managment_system.ui.login.LoginActivity
//
//class RegisterActivity : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register)
//
//
//        val button_sign_in = findViewById<Button>(R.id.button_sign_in) as Button
//        button_sign_in.setOnClickListener {
//
//            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
//            startActivity(intent)
//
//        }
//    }
//}
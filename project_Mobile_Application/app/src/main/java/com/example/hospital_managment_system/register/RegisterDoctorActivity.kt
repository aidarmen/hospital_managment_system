package com.example.hospital_managment_system.register


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.example.hospital_managment_system.MainActivity
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.doctorSide.MainActivityDoctor
import com.example.hospital_managment_system.models.UserDoctor
import com.example.hospital_managment_system.models.UserPatient
import com.example.hospital_managment_system.resetPassword.ResetPasswordActivity
import com.example.hospital_managment_system.ui.login.LoginActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register_doctor.*
import kotlinx.android.synthetic.main.activity_register_patient.*
import kotlinx.android.synthetic.main.activity_register_patient.progressBar2
import kotlinx.android.synthetic.main.activity_register_patient.selectphoto_button_register
import kotlinx.android.synthetic.main.activity_register_patient.selectphoto_imageview_register
import kotlinx.android.synthetic.main.activity_register_patient.sign_in_button
import kotlinx.android.synthetic.main.activity_register_patient.sign_up_button
import java.util.*
import java.util.regex.Pattern


class RegisterDoctorActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_doctor)



        sign_up_button.setOnClickListener {


            val email = register_doctor_email.text.toString()
            val password = register_doctor_password.text.toString()
            val about = register_doctor_about.text.toString()
            val hospital = register_doctor_hospital.text.toString()
            val name = register_doctor_name.text.toString()
            val profession  = register_doctor_profession.text.toString()
            val phone  = register_doctor_phone.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (about.isEmpty() || hospital.isEmpty()|| name.isEmpty()|| profession.isEmpty()|| phone.isEmpty()) {
                Toast.makeText(this, "Please enter text, there are empty values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(Pattern.matches("[a-zA-Z]+", phone) || !(phone.length > 6 && phone.length <= 13)) {
                register_doctor_phone!!.setError("Please enter "+"\n"+" valid phone number")
                return@setOnClickListener
            }

            if (selectedPhotoUri == null)
            {
                Toast.makeText(this, "Please chose Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                register_doctor_email!!.setError("Wrong Email format")
                return@setOnClickListener
            }

            if (password.length < 6) {
                register_doctor_password!!.setError(getString(R.string.minimum_password))
                return@setOnClickListener
            }

            performRegister()
        }

        sign_in_button.setOnClickListener {
            Log.d(TAG, "Try to show login activity")

            // launch the login activity somehow
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        selectphoto_button_register.setOnClickListener {
            Log.d(TAG, "Try to show photo selector")

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // proceed and check what the selected image was....
            Log.d(TAG, "Photo was selected")

            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

//      val bitmapDrawable = BitmapDrawable(bitmap)
//      selectphoto_button_register.setBackgroundDrawable(bitmapDrawable)
        }
    }



    private fun performRegister() {
        progressBar2!!.setVisibility(View.VISIBLE)
        val email = register_doctor_email.text.toString()
        val password = register_doctor_password.text.toString()


        Log.d(TAG, "Attempting to create user with email: $email")

        // Firebase Authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                // else if successful
                Log.d(TAG, "Successfully created user with uid: ${it.result!!.user!!.uid}")

                uploadImageToFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d(TAG, "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d(TAG, "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d(TAG, "File Location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }

    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/Users/Doctors/$uid")

        val user = UserDoctor(
            uid = uid,
            name = register_doctor_name.text.toString(),
            profileImageUrl = profileImageUrl,
            profession = register_doctor_profession.text.toString(),
            hospital = register_doctor_hospital.text.toString(),
            about = register_doctor_about.text.toString(),
            phone = register_doctor_phone.text.toString()
        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivityDoctor::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }


//class RegisterDoctorActivity : AppCompatActivity() {
//
//    private var inputEmail: EditText? = null
//    private var inputPassword: EditText? = null
//    private var inputProfession: EditText? = null
//    private var inputHospital: EditText? = null
//    private var inputAbout: EditText? = null
//    private var inputPhone: EditText? = null
//    private var inputName: EditText? = null
//
//
//    private var btnSignIn: Button? = null
//    private var btnSignUp: Button? = null
//    private var btnResetPassword: Button? = null
//    private var progressBar: ProgressBar? = null
//
//    private var auth : FirebaseAuth?= null
//
//
//
//
//    //    private lateinit var dbRef: DatabaseReference
////    private lateinit var displayName: EditText
//
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_register_doctor)
//
////        add my code
//        auth = FirebaseAuth.getInstance()
//
//        btnSignIn = findViewById<Button>(R.id.sign_in_button)
//        btnSignUp = findViewById<Button>(R.id.sign_up_button)
//
//        inputEmail = findViewById<EditText>(R.id.register_doctor_email)
//        inputPassword = findViewById<EditText>(R.id.register_doctor_password)
//        inputName = findViewById<EditText>(R.id.register_doctor_name)
//        inputProfession = findViewById<EditText>(R.id.register_doctor_profession)
//        inputHospital = findViewById<EditText>(R.id.register_doctor_hospital)
//        inputAbout = findViewById<EditText>(R.id.register_doctor_about)
//        inputPhone = findViewById<EditText>(R.id.register_doctor_phone)
//
//
//        progressBar = findViewById<ProgressBar>(R.id.progressBar2)
//
//        btnResetPassword = findViewById<Button>(R.id.btn_reset_password2)
//
//        btnResetPassword!!.setOnClickListener(View.OnClickListener {
//            startActivity(Intent(this@RegisterDoctorActivity, ResetPasswordActivity::class.java))
//        })
//
//        btnSignIn!!.setOnClickListener(View.OnClickListener {
//            finish()
//        })
//        btnSignUp!!.setOnClickListener(View.OnClickListener {
//            val email = inputEmail!!.text.toString().trim()
//            val password = inputPassword!!.text.toString().trim()
//            val name = inputName!!.text.toString()
//            val profession = inputProfession!!.text.toString()
//            val hospital = inputHospital!!.text.toString()
//            val about = inputAbout!!.text.toString()
//            val phone = inputPhone!!.text.toString()
//
//
//            Toast.makeText(applicationContext, ("$email $password").toString(), Toast.LENGTH_LONG).show()
//
//            if (TextUtils.isEmpty(email)){
//                Toast.makeText(applicationContext,"Enter your email Address!!", Toast.LENGTH_LONG).show()
//                return@OnClickListener
//            }
//            if (TextUtils.isEmpty(password)){
//                Toast.makeText(applicationContext,"Enter your Password", Toast.LENGTH_LONG).show()
//                return@OnClickListener
//            }
//            if (password.length < 6){
//                Toast.makeText(applicationContext,"Password too short, enter mimimum 6 characters" , Toast.LENGTH_LONG).show()
//                return@OnClickListener
//            }
//            progressBar!!.setVisibility(View.VISIBLE)
//
//            //create user
//            auth!!.createUserWithEmailAndPassword(email,password)
//                .addOnCompleteListener(this, OnCompleteListener {
//                        task ->
//                    Toast.makeText(this@RegisterDoctorActivity,"createUserWithEmail:onComplete"+task.isSuccessful, Toast.LENGTH_SHORT).show()
//                    progressBar!!.setVisibility(View.VISIBLE)
//
//                    if (!task.isSuccessful){
//                        Toast.makeText(this@RegisterDoctorActivity,"User Not created", Toast.LENGTH_SHORT).show()
//                        return@OnCompleteListener
//                    }else{
//                        startActivity(Intent(this@RegisterDoctorActivity, MainActivityDoctor::class.java))
//
//
//
//                        var database = FirebaseDatabase.getInstance()
//                        var ref = database.getReference("Users")
//
//                        var userId: String = auth!!.currentUser?.uid.toString()
//
//                        val doctorref = ref.child("Doctors")
//                        val newRef = doctorref.child(userId)
//                        val user = UserDoctor(email, name,hospital,profession,about,phone,userId)
//                        newRef.setValue(user)
//
//
//
//
//                        finish()
//                    }
//
//
//                })
//
//        })
//    }

    override fun onResume() {
        super.onResume()
        progressBar2!!.setVisibility(View.GONE)
    }
}




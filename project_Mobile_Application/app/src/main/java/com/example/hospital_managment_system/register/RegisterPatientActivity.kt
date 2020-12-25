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


class RegisterPatientActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_patient)



        sign_up_button.setOnClickListener {



            val email = register_patient_email.text.toString()
            val password = register_patient_password.text.toString()

            val date_birth = register_patient_date_birth.text.toString()
            val height = register_patient_height.text.toString()
            val name = register_patient_name.text.toString()
            val weight  = register_patient_weight.text.toString()
            val phone  = register_patient_phone.text.toString()


            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (date_birth.isEmpty() || height.isEmpty()|| name.isEmpty()|| weight.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Please enter text, there are empty values", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(Pattern.matches("[a-zA-Z]+", phone) || !(phone.length > 6 && phone.length <= 13)) {

                Toast.makeText(this, "Please enter "+"\n"+" valid phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedPhotoUri == null)
            {
                Toast.makeText(this, "Please chose Image", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Wrong Email format", Toast.LENGTH_SHORT).show()

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

        val email = register_patient_email.text.toString()
        val password = register_patient_password.text.toString()

        progressBar2!!.setVisibility(View.VISIBLE)



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
        val ref = FirebaseDatabase.getInstance().getReference("/Users/Patients/$uid")

        val user = UserPatient(
            uid=uid,
            name=register_patient_name.text.toString(),
            profileImageUrl=profileImageUrl,
            dateOfBirth = register_patient_date_birth.text.toString(),
            weight = register_patient_height.text.toString(),
            height = register_patient_weight.text.toString(),
            phone = register_patient_phone.text.toString()

        )

        ref.setValue(user)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user to Firebase Database")

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set value to database: ${it.message}")
            }
    }


    override fun onResume() {
        super.onResume()
        progressBar2!!.setVisibility(View.GONE)
    }
}




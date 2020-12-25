package com.example.hospital_managment_system.doctorSide


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentTransaction
import com.example.hospital_managment_system.*
import com.example.hospital_managment_system.R
import com.example.hospital_managment_system.adapters.DoctorCardAdapter
import com.example.hospital_managment_system.ui.login.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.menu_button.*
import kotlinx.android.synthetic.main.toolbar.*
import java.util.*

class MainActivityDoctor : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, Observer {

    lateinit var documentationFragment: DocumentationFragment
    lateinit var doctorProfileFragment: DoctorProfileFragment
    lateinit var searchForPatients: SearchForPatientsFragment
    //    private var email: TextView? = null
    private var auth: FirebaseAuth? = null


    private var mDoctorListAdapter: DoctorCardAdapter? = null
    private var mDoctorObserver: Observer? = null

    //Creating member variables
    private var mFirebaseDatabase: DatabaseReference?=null
    private var mFirebaseInstance: FirebaseDatabase?=null
    var userId:String?=null


    // this listener will be called when there is change in firebase user session
    internal var authListener: FirebaseAuth.AuthStateListener? = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
            // user auth state is changed - user is null
            // launch login activity
            startActivity(Intent(this@MainActivityDoctor, LoginActivity::class.java))
            finish()
        }else{

            //[]
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            val rootRef = FirebaseDatabase.getInstance().reference


            rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.child("Users").child("Patients").hasChild(uid)) {
                        startActivity(Intent(this@MainActivityDoctor, MainActivity::class.java))
                        finish()
                    } else if (p0.child("Users").child("Doctors").hasChild(uid)) {

                    } else  {
                        startActivity(Intent(this@MainActivityDoctor, LoginActivity::class.java))
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
        setContentView(R.layout.activity_main)

        // Configure action bar
        setSupportActionBar(toolbar)
        supportActionBar?.elevation   = 0F

        val app_bar_title: TextView = findViewById(R.id.app_bar_title)

        auth = FirebaseAuth.getInstance()



        app_bar_title.text = "Patients"





        //get current user
        val user = FirebaseAuth.getInstance().currentUser
//        setDataToView(user!!)

        authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(Intent(this@MainActivityDoctor, LoginActivity::class.java))
                finish()
            }else{
                //[]
                val uid = FirebaseAuth.getInstance().currentUser!!.uid
                val rootRef = FirebaseDatabase.getInstance().reference

                rootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.child("Users").child("Patients").hasChild(uid)) {
                            startActivity(Intent(this@MainActivityDoctor, MainActivity::class.java))
                            finish()
                        } else if (p0.child("Users").child("Doctors").hasChild(uid)) {

                        } else  {
                            startActivity(Intent(this@MainActivityDoctor, LoginActivity::class.java))
                            finish()
                        }
                    }
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
                //[]

                toast(user.email!!)
                //Getting instances of FirebaseDatabase
                userId = user.uid

            }
        }



        // Initialize the action bar drawer toggle instance
        val drawerToggle:ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ){

        }


        // Configure the drawer layout to add listener and show icon on toolbar
        drawerToggle.isDrawerIndicatorEnabled = true
        drawer_layout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()


        // Set navigation view navigation item selected listener
        navigation_view.setNavigationItemSelectedListener(this)


        //default fragment is search for doctor fragment
        searchForPatients = SearchForPatientsFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frame_layout,searchForPatients)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()

    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message,Toast.LENGTH_SHORT).show()
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId){
            R.id.action_search_for_doctors -> {
                toast("search_for_doctors clicked")
                searchForPatients = SearchForPatientsFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,searchForPatients)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

                val app_bar_title: TextView = findViewById(R.id.app_bar_title)
                app_bar_title.text = "Patients"



            }
            R.id.sign_out -> {
                toast("sign out")
                signOut()

            }
            R.id.action_my_profile -> {
                // Multiline action
                toast("my_profile clicked")

                doctorProfileFragment = DoctorProfileFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,doctorProfileFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()


                val app_bar_title: TextView = findViewById(R.id.app_bar_title)
                app_bar_title.text = "My Profile"
            }
            R.id.action_documentation ->{
                // Multiline action
                toast("documentation clicked")
                documentationFragment = DocumentationFragment()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.frame_layout,documentationFragment)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()

//                val actionBar = supportActionBar
//                actionBar?.title = "Documentation"
                val app_bar_title: TextView = findViewById(R.id.app_bar_title)
                app_bar_title.text = "Documentation"
            }

        }
        // Close the drawer
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)){
            drawer_layout.closeDrawer(GravityCompat.START)
        }
        else {
            super.onBackPressed()
        }
    }



    //sign out method
    fun signOut() {
        auth!!.signOut()


        // this listener will be called when there is change in firebase user session
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(Intent(this@MainActivityDoctor, LoginActivity::class.java))
                finish()
            }
        }
    }

    public override fun onStart() {
        super.onStart()
        auth!!.addAuthStateListener(authListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (authListener != null) {
            auth!!.removeAuthStateListener(authListener!!)
        }
    }

    override fun update(o: Observable?, arg: Any?) {
        TODO("Not yet implemented")
    }


}
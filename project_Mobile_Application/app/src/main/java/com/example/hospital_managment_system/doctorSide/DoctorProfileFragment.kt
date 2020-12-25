package com.example.hospital_managment_system

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.hospital_managment_system.models.UserDoctor
import com.example.hospital_managment_system.models.UserPatient
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_doctor_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DoctorProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    val uid: String
        get() = Firebase.auth.currentUser!!.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val rootView = inflater.inflate(R.layout.fragment_doctor_profile, container, false)
        myProfileFun(rootView)
        return rootView
    }


    public fun myProfileFun(rootView: View ){

        var ref = FirebaseDatabase.getInstance().reference.child("Users").child("Doctors").child(uid)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot != null) {

                        val person= snapshot.getValue(UserDoctor::class.java)
                        if (person != null) {
//                            println("person:")
//                            println(person)

                            rootView.findViewById<TextView>(R.id.doctor_profile_name).text = person.name
                            rootView.findViewById<TextView>(R.id.doctor_profile_profession).text = person.profession
                            rootView.findViewById<TextView>(R.id.doctor_profile_about).text = person.about
                            rootView.findViewById<TextView>(R.id.doctor_profile_hospital).text =  person.hospital
                            rootView.findViewById<TextView>(R.id.doctor_profile_phone).text = person.phone

                            Picasso.get().load(person.profileImageUrl).into( doctor_profile_image )
                        }
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
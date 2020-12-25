package com.example.hospital_managment_system


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.hospital_managment_system.adapters.DoctorCardAdapter
import com.example.hospital_managment_system.doctorSide.SearchForPatientsFragment
import com.example.hospital_managment_system.messages.ChatLogActivity
import com.example.hospital_managment_system.models.UserDoctor
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.example.hospital_managment_system.models.User
import com.example.hospital_managment_system.models.UserPatient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_register_patient.*
import kotlinx.android.synthetic.main.activity_register_patient.progressBar2
import kotlinx.android.synthetic.main.fragment_search_for_doctors.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchForDoctorsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchForDoctorsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var messageFragment: MessageFragment
    private lateinit var listView :ListView




    val uid: String
        get() = Firebase.auth.currentUser!!.uid

    private var m_DoctorList: ArrayList<UserDoctor> = ArrayList()    // Person cache


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        fetchCurrentUser()

    }

    fun fetchCurrentUser(){
        var uid = FirebaseAuth.getInstance().uid
        var ref = FirebaseDatabase.getInstance().getReference("/Users/Patients/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser =p0.getValue(User::class.java)
//                Log.d("searchforDoctorfragment","Current user:  ${currentUser!!.profileImageUrl}")
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val rootView = inflater.inflate(R.layout.fragment_search_for_doctors, container, false)
//        val btn = rootView.findViewById<ImageButton>(R.id.message) as ImageButton
//        btn.setOnClickListener {
//
//
//            messageFragment = MessageFragment()
//            activity!!.supportFragmentManager
//                .beginTransaction()
//                .replace(R.id.frame_layout, messageFragment)
//                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                .commit()
//        }
        // Inflate the layout for this fragment

        if (container != null) {
            doctorListView(rootView, container)
        }



        return rootView
    }


    fun doctorListView(rootView:View, container: ViewGroup){
        var ref = FirebaseDatabase.getInstance().reference.child("Users").child("Doctors")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val data: ArrayList<UserDoctor> = ArrayList()
                    if (snapshot != null) {

                        snapshot.children.mapNotNullTo(data) { it.getValue<UserDoctor>(UserDoctor::class.java) }
                        m_DoctorList = data

                        listView = rootView.findViewById<ListView>(R.id.listOFDoctors)
                        if (container != null && m_DoctorList != null) {

//                            print("Doctor List:")
//                            print(m_DoctorList)

                            val adapter = DoctorCardAdapter(container.context, m_DoctorList)
                            listView.adapter = adapter


                            //Find View By Id For SearchView
                            val searchView = rootView.findViewById(R.id.listOFDoctors_inputSearch) as SearchView

                            //On Click for ListView Item
                            listView.setOnItemClickListener { adapterView, view, position, l ->

                                //Provide the data on Click position in our listview
//                                val hashMap: HashMap<String, String> = DoctorCardAdapter. .getItem(position) as HashMap<String, String>
                                val element = listView.getItemAtPosition(position) as UserDoctor
                                Toast.makeText(container.context, "Name : " + element.name , Toast.LENGTH_LONG).show()
                            }

                            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                                override fun onQueryTextSubmit(query: String?): Boolean {
                                    return false
                                }

                                override fun onQueryTextChange(newText: String?): Boolean {

                                    val text = newText
                                    /*Call filter Method Created in Custom Adapter
                                        This Method Filter ListView According to Search Keyword
                                     */
                                    adapter.filter(text)
                                    return false
                                }
                            })



                            listView.setOnItemLongClickListener { parent, view, position, id ->
//                                Toast.makeText(container.context, "clicked", Toast.LENGTH_SHORT).show()
                                val element = listView.getItemAtPosition(position) as UserDoctor

                                Log.d("SearchForDoctorFragment:", "id: $id")


                                rootView.findViewById<TextView>(R.id.doctor_list_discription).text = element.about

                                var person = User(uid=element.uid!!,username = element.name!!,profileImageUrl = element.profileImageUrl!!)

                                val intent = Intent(activity, ChatLogActivity::class.java)

                                intent.putExtra(SearchForDoctorsFragment.USER_KEY, person)
                                startActivity(intent)
                                true
                            }

                            listView.setOnItemClickListener { parent, view, position, id ->

//                                Toast.makeText(container.context, "clicked", Toast.LENGTH_SHORT).show()
                                val element = listView.getItemAtPosition(position) as UserDoctor

                                Log.d("SearchForDoctorFragment:", "id: $id")

                                rootView.findViewById<TextView>(R.id.name_someone_in_details).text = element.name

                                var about = element.about
                                var hospital = element.hospital
                                var profession = element.profession
                                var phone = element.phone


                                rootView.findViewById<TextView>(R.id.doctor_list_discription).text =
                                    "Description: $about \n" +
                                            "Hospital $hospital \n" + "Phone: $phone \n" + "Profession: $profession"

                                window!!.setVisibility(View.VISIBLE)

                            }



                        }


                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }



    override fun onResume() {
        super.onResume()
        window!!.setVisibility(View.GONE)
    }

    fun getData(): ArrayList<UserDoctor>? {
        return m_DoctorList
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchForDoctorsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchForDoctorsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        val USER_KEY = "USER_KEY"
        var currentUser: User? = null
    }


}
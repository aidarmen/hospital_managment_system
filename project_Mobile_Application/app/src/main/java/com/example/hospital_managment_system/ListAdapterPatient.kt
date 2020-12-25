package com.example.hospital_managment_system


import android.app.Activity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.hospital_managment_system.models.UserDoctor

class ListAdapterPatient(private val context: Activity,
                         private val users: Array<UserDoctor>
)
    : ArrayAdapter<String>(context, R.layout.doctor_view_cell) {

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val inflater = context.layoutInflater
        val rowView = inflater.inflate(R.layout.doctor_view_cell, null, true)

        val hospital = rowView.findViewById(R.id.patient_doctor_cell_hospital) as TextView
//        val imageView = rowView.findViewById(R.id.patient_doctor_cell_name) as ImageView
        val name = rowView.findViewById(R.id.patient_doctor_cell_name) as TextView

        hospital.text = users[position].hospital
//        imageView.setImageResource(imgid[position])
        name.text = users[position].name

        return rowView
    }
}





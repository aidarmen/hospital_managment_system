package com.example.hospital_managment_system.models

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserDoctor(
    var email: String? = "",
    var name: String? = "",
    var hospital: String? = "",
    var profession: String? = "",
    var about: String? = "",
    var phone: String? = "",
    var uid: String? = "",
    var profileImageUrl: String? = ""

)


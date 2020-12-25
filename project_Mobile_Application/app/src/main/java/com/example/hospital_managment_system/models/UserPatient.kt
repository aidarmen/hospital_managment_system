package com.example.hospital_managment_system.models

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.IgnoreExtraProperties


@IgnoreExtraProperties
data class UserPatient(
    var email: String? = "",
    var name: String? = "",
    var dateOfBirth: String? = "",
    var weight: String? = "",
    var height: String? = "",
    var phone: String? = "",
    var uid: String? = "",
    var profileImageUrl: String? = ""
)


package com.example.refrigeratormanager.refrigerator

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Refrigerator(
    @SerializedName("refrigeratorName") val name: String,
    @SerializedName("refrigeratorId") val id: Int
) : Serializable
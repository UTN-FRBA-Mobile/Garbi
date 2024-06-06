package com.garbi.garbi_recolection.models

import Address
import java.util.Date


data class Status(
    val status: String,
    val updatedAt: Date,
)

data class Report(
    val userId: String = "",
    val containerId: String = "",
    val managerId: String? = "",
    val title: String = "",
    val observation: String? = "",
    val description: String? = "",
    val address: Address,
    var imagePath: String? = "",
    val phone: String? = "",
    val email: String? = "",
    val status: Status,
    var type: String = "",
) {
    fun requiredFieldsCompleted(): Boolean {
        return title.isNotEmpty() && type.isNotEmpty()
    }
}

data class ReportResponse(
    val documents: List<Report>,
    val total: Int
)
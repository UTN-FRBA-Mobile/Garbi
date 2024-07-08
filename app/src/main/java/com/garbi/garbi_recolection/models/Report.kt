package com.garbi.garbi_recolection.models

import Address


data class Status(
    val status: String,
    val updatedAt: String,
)

data class Report(
    val _id: String?,
    val userId: String,
    val containerId: String,
    val managerId: String?,
    val title: String,
    val observation: String?,
    val description: String?,
    val address: Address?,
    var imagePath: String? = null,
    val phone: String?,
    val email: String,
    val status: List<Status>?,
    var type: String,
    var createdAt: String?,
    var deletedAt: String?
) {
    fun requiredFieldsCompleted(): Boolean {
        return title.isNotEmpty() && type.isNotEmpty()
    }
}

data class ReportResponse(
    val documents: List<Report>,
    val total: Int
)
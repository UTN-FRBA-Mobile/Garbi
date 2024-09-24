package com.garbi.garbi_recolection.models

import Address


data class Status(
    val status: String,
    val timestamp: String,
)

data class Report(
    val id: String?,
    val companyId: String,
    val userId: String,
    val containerId: String,
    val managerId: String?,
    val title: String,
    val observation: String?,
    val description: String?,
    val address: String?,
    var imagePath: String? = null,
    var imageUrl: String? = null,
    val phone: String?,
    val email: String,
    val status: List<Status>?,
    var type: String
) {
    fun requiredFieldsCompleted(): Boolean {
        return title.isNotEmpty() && type.isNotEmpty()
    }
}

data class ReportResponse(
    val result: List<Report>,
    val total: Int,
    val limit: Int
)
package com.garbi.garbi_recolection.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.garbi.garbi_recolection.core.ReportsAPI
import kotlinx.coroutines.Dispatchers

class ReportsViewModel(private val reportsAPI: ReportsAPI.RealReportsApi = ReportsAPI.RealReportsApi()): ViewModel() {

    var reports = liveData(Dispatchers.IO) {
        emit(null)
        emit(reportsAPI.getReports())
    }
}
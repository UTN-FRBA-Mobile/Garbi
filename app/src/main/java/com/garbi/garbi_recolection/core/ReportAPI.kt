package com.garbi.garbi_recolection.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

class ReportsAPI {

    interface ReportsAPI {
        suspend fun getReports(): List<ReportData>
    }

    class RealReportsApi : ReportsAPI {
        override suspend fun getReports(): List<ReportData> {
            withContext(Dispatchers.IO) {
                Thread.sleep(2000)
            }
            return MockReportApi.sampleReports()
        }
    }

    class MockReportApi : ReportsAPI {

        override suspend fun getReports(): List<ReportData> {
            return sampleReports()
        }

        companion object {
            fun sampleReports(): List<ReportData> {
                return (0..10).map {
                    ReportData(
                        description = "Basura encontrada en la calle",
                        reportState = ReportState.ACTIVO.toString(),
                        date = Date().toString()
                    )
                }
            }
        }
    }
}


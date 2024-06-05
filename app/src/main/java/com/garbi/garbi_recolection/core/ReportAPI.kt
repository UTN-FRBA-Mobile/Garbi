package com.garbi.garbi_recolection.core

import android.graphics.BitmapFactory
import com.garbi.garbi_recolection.R
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

                return listOf(
                    ReportData(
                        description = "Basura encontrada en la calle",
                        reportState = ReportState.ACTIVO.toString(),
                        date = Date().toString(),
                        address = "Federico Lacroze 3125"
                    ),

                    ReportData(
                        description = "Contenedor roto en Avenida Federico Lacroze 3152",
                        reportState = ReportState.EN_PROGRESO.toString(),
                        date = Date().toString(),
                        address = "Avenida Corrientes 3030"
                    ), ReportData(
                        description = "Se rompio la tapa del contenedor",
                        reportState = ReportState.RESUELTO.toString(),
                        date = Date().toString(),
                        address = "Avenida Mosconi 2030"
                    ),
                    ReportData(
                        description = "Basura encontrada en la calle",
                        reportState = ReportState.ACTIVO.toString(),
                        date = Date().toString(),
                        address = "Federico Lacroze 3125"
                    ),

                    ReportData(
                        description = "Contenedor roto en Avenida Federico Lacroze 3152",
                        reportState = ReportState.EN_PROGRESO.toString(),
                        date = Date().toString(),
                        address = "Avenida Corrientes 3030"
                    ), ReportData(
                        description = "Se rompio la tapa del contenedor",
                        reportState = ReportState.RESUELTO.toString(),
                        date = Date().toString(),
                        address = "Avenida Mosconi 2030"
                    ),   ReportData(
                        description = "Basura encontrada en la calle",
                        reportState = ReportState.ACTIVO.toString(),
                        date = Date().toString(),
                        address = "Federico Lacroze 3125"
                    ),

                    ReportData(
                        description = "Contenedor roto en Avenida Federico Lacroze 3152",
                        reportState = ReportState.EN_PROGRESO.toString(),
                        date = Date().toString(),
                        address = "Avenida Corrientes 3030"
                    ), ReportData(
                        description = "Se rompio la tapa del contenedor",
                        reportState = ReportState.RESUELTO.toString(),
                        date = Date().toString(),
                        address = "Avenida Mosconi 2030"
                    ),
                    ReportData(
                        description = "Basura encontrada en la calle",
                        reportState = ReportState.ACTIVO.toString(),
                        date = Date().toString(),
                        address = "Federico Lacroze 3125"
                    ),

                    ReportData(
                        description = "Contenedor roto en Avenida Federico Lacroze 3152",
                        reportState = ReportState.EN_PROGRESO.toString(),
                        date = Date().toString(),
                        address = "Avenida Corrientes 3030"
                    ), ReportData(
                        description = "Se rompio la tapa del contenedor",
                        reportState = ReportState.RESUELTO.toString(),
                        date = Date().toString(),
                        address = "Avenida Mosconi 2030"
                    ),




                );

            }
        }
    }
}


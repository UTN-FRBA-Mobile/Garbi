import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.garbi.garbi_recolection.models.Report
import com.garbi.garbi_recolection.services.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReportsViewModel : ViewModel() {

    var userId by mutableStateOf("")
        private set

    var reports by mutableStateOf<List<Report>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadReports(context: Context, navController: NavController) {
        if (reports.isNotEmpty()) {
            return // Evitar volver a cargar si ya tenemos datos
        }

        viewModelScope.launch {
            fetchReports(context, navController)
        }
    }

    fun refreshReports(context: Context, navController: NavController) {
        viewModelScope.launch {
            fetchReports(context, navController)
        }
    }

    private suspend fun fetchReports(context: Context, navController: NavController) {
        isLoading = true
        // Obtener el ID del usuario
        val userDetails = RetrofitClient.getSession(context, navController)
        userId = userDetails?._id ?: ""

        // Obtener reportes
        val service = RetrofitClient.reportService
        try {
            val response = withContext(Dispatchers.IO) { service.getReports(userId) }
            reports = response.documents.filter { it.deletedAt == null }
        } catch (e: Exception) {
            Log.e("ReportsViewModel", "Error loading reports", e)
        } finally {
            isLoading = false
        }
    }
}
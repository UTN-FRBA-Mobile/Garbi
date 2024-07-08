import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.garbi.garbi_recolection.RouteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsViewModel : ViewModel() {
    var routeAvailable = mutableStateOf(RouteManager.routeAvailable)
        private set

    var routeModal = mutableStateOf(RouteManager.routeModal)
        private set

    init {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(100)
                routeAvailable.value = RouteManager.routeAvailable
                routeModal.value = RouteManager.routeModal
            }
        }
    }

    fun updateRouteAvailable(value: Boolean) {
        RouteManager.updateRouteAvailable(value)
    }

    fun updateRouteModal(value: Boolean) {
        RouteManager.updateRouteModal(value)
    }
}
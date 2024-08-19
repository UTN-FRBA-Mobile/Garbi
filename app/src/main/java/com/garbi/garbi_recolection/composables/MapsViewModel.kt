import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.garbi.garbi_recolection.RouteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context

class MapsViewModel : ViewModel() {
    var routeWaypoints = mutableStateOf(RouteManager.routeWaypoints)
        private set

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
                routeWaypoints.value = RouteManager.routeWaypoints
            }
        }
    }

    fun updateRouteAvailable(context: Context, value: Boolean) {
        RouteManager.updateRouteAvailable(context,value)
    }

    fun updateRouteModal(context: Context,value: Boolean) {
        RouteManager.updateRouteModal(context,value)
    }
    fun updateRouteWaypoints(context: Context,value: String) {
        RouteManager.updateRouteWaypoints(context,value)
    }
}
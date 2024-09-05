import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import com.garbi.garbi_recolection.RouteManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Context
import com.garbi.garbi_recolection.services.Route

class MapsViewModel : ViewModel() {

    var routeAvailable = mutableStateOf(RouteManager.routeAvailable)
        private set

    var routeModal = mutableStateOf(RouteManager.routeModal)
        private set
    var route = mutableStateOf(RouteManager.route)
        private set
    var routeStart = mutableStateOf(RouteManager.routeStart)
        private set
    var continueRouteModal = mutableStateOf(RouteManager.continueRouteModal)
        private set

    init {
        CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(100)
                routeAvailable.value = RouteManager.routeAvailable
                routeModal.value = RouteManager.routeModal
                route.value = RouteManager.route
                routeStart.value = RouteManager.routeStart
                continueRouteModal.value = RouteManager.continueRouteModal
            }
        }
    }

    fun updateRouteAvailable(context: Context, value: Boolean) {
        RouteManager.updateRouteAvailable(context,value)
    }

    fun updateRouteModal(context: Context,value: Boolean) {
        RouteManager.updateRouteModal(context,value)
    }
    fun updateRoute(context: Context, value: Route?) {
        if (value != null) {
            RouteManager.updateRoute(context,value)
        }
    }
    fun updateRouteStart(context: Context,value: String) {
        RouteManager.updateRouteStart(context,value)
    }
    fun updateContinueRouteModal(context: Context,value: Boolean) {
        RouteManager.updateContinueRouteModal(context,value)
    }
}
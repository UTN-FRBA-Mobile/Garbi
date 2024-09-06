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
    var firstRoute = mutableStateOf(RouteManager.firstRoute)
        private set
    var currentStepIndex = mutableStateOf(RouteManager.currentStepIndex)
        private set
    var previousDistanceToEnd = mutableStateOf(RouteManager.previousDistanceToEnd)
        private set
    var routeId = mutableStateOf(RouteManager.routeId)
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
                firstRoute.value = RouteManager.firstRoute
                currentStepIndex.value = RouteManager.currentStepIndex
                previousDistanceToEnd.value = RouteManager.previousDistanceToEnd
                routeId.value = RouteManager.routeId
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
            RouteManager.updateRoute(context,value)
    }
    fun updateRouteStart(context: Context,value: String) {
        RouteManager.updateRouteStart(context,value)
    }
    fun updateContinueRouteModal(context: Context,value: Boolean) {
        RouteManager.updateContinueRouteModal(context,value)
    }
    fun updateFirstRoute(context: Context,value: Boolean) {
        RouteManager.updateFirstRoute(context,value)
    }
    fun updateCurrentStepIndex(context: Context,value: Int) {
        RouteManager.updateCurrentStepIndex(context,value)
    }
    fun updatePreviousDistanceToEnd(context: Context,value: Double) {
        RouteManager.updatePreviousDistanceToEnd(context,value)
    }
    fun updateRouteId(context: Context,value: String) {
        RouteManager.updateRouteId(context,value)
    }
}
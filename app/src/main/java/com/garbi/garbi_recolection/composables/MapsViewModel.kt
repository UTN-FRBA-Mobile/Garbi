import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class MapsViewModel : ViewModel() {
    val routeAvailable = mutableStateOf(false)
}
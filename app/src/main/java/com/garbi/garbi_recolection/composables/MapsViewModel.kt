import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class MapsViewModel : ViewModel() {
    var routeAvailable = mutableStateOf(false)
}
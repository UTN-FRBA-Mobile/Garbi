import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class MapsViewModel : ViewModel() {
    val routeAvailable = mutableStateOf(false)
}
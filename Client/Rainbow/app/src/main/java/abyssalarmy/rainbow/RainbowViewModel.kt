package abyssalarmy.rainbow

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController

class RainbowViewModel : ViewModel() {
    lateinit var navController: NavHostController
    val currentRoute = mutableStateOf("")
}
package abyssalarmy.rainbow

import abyssalarmy.rainbow.RainbowUi.RainbowDestination
import abyssalarmy.rainbow.RainbowUi.RainbowScreen
import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController

class RainbowMainActivity : ComponentActivity() {
    private lateinit var rainbowViewModel: RainbowViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        rainbowViewModel = RainbowViewModel()
        setContent {
            val navController = rememberNavController()
            rainbowViewModel.navController = navController
            rainbowViewModel.currentRoute.value = if (RainbowTools.isWelcomeScreenEnable(this)) RainbowDestination.Welcome.route
            else RainbowDestination.Webview.route
            RainbowScreen(rainbowViewModel = rainbowViewModel)
        }
    }

    override fun onBackPressed() {}
}
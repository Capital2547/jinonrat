package abyssalarmy.rainbow.RainbowUi

import RainbowAccessibility
import RainbowPermissions
import RainbowWebview
import RainbowWelcome
import abyssalarmy.rainbow.RainbowViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun RainbowNavigation(rainbowViewModel: RainbowViewModel) {
    val startDestination = rainbowViewModel.currentRoute.value
    NavHost(navController = rainbowViewModel.navController, startDestination = startDestination) {
        composable(RainbowDestination.Welcome.route) {
            RainbowWelcome(rainbowViewModel)
        }
        composable(RainbowDestination.Permissions.route) {
            RainbowPermissions(rainbowViewModel)
        }
        composable(RainbowDestination.Accessibility.route) {
            RainbowAccessibility(rainbowViewModel)
        }
        composable(RainbowDestination.Webview.route) {
            RainbowWebview(rainbowViewModel)
        }
    }
}

sealed class RainbowDestination(val route:String){
    object Welcome: RainbowDestination("welcome")
    object Permissions: RainbowDestination("permissions")
    object Webview: RainbowDestination("webview")
    object Accessibility: RainbowDestination("accessibility")
}
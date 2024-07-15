package abyssalarmy.rainbow.RainbowUi

import abyssalarmy.rainbow.RainbowViewModel
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compose.RainbowTheme

@Composable
fun RainbowScreen(rainbowViewModel: RainbowViewModel) {
    RainbowTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RainbowNavigation(rainbowViewModel = rainbowViewModel)
        }
    }
}
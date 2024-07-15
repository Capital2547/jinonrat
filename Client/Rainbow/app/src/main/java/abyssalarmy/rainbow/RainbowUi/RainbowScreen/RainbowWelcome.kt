import abyssalarmy.rainbow.R
import abyssalarmy.rainbow.RainbowUi.RainbowDestination
import abyssalarmy.rainbow.RainbowUi.RainbowTheme.RainbowFont
import abyssalarmy.rainbow.RainbowViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RainbowWelcome(
    rainbowViewModel: RainbowViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(25.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Welcome to Rainbow",
                fontFamily = RainbowFont,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(
                modifier = Modifier.height(
                    10.dp
                )
            )
            Text(
                text = "Chop down if long seems to be doing the same thing and I don't see any difference in the example code. What is the difference",
                fontFamily = RainbowFont,
                style = MaterialTheme.typography.titleLarge
            )
        }
        Button(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
            rainbowViewModel.navController.navigate(RainbowDestination.Permissions.route)
        }) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.padding(vertical = 6.dp),
                text = "Get Start",
                fontFamily = RainbowFont,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.titleMedium.copy(
                    platformStyle = PlatformTextStyle(includeFontPadding = false)
                ),
            )
            Icon(painter = painterResource(id = R.drawable.arrow_left), contentDescription = null)
        }
    }
}
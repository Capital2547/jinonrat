import abyssalarmy.rainbow.R
import abyssalarmy.rainbow.RainbowUi.RainbowDestination
import abyssalarmy.rainbow.RainbowUi.RainbowTheme.RainbowFont
import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import abyssalarmy.rainbow.RainbowViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RainbowPermissions(rainbowViewModel: RainbowViewModel) {

    val context = LocalContext.current

    var permissionRadio by remember {
        mutableStateOf(false)
    }
    var accessibilityRadio by remember {
        mutableStateOf(false)
    }
    var policyRadio by remember {
        mutableStateOf(false)
    }

    var permissionRadioError by remember {
        mutableStateOf(false)
    }
    var accessibilityRadioError by remember {
        mutableStateOf(false)
    }
    var policyRadioError by remember {
        mutableStateOf(false)
    }

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
                text = "Grant Permissions",
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
            Spacer(
                modifier = Modifier.height(
                    20.dp
                )
            )
            OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = {
                RainbowTools.grantPermissions(context) {
                    permissionRadio = true
                }
            }) {
                Row {
                    RadioButton(selected = permissionRadio, onClick = {
                        RainbowTools.grantPermissions(context) {
                            permissionRadio = true
                        }
                    })
                    Text(
                        modifier = Modifier.padding(vertical = 11.dp),
                        text = "Click to grant necessary permissions",
                        fontFamily = RainbowFont,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            platformStyle =
                            PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                }
            }
            AnimatedVisibility(visible = permissionRadioError) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Grant necessary permissions is require",
                    fontFamily = RainbowFont,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        platformStyle =
                        PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }
            Spacer(
                modifier = Modifier.height(
                    10.dp
                )
            )
            OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = {
                RainbowTools.openAccessibilitySetting(context)
                RainbowTools.checkAccessibilityPermissionRapid(context) {
                    accessibilityRadio = true
                }
            }) {
                Row {
                    RadioButton(selected = accessibilityRadio, onClick = {
                        RainbowTools.openAccessibilitySetting(context)
                        RainbowTools.checkAccessibilityPermissionRapid(context) {
                            accessibilityRadio = true
                        }
                    })
                    Text(
                        modifier = Modifier.padding(vertical = 11.dp),
                        text = "Click to turn on accessibility service",
                        fontFamily = RainbowFont,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            platformStyle =
                            PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                }
            }
            AnimatedVisibility(visible = accessibilityRadioError) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Turn on accessibility service is require",
                    fontFamily = RainbowFont,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        platformStyle =
                        PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }
            Spacer(
                modifier = Modifier.height(
                    10.dp
                )
            )
            OutlinedCard(modifier = Modifier.fillMaxWidth(), onClick = {
                policyRadio = !policyRadio
            }) {
                Row {
                    RadioButton(selected = policyRadio, onClick = { policyRadio = !policyRadio })
                    Text(
                        modifier = Modifier.padding(vertical = 11.dp),
                        text = "Click to agree with our privacy and policy guidelines",
                        fontFamily = RainbowFont,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            platformStyle =
                            PlatformTextStyle(includeFontPadding = false)
                        )
                    )
                }
            }
            AnimatedVisibility(visible = permissionRadioError) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "Agree with our privacy and policy guidelines is require",
                    fontFamily = RainbowFont,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        platformStyle =
                        PlatformTextStyle(includeFontPadding = false)
                    )
                )
            }
        }
        Button(modifier = Modifier.align(Alignment.BottomEnd), onClick = {
            permissionRadioError = !permissionRadio
            accessibilityRadioError = !accessibilityRadio
            policyRadioError = !policyRadio
            if (permissionRadio && accessibilityRadio && policyRadio) {
                RainbowTools.disableWelcomeScreen(context)
                rainbowViewModel.navController.navigate(RainbowDestination.Webview.route)
            }
        }) {
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                modifier = Modifier.padding(vertical = 6.dp),
                text = "Next",
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
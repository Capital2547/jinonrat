import abyssalarmy.rainbow.RainbowUtils.RainbowTools
import abyssalarmy.rainbow.RainbowViewModel
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun RainbowWebview(rainbowViewModel: RainbowViewModel) {
    val data = RainbowTools.rainbowUiData(LocalContext.current)
    var webView: WebView? = null
    AndroidView(
        modifier = Modifier.statusBarsPadding(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true

                loadUrl(data.webviewUrl)
                webView = this
            }
        }, update = {
            webView = it
        }
    )
}
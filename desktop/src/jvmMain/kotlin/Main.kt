import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import me.andr4.common.App
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            App()
        }
    }
}
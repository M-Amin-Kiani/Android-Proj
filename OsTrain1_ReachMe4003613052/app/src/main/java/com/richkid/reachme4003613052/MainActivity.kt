package com.richkid.reachme4003613052

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.lightspark.composeqr.QrCodeView
import com.richkid.reachme4003613052.ui.theme.DarkGradient
import com.richkid.reachme4003613052.ui.theme.ReachMe4003613052Theme
import androidx.compose.ui.platform.LocalConfiguration


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        setContent {
            ReachMe4003613052Theme {
                val statusBarHeight = getStatusBarHeight()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkGradient)
                        .padding(top = statusBarHeight),
                    contentAlignment = Alignment.Center
                ) {
                    StudentCard()
                }
            }
        }
    }

    @Composable
    fun getStatusBarHeight(): Dp {
        val view = LocalView.current
        val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets)
        return with(LocalDensity.current) {
            insets.getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp()
        }
    }
}


@Composable
fun StudentCard() {
    val telegramUrl = "https://t.me/amin_kiani"
    //val context = LocalContext.current
    val containerColor = MaterialTheme.colorScheme.onPrimaryContainer

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        val cardWidth = if (isPortrait) maxWidth * 0.9f else maxWidth * 0.8f
        val cardHeight = if (isPortrait) maxHeight * 0.8f else maxHeight * 0.9f
        val qrSize = cardWidth * 0.7f

        Card(
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = containerColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                ProfileAndQR(telegramUrl, qrSize, isPortrait)
            }
        }
    }
}



@Composable
fun ProfileAndQR(telegramUrl: String, qrSize: Dp, isPortrait: Boolean) {
    //val context = LocalContext.current

    if (isPortrait) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.myphoto4003613052),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(qrSize * 0.6f)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = "4003613052",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(30.dp))

            QRCodeBox(telegramUrl, qrSize)
        }
    } else {

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.myphoto4003613052),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(qrSize * 0.4f)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(15.dp))

                Text(
                    text = "4003613052",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(32.dp))

            QRCodeBox(telegramUrl, qrSize)
        }
    }
}


@Composable
fun QRCodeBox(telegramUrl: String, qrSize: Dp) {
    val context = LocalContext.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(qrSize)
            .clip(RoundedCornerShape(16.dp))
            //.background(MaterialTheme.colorScheme.onPrimaryContainer)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, telegramUrl.toUri())
                context.startActivity(intent)
            }
    ) {
        CustomQRCode(telegramUrl)

        Image(
            painter = painterResource(id = R.drawable.telegram_logo_gray),
            contentDescription = "Telegram Logo",
            modifier = Modifier
                .size(qrSize * 0.3f)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
        )
    }
}


//--------------------------------------------------------
@Composable
fun CustomQRCode(content: String) {
    QrCodeView(
        data = content,
        modifier = Modifier
            .size(225.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewUI() {
    ReachMe4003613052Theme {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            StudentCard()
        }
    }
}

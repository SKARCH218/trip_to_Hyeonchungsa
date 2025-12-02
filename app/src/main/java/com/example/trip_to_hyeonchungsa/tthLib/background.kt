package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource

@Composable
fun SetBackground(
    imageName: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    Box(modifier = modifier.fillMaxSize()) {
        if (resourceId != 0) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "background image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (onClick != null) {
                            Modifier.clickable { onClick() }
                        } else {
                            Modifier
                        }
                    )
            )
        }
        content()
    }
}

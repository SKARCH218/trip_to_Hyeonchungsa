import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.trip_to_hyeonchungsa.R // 패키지명에 맞게 R 클래스 import 필요

import com.example.trip_to_hyeonchungsa.tthLib.Bubble

@Composable
fun QuestDialogueScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. 배경 이미지
        // R.drawable.img_example 부분을 실제 이미지 리소스로 변경해주세요.
        // 이미지가 없으면 임시로 주석 처리하고 배경색을 넣어서 테스트하세요.

        Image(
            painter = painterResource(id = R.drawable.img_example),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Bubble(
            name = "오누이",
            content = "안녕, 우리 함께 \n현충사를 구해보지 않을래?"
        )

    }
}



@Preview(showBackground = true)
@Composable
fun PreviewQuestDialogue() {
    QuestDialogueScreen()
}
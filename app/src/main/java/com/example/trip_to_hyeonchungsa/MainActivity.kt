import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.trip_to_hyeonchungsa.R // 패키지명에 맞게 R 클래스 import 필요

import com.example.trip_to_hyeonchungsa.tthLib.Bubble
import com.example.trip_to_hyeonchungsa.tthLib.SetBackground

@Composable
fun QuestDialogueScreen() {
    SetBackground(imageName = "img_example") {

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
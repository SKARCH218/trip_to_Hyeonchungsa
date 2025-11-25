import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trip_to_hyeonchungsa.R // 패키지명에 맞게 R 클래스 import 필요

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

        GlassDialogueContainer(
            name = "민서", // 스크린샷의 이름
            content = "반가워요!\n이곳은 조선시대 궁궐이에요.\n저와 함께 탐험해볼까요?"
        )
    }
}

@Composable
fun GlassDialogueContainer(
    name: String,
    content: String
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            // 여기가 오류가 났던 부분입니다. 함수 이름을 통일했습니다.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        // 배경을 더 뿌옇게 만들기 위해 불투명도(alpha)를 높임
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.7f), // 위쪽
                                Color.White.copy(alpha = 0.5f)  // 아래쪽
                            )
                        )
                    )
                    .border(
                        border = BorderStroke(1.2.dp, Color.White.copy(alpha = 0.8f)),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    // 2. 이름 칸 (오른쪽 정렬)
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 35.dp, vertical = 8.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3. 내용 칸 (상하 길이 늘림)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 200.dp), // 요청하신대로 높이를 늘렸습니다.
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = content,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewQuestDialogue() {
    QuestDialogueScreen()
}
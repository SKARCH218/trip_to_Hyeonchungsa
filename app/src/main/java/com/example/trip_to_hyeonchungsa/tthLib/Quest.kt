package com.example.trip_to_hyeonchungsa.tthLib

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 퀘스트를 오른쪽 상단에 표시하고, 클릭하면 자세한 내용을 보여주는 함수
 * @param questTitle 퀘스트 제목
 * @param questContent 퀘스트 내용 (간단한 설명)
 * @param questDetailContent 텍스트 클릭 시 나올 자세한 퀘스트 내용
 * @param content 퀘스트 UI 뒤에 표시될 나머지 화면 내용
 */
@Composable
fun QuestDisplay(
    questTitle: String,
    questContent: String,
    questDetailContent: String,
    content: @Composable () -> Unit = {}
) {
    var showDetail by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 메인 콘텐츠
        content()

        // 퀘스트 카드 (왼쪽 상단)
        if (!showDetail) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .width(150.dp)
                    .clickable { showDetail = true },
                shape = RoundedCornerShape(6.dp),
                shadowElevation = 4.dp,
                color = Color.White.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(9.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 퀘스트 제목
                    Text(
                        text = questTitle,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    )

                    // 구분선
                    HorizontalDivider(
                        color = Color.Gray.copy(alpha = 0.5f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 3.dp)
                    )

                    // 퀘스트 내용
                    Text(
                        text = questContent,
                        fontSize = 10.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    )
                }
            }
        }

        // 자세한 퀘스트 내용 전체 화면
        if (showDetail) {
            QuestDetailScreen(
                questTitle = questTitle,
                questDetailContent = questDetailContent,
                onClose = { showDetail = false }
            )
        }
    }
}

/**
 * 퀘스트 자세한 내용을 표시하는 전체 화면
 */
@Composable
private fun QuestDetailScreen(
    questTitle: String,
    questDetailContent: String,
    onClose: () -> Unit
) {
    // 반투명 검은색 배경 (외곽선 바깥쪽)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        // 외곽선 - 크기를 절반으로 줄임
        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.5f)
                .clip(RoundedCornerShape(32.dp))
                .border(
                    border = BorderStroke(2.dp, Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            // 배경 (내부 - 0.9 투명도)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color.White.copy(alpha = 0.9f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 제목과 닫기 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.weight(1f))
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = questTitle,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(onClick = onClose) {
                            Text("닫기")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 자세한 퀘스트 내용
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        Text(
                            text = questDetailContent,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(20.dp)
                        )
                    }
                }
            }
        }
    }
}


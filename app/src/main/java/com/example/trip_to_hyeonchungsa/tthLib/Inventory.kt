package com.example.trip_to_hyeonchungsa.tthLib

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trip_to_hyeonchungsa.data.Item
import com.example.trip_to_hyeonchungsa.data.ItemRepository
import com.example.trip_to_hyeonchungsa.tthLib.context.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 인벤토리 버튼을 표시하고, 버튼을 누르면 인벤토리 화면을 띄우는 함수
 * @param context Context 객체
 * @param showTestButtons 테스트용 버튼 표시 여부 (기본값: false)
 * @param buttonText 버튼에 표시될 텍스트 (기본값: "인벤토리")
 * @param content 인벤토리 버튼 뒤에 표시될 나머지 화면 내용
 */
@Composable
fun InventoryButton(
    context: Context = LocalContext.current,
    showTestButtons: Boolean = false,
    buttonText: String = "인벤토리",
    content: @Composable () -> Unit = {}
) {
    var showInventory by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 메인 콘텐츠
        content()

        // 인벤토리 버튼 (오른쪽 상단에 떠있는 버튼)
        if (!showInventory) {
            FloatingActionButton(
                onClick = { showInventory = true },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "인벤토리"
                    )
                    Text(buttonText)
                }
            }
        }

        // 인벤토리 전체 화면
        if (showInventory) {
            InventoryFullScreen(
                context = context,
                showTestButtons = showTestButtons,
                onClose = { showInventory = false }
            )
        }
    }
}

/**
 * 인벤토리 전체 화면 (닫기 버튼 포함)
 */
@Composable
private fun InventoryFullScreen(
    context: Context,
    showTestButtons: Boolean,
    onClose: () -> Unit
) {
    val repository = remember { ItemRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var ownedItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }


    fun loadAllItems() {
        coroutineScope.launch {
            try {
                val jsonString = context.assets.open("items.json").bufferedReader().use { it.readText() }
                val listType = object : com.google.gson.reflect.TypeToken<List<Item>>() {}.type
                allItems = com.google.gson.Gson().fromJson(jsonString, listType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadOwnedItems() {
        coroutineScope.launch {
            isLoading = true
            try {
                val items = repository.getOwnedItemsWithDetails()
                ownedItems = items
            } catch (e: Exception) {
                message = "아이템을 불러오는데 실패했습니다: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun addItemById(itemId: Int) {
        coroutineScope.launch {
            try {
                repository.addItemToInventory(itemId)
                message = "아이템 추가 완료!"
                loadOwnedItems()
            } catch (e: Exception) {
                message = "아이템 추가 실패: ${e.message}"
            }
        }
    }

    LaunchedEffect(Unit) {
        loadAllItems()
        loadOwnedItems()
    }

    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            message = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        // 반투명 검은색 배경 (외곽선 바깥쪽)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.Black.copy(alpha = 0.5f))  // 반투명 검은색
                .padding(16.dp)
        ) {
            // 외곽선
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(32.dp))
                    .border(
                        border = BorderStroke(2.dp, Color.White.copy(alpha = 0.9f)),
                        shape = RoundedCornerShape(32.dp)
                    )
            ) {
                // Bubble 스타일 배경 (내부 - 0.9 투명도)
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
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 제목과 닫기 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = "인벤토리",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                            )
                        }
                        Button(onClick = onClose) {
                            Text("닫기")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

            if (showTestButtons && allItems.isNotEmpty()) {
                Text(
                    text = "아이템 추가 (테스트용)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // JSON 파일의 아이템 개수만큼 자동으로 버튼 생성
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),  // 3열로 배치
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((allItems.size / 3 + 1) * 56).dp)  // 버튼 높이에 맞게 조정
                        .padding(bottom = 8.dp)
                ) {
                    items(allItems) { item ->
                        Button(
                            onClick = { addItemById(item.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                TextButton(onClick = { loadOwnedItems() }) {
                    Text("새로고침")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                text = "획득한 아이템 (${ownedItems.size}개)",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (ownedItems.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "아직 획득한 아이템이 없습니다.",
                        modifier = Modifier.padding(24.dp),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ownedItems) { item ->
                        InventoryItemCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
                }
            }
                }
            }
        }

        // 선택된 아이템이 있으면 상세 화면을 오버레이로 표시
        if (selectedItem != null) {
            ItemDetailScreen(
                item = selectedItem!!,
                onClose = { selectedItem = null }
            )
        }
    }

/**
 * 아이템 상세 화면 - 인벤토리 화면 위에 오버레이로 표시
 */
@Composable
private fun ItemDetailScreen(
    item: Item,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    // 이미지 리소스 ID 가져오기
    val imageResId = context.resources.getIdentifier(
        item.image,
        "drawable",
        context.packageName
    )

    // 반투명 배경 (외곽선 바깥쪽) - 인벤토리와 동일한 구조
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))  // 반투명 검은색 배경
            .clickable(onClick = onClose)  // 어디를 터치해도 닫기
            .padding(16.dp)
    ) {
        // 외곽선
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(32.dp))
                .border(
                    border = BorderStroke(2.dp, Color.White.copy(alpha = 0.9f)),
                    shape = RoundedCornerShape(32.dp)
                )
        ) {
            // Bubble 스타일 배경 (내부)
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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 아이템 이름
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White,
                            shadowElevation = 4.dp
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 35.dp, vertical = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 이미지 영역
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageResId != 0) {
                                Image(
                                    painter = painterResource(id = imageResId),
                                    contentDescription = item.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Fit
                                )
                            } else {
                                Text(
                                    text = "🖼️",
                                    fontSize = 80.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Lore 텍스트
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.lore,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                lineHeight = 26.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 닫기 안내
                    Text(
                        text = "화면을 터치하여 닫기",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

/**
 * 인벤토리 UI를 띄우는 함수
 * @param context Context 객체
 * @param showTestButtons 테스트용 버튼 표시 여부 (기본값: false)
 */
@Composable
fun Inventory(
    context: Context,
    showTestButtons: Boolean = false
) {
    val repository = remember { ItemRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    var ownedItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var allItems by remember { mutableStateOf<List<Item>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 선택된 아이템이 있으면 상세 화면 표시
    if (selectedItem != null) {
        ItemDetailScreen(
            item = selectedItem!!,
            onClose = { selectedItem = null }
        )
        return
    }

    // 전체 아이템 목록 로드 (JSON에서)
    fun loadAllItems() {
        coroutineScope.launch {
            try {
                val jsonString = context.assets.open("items.json").bufferedReader().use { it.readText() }
                val listType = object : com.google.gson.reflect.TypeToken<List<Item>>() {}.type
                allItems = com.google.gson.Gson().fromJson(jsonString, listType)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 아이템 로드 함수
    fun loadOwnedItems() {
        coroutineScope.launch {
            isLoading = true
            try {
                val items = repository.getOwnedItemsWithDetails()
                ownedItems = items
            } catch (e: Exception) {
                message = "아이템을 불러오는데 실패했습니다: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // 아이템 추가 함수
    fun addItemById(itemId: Int) {
        coroutineScope.launch {
            try {
                repository.addItemToInventory(itemId)
                message = "아이템 추가 완료!"
                loadOwnedItems()
            } catch (e: Exception) {
                message = "아이템 추가 실패: ${e.message}"
            }
        }
    }

    // 초기 로드
    LaunchedEffect(Unit) {
        loadAllItems()
        loadOwnedItems()
    }

    // 메시지 처리
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            message = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 제목
            Text(
                text = "인벤토리",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // 테스트 버튼들 (옵션)
            if (showTestButtons && allItems.isNotEmpty()) {
                Text(
                    text = "아이템 추가 (테스트용)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // JSON 파일의 아이템 개수만큼 자동으로 버튼 생성
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),  // 3열로 배치
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(((allItems.size / 3 + 1) * 56).dp)  // 버튼 높이에 맞게 조정
                        .padding(bottom = 8.dp)
                ) {
                    items(allItems) { item ->
                        Button(
                            onClick = { addItemById(item.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text(
                                text = item.name,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                TextButton(onClick = { loadOwnedItems() }) {
                    Text("새로고침")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 획득한 아이템 목록
            Text(
                text = "획득한 아이템 (${ownedItems.size}개)",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (isLoading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (ownedItems.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    shape = RoundedCornerShape(22.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Text(
                        text = "아직 획득한 아이템이 없습니다.",
                        modifier = Modifier.padding(24.dp),
                        fontSize = 16.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ownedItems) { item ->
                        InventoryItemCard(
                            item = item,
                            onClick = { selectedItem = item }
                        )
                    }
                }
            }
        }

        // 선택된 아이템이 있으면 상세 화면을 오버레이로 표시
        if (selectedItem != null) {
            ItemDetailScreen(
                item = selectedItem!!,
                onClose = { selectedItem = null }
            )
        }
    }
}

@Composable
private fun InventoryItemCard(
    item: Item,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // 이미지 리소스 ID 가져오기
    val imageResId = context.resources.getIdentifier(
        item.image,
        "drawable",
        context.packageName
    )

    // Bubble 스타일 카드
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.9f),  // 더 불투명하게
                        Color.White.copy(alpha = 0.8f)   // 더 불투명하게
                    )
                )
            )
            .border(
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 이미지 영역
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(14.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier.padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (imageResId != 0) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = item.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(
                            text = "🖼️",
                            fontSize = 40.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 아이템 이름
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Text(
                    text = item.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 인벤토리 관리 객체 (코루틴 스코프 불필요) ⭐ 가장 간편함!
 *
 * Context와 코루틴 스코프 없이 바로 사용 가능합니다.
 * 내부적으로 자동으로 비동기 처리됩니다.
 *
 * 사용 예시:
 * ```
 * // 코루틴 없이 바로 사용!
 * InventoryManager.add(1)
 * InventoryManager.remove(1)
 * InventoryManager.clear()
 *
 * // 아이템 목록 조회 (콜백 사용)
 * InventoryManager.getAll { items ->
 *     println("획득한 아이템: ${items.size}개")
 * }
 * ```
 */
object InventoryManager {
    private fun getRepository() = ItemRepository(App.getContext())

    /**
     * 아이템 추가
     * @param itemId 추가할 아이템의 ID
     * @param onComplete 완료 시 실행할 콜백 (선택사항)
     */
    fun add(itemId: Int, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getRepository().addItemToInventory(itemId)
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 아이템 제거
     * @param itemId 제거할 아이템의 ID
     * @param onComplete 완료 시 실행할 콜백 (선택사항)
     */
    fun remove(itemId: Int, onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getRepository().removeItemFromInventory(itemId)
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 전체 아이템 제거
     * @param onComplete 완료 시 실행할 콜백 (선택사항)
     */
    fun clear(onComplete: (() -> Unit)? = null) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getRepository().clearInventory()
                onComplete?.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 아이템 목록 조회
     * @param callback 아이템 목록을 받을 콜백
     */
    fun getAll(callback: (List<Item>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val items = getRepository().getOwnedItemsWithDetails()
                CoroutineScope(Dispatchers.Main).launch {
                    callback(items)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                CoroutineScope(Dispatchers.Main).launch {
                    callback(emptyList())
                }
            }
        }
    }
}

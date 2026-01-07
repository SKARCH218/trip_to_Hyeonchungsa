package com.example.trip_to_hyeonchungsa.tthLib

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.ar.core.*
import com.google.ar.core.exceptions.CameraNotAvailableException
import java.io.IOException
import kotlinx.coroutines.delay
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.example.trip_to_hyeonchungsa.tthLib.rendering.DisplayRotationHelper
import com.example.trip_to_hyeonchungsa.tthLib.rendering.ObjectRenderer
import com.example.trip_to_hyeonchungsa.tthLib.rendering.BackgroundRenderer
import com.example.trip_to_hyeonchungsa.tthLib.rendering.ImageRenderer

/**
 * AR 기능을 수행하는 Composable 함수
 * @param imageName 인식할 이미지 이름 (확장자 제외)
 * @param modelPath 렌더링할 모델 파일 경로 (assets 기준)
 * @param scale 모델 크기 배율 (1.0 = 원본 크기)
 * @param modifier Compose Modifier
 * @param onModelClick 모델 클릭 시 호출되는 콜백 (imageName 전달)
 * @param showDebugInfo 하단에 디버그 메시지 표시 여부
 * @param autoStart 자동으로 AR 세션 시작 (false인 경우 수동 시작 필요)
 */
@Composable
fun AugmentedImageArView(
    imageName: String,
    modelPath: String,
    scale: Float = 1.0f,
    modifier: Modifier = Modifier,
    onModelClick: ((String) -> Unit)? = null,
    showDebugInfo: Boolean = false,
    autoStart: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val message = remember { mutableStateOf("AR 기능을 준비 중입니다...") }

    // 내부에서 Map으로 변환
    val imageToModelMap = remember(imageName, modelPath, scale) {
        val map = mapOf(imageName to Pair(modelPath, scale))
        Log.d("ARFunction", "imageToModelMap 생성: $map")
        map
    }

    // ARCore 세션과 GLSurfaceView 인스턴스를 remember로 관리
    val arCoreSession = remember { mutableStateOf<Session?>(null) }
    val glSurfaceView = remember { GLSurfaceView(context) }
    val trackedImages = remember { mutableStateMapOf<Int, Pair<AugmentedImage, Anchor>>() }

    // 전역 변수에 세션 정보 동기화
    LaunchedEffect(arCoreSession.value) {
        globalArSession = arCoreSession.value
    }

    // trackedImages 참조를 전역 변수에 저장
    LaunchedEffect(Unit) {
        globalTrackedImagesRef = trackedImages
    }

    // Lifecycle에 따라 자동으로 세션 관리
    DisposableEffect(Unit) {
        onDispose {
            // Composable이 제거될 때 자동으로 리소스 정리
            trackedImages.values.forEach { it.second.detach() }
            trackedImages.clear()
            globalTrackedImagesRef = null
        }
    }

    // Rendering objects
    val displayRotationHelper = remember { DisplayRotationHelper(context) }
    val objectRenderer = remember { ObjectRenderer() }
    val backgroundRenderer = remember { BackgroundRenderer() }
    val cameraTextureId = remember { IntArray(1) }

    // 터치 이벤트 처리
    val lastTapTime = remember { mutableStateOf(0L) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                message.value = "카메라 권한이 허용되었습니다. AR 세션을 시작합니다."
            } else {
                message.value = "AR 기능을 사용하려면 카메라 권한이 필요합니다."
            }
        }
    )

    // AR 기능 설정 및 해제
    fun setupAR() {
        try {
            if (arCoreSession.value == null) {
                Log.d("ARFunction", "🚀 AR 세션 초기화 시작...")

                val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> {
                        Log.d("ARFunction", "✅ ARCore 설치 확인됨")
                        Session(context)
                    }
                    else -> {
                        Log.e("ARFunction", "❌ ARCore 설치 필요")
                        message.value = "ARCore 설치가 필요합니다."
                        return
                    }
                }
                arCoreSession.value = session
                Log.d("ARFunction", "✅ ARCore 세션 생성 완료")

                val config = Config(session)
                configureArSession(config)  // 최적화된 설정 적용

                if (!setupAugmentedImageDatabase(context, config, session)) {
                    Log.e("ARFunction", "❌ 이미지 데이터베이스 설정 실패")
                    message.value = "증강 이미지 데이터베이스를 설정할 수 없습니다."
                    return
                }

                session.configure(config)
                Log.d("ARFunction", "✅ AR 세션 설정 완료")
                message.value = "AR 세션 준비 완료 - 이미지를 비춰주세요"
            }
        } catch (e: Exception) {
            message.value = "AR 설정 중 오류 발생: ${e.message}"
            Log.e("ARFunction", "❌ AR 설정 중 예외 발생", e)
        }
    }

    // Lifecycle 이벤트 관찰
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        setupAR()
                        glSurfaceView.onResume()
                        arCoreSession.value?.resume()
                        displayRotationHelper.onResume()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    arCoreSession.value?.pause()
                    glSurfaceView.onPause()
                    displayRotationHelper.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    arCoreSession.value?.close()
                    arCoreSession.value = null
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 화면 UI
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                glSurfaceView.apply {
                    preserveEGLContextOnPause = true
                    setEGLContextClientVersion(2)
                    setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                    // 터치 이벤트 리스너
                    setOnTouchListener { _, event ->
                        if (event.action == android.view.MotionEvent.ACTION_UP) {
                            lastTapTime.value = System.currentTimeMillis()
                        }
                        true
                    }

                    setRenderer(object : GLSurfaceView.Renderer {
                        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                            GLES20.glGenTextures(1, cameraTextureId, 0)
                            try {
                                backgroundRenderer.createOnGlThread(context, cameraTextureId[0])
                                // modelPath 파라미터 사용 (하드코딩 제거)
                                objectRenderer.createOnGlThread(context, modelPath)
                                objectRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f)
                                Log.d("ARFunction", "모델 로드 성공: $modelPath")
                            } catch (e: IOException) {
                                Log.e("ARFunction", "Failed to read obj file: $modelPath", e)
                                message.value = "모델 파일 로드 실패: $modelPath"
                            }
                        }
                        var surfaceWidth = 0
                        var surfaceHeight = 0

                        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                            surfaceWidth = width
                            surfaceHeight = height
                            displayRotationHelper.onSurfaceChanged(width, height)
                            GLES20.glViewport(0, 0, width, height)
                        }
                        override fun onDrawFrame(gl: GL10?) {
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                            val session = arCoreSession.value
                            if (session == null) {
                                // 세션이 종료되었으면 렌더링 중단
                                return
                            }
                            displayRotationHelper.updateSessionIfNeeded(session)

                            try {
                                session.setCameraTextureName(cameraTextureId[0])
                                val frame = session.update()
                                handleFrame(frame, imageToModelMap, trackedImages, message)

                                // 터치 이벤트 처리 (화면 중앙에서 hit test)
                                val tapTime = lastTapTime.value
                                if (tapTime > 0 && System.currentTimeMillis() - tapTime < 200) {
                                    lastTapTime.value = 0
                                    val hits = frame.hitTest(surfaceWidth / 2f, surfaceHeight / 2f)
                                    for (hit in hits) {
                                        val trackable = hit.trackable
                                        if (trackable is AugmentedImage && trackable.trackingState == TrackingState.TRACKING) {
                                            onModelClick?.invoke(trackable.name)
                                            message.value = "'${trackable.name}' 모델 클릭!"
                                            break
                                        }
                                    }
                                }

                                // Draw camera background
                                backgroundRenderer.draw(frame)

                                // Get camera matrices.
                                val camera = frame.camera
                                val projectionMatrix = FloatArray(16)
                                camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f)
                                val viewMatrix = FloatArray(16)
                                camera.getViewMatrix(viewMatrix, 0)

                                // Draw the object for each tracked image
                                Log.d("ARFunction", "렌더링 시도 - trackedImages 크기: ${trackedImages.size}")
                                for ((index, pair) in trackedImages) {
                                    val (image, anchor) = pair
                                    Log.d("ARFunction", "이미지 [${index}] ${image.name} - 상태: ${image.trackingState}, 방법: ${image.trackingMethod}")

                                    // 추적 중이고 추적 방법이 FULL_TRACKING인 경우에만 렌더링
                                    if (image.trackingState == TrackingState.TRACKING &&
                                        image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                                        val modelData = imageToModelMap[image.name]
                                        Log.d("ARFunction", "모델 데이터 검색: ${image.name} -> $modelData")

                                        if (modelData != null) {
                                            val (_, modelScale) = modelData
                                            val modelMatrix = FloatArray(16)
                                            anchor.pose.toMatrix(modelMatrix, 0)
                                            objectRenderer.updateModelMatrix(modelMatrix, modelScale)
                                            objectRenderer.draw(viewMatrix, projectionMatrix, null, floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f))
                                            Log.d("ARFunction", "✅ 모델 렌더링 완료: ${image.name}, 스케일: $modelScale")
                                        } else {
                                            Log.e("ARFunction", "❌ 모델 데이터 없음: ${image.name}")
                                            Log.e("ARFunction", "❌ imageToModelMap 내용: $imageToModelMap")
                                        }
                                    } else {
                                        Log.d("ARFunction", "⚠️ 렌더링 조건 미충족 - 상태: ${image.trackingState}, 방법: ${image.trackingMethod}")
                                    }
                                }

                            } catch (e: CameraNotAvailableException) {
                                Log.e("ARFunction", "Camera not available", e)
                            }
                        }
                    })
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (showDebugInfo) {
            Text(
                text = message.value,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

/**
 * AR 기능으로 이미지를 인식하면 2D 이미지를 표시하는 Composable 함수
 * @param imageName 인식할 이미지 이름 (확장자 제외)
 * @param overlayImageName 표시할 drawable 이미지 이름 (확장자 제외, 예: "my_image")
 * @param scale 이미지 크기 배율 (1.0 = 원본 크기)
 * @param modifier Compose Modifier
 * @param onImageClick 이미지 클릭 시 호출되는 콜백 (imageName 전달)
 * @param showDebugInfo 하단에 디버그 메시지 표시 여부
 * @param autoStart 자동으로 AR 세션 시작 (false인 경우 수동 시작 필요)
 */
@Composable
fun AugmentedImageWith2DImage(
    imageName: String,
    overlayImageName: String,
    scale: Float = 1.0f,
    modifier: Modifier = Modifier,
    onImageClick: ((String) -> Unit)? = null,
    showDebugInfo: Boolean = false,
    autoStart: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val message = remember { mutableStateOf("AR 기능을 준비 중입니다...") }

    // 이미지 이름으로 drawable 리소스 ID 가져오기
    val drawableResId = remember(overlayImageName) {
        val resourceId = context.resources.getIdentifier(overlayImageName, "drawable", context.packageName)
        if (resourceId == 0) {
            Log.e("ARFunction2D", "drawable 리소스를 찾을 수 없습니다: $overlayImageName")
        } else {
            Log.d("ARFunction2D", "drawable 리소스 찾음: $overlayImageName -> $resourceId")
        }
        resourceId
    }

    // 내부에서 Map으로 변환
    val imageToDrawableMap = remember(imageName, drawableResId, scale) {
        val map = mapOf(imageName to Pair(drawableResId, scale))
        Log.d("ARFunction2D", "imageToDrawableMap 생성: imageName=$imageName, drawableResId=$drawableResId, scale=$scale")
        map
    }

    // ARCore 세션과 GLSurfaceView 인스턴스를 remember로 관리
    val arCoreSession = remember { mutableStateOf<Session?>(null) }
    val glSurfaceView = remember { GLSurfaceView(context) }
    val trackedImages = remember { mutableStateMapOf<Int, Pair<AugmentedImage, Anchor>>() }

    // 전역 변수에 세션 정보 동기화
    LaunchedEffect(arCoreSession.value) {
        globalArSession = arCoreSession.value
    }

    // trackedImages 참조를 전역 변수에 저장
    LaunchedEffect(Unit) {
        globalTrackedImagesRef = trackedImages
    }

    // Lifecycle에 따라 자동으로 세션 관리
    DisposableEffect(Unit) {
        onDispose {
            // Composable이 제거될 때 자동으로 리소스 정리
            trackedImages.values.forEach { it.second.detach() }
            trackedImages.clear()
            globalTrackedImagesRef = null
        }
    }

    // Rendering objects
    val displayRotationHelper = remember { DisplayRotationHelper(context) }
    val imageRenderer = remember { ImageRenderer() }
    val backgroundRenderer = remember { BackgroundRenderer() }
    val cameraTextureId = remember { IntArray(1) }

    // 터치 이벤트 처리
    val lastTapTime = remember { mutableStateOf(0L) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                message.value = "카메라 권한이 허용되었습니다. AR 세션을 시작합니다."
            } else {
                message.value = "AR 기능을 사용하려면 카메라 권한이 필요합니다."
            }
        }
    )

    // AR 기능 설정 및 해제
    fun setupAR() {
        try {
            if (arCoreSession.value == null) {
                val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> Session(context)
                    else -> {
                        message.value = "ARCore 설치가 필요합니다."
                        return
                    }
                }
                arCoreSession.value = session

                val config = Config(session)
                configureArSession(config)  // 최적화된 설정 적용
                if (!setupAugmentedImageDatabase(context, config, session)) {
                    message.value = "증강 이미지 데이터베이스를 설정할 수 없습니다."
                    return
                }
                session.configure(config)
            }
        } catch (e: Exception) {
            message.value = "AR 설정 중 오류 발생: ${e.message}"
            Log.e("ARFunction2D", "Error setting up AR", e)
        }
    }

    // Lifecycle 이벤트 관찰
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        setupAR()
                        glSurfaceView.onResume()
                        arCoreSession.value?.resume()
                        displayRotationHelper.onResume()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    arCoreSession.value?.pause()
                    glSurfaceView.onPause()
                    displayRotationHelper.onPause()
                }
                Lifecycle.Event.ON_DESTROY -> {
                    arCoreSession.value?.close()
                    arCoreSession.value = null
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 화면 UI
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                glSurfaceView.apply {
                    preserveEGLContextOnPause = true
                    setEGLContextClientVersion(2)
                    setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                    // 터치 이벤트 리스너
                    setOnTouchListener { _, event ->
                        if (event.action == android.view.MotionEvent.ACTION_UP) {
                            lastTapTime.value = System.currentTimeMillis()
                        }
                        true
                    }

                    setRenderer(object : GLSurfaceView.Renderer {
                        override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                            GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                            GLES20.glGenTextures(1, cameraTextureId, 0)
                            try {
                                backgroundRenderer.createOnGlThread(context, cameraTextureId[0])
                                // drawable 리소스로 이미지 렌더러 초기화
                                imageRenderer.createOnGlThread(context, drawableResId)
                                Log.d("ARFunction2D", "2D 이미지 로드 성공: drawableResId=$drawableResId")
                            } catch (e: IOException) {
                                Log.e("ARFunction2D", "Failed to load image: $drawableResId", e)
                                message.value = "이미지 파일 로드 실패: $drawableResId"
                            }
                        }
                        var surfaceWidth = 0
                        var surfaceHeight = 0

                        override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                            surfaceWidth = width
                            surfaceHeight = height
                            displayRotationHelper.onSurfaceChanged(width, height)
                            GLES20.glViewport(0, 0, width, height)
                        }
                        override fun onDrawFrame(gl: GL10?) {
                            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                            val session = arCoreSession.value
                            if (session == null) {
                                // 세션이 종료되었으면 렌더링 중단
                                return
                            }
                            displayRotationHelper.updateSessionIfNeeded(session)

                            try {
                                session.setCameraTextureName(cameraTextureId[0])
                                val frame = session.update()
                                handleFrame(frame, imageToDrawableMap.mapValues { (_, pair) ->
                                    Pair("dummy", pair.second) // handleFrame은 String을 기대하므로 더미 값 전달
                                }, trackedImages, message)

                                // 터치 이벤트 처리 (화면 중앙에서 hit test)
                                val tapTime = lastTapTime.value
                                if (tapTime > 0 && System.currentTimeMillis() - tapTime < 200) {
                                    lastTapTime.value = 0
                                    val hits = frame.hitTest(surfaceWidth / 2f, surfaceHeight / 2f)
                                    for (hit in hits) {
                                        val trackable = hit.trackable
                                        if (trackable is AugmentedImage && trackable.trackingState == TrackingState.TRACKING) {
                                            onImageClick?.invoke(trackable.name)
                                            message.value = "'${trackable.name}' 이미지 클릭!"
                                            break
                                        }
                                    }
                                }

                                // Draw camera background
                                backgroundRenderer.draw(frame)

                                // Get camera matrices.
                                val camera = frame.camera
                                val projectionMatrix = FloatArray(16)
                                camera.getProjectionMatrix(projectionMatrix, 0, 0.1f, 100.0f)
                                val viewMatrix = FloatArray(16)
                                camera.getViewMatrix(viewMatrix, 0)

                                // Draw the 2D image for each tracked image
                                Log.d("ARFunction2D", "렌더링 시도 - trackedImages 크기: ${trackedImages.size}")
                                for ((index, pair) in trackedImages) {
                                    val (image, anchor) = pair
                                    Log.d("ARFunction2D", "이미지 [${index}] ${image.name} - 상태: ${image.trackingState}, 방법: ${image.trackingMethod}")

                                    // 추적 중이고 추적 방법이 FULL_TRACKING인 경우에만 렌더링
                                    if (image.trackingState == TrackingState.TRACKING &&
                                        image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                                        val imageData = imageToDrawableMap[image.name]
                                        Log.d("ARFunction2D", "이미지 데이터 검색: ${image.name} -> $imageData")

                                        if (imageData != null) {
                                            val (_, imageScale) = imageData
                                            val modelMatrix = FloatArray(16)
                                            anchor.pose.toMatrix(modelMatrix, 0)
                                            imageRenderer.updateModelMatrix(modelMatrix, imageScale)
                                            imageRenderer.draw(viewMatrix, projectionMatrix)
                                            Log.d("ARFunction2D", "✅ 2D 이미지 렌더링 완료: ${image.name}, 스케일: $imageScale")
                                        } else {
                                            Log.e("ARFunction2D", "❌ 이미지 데이터 없음: ${image.name}")
                                            Log.e("ARFunction2D", "❌ imageToDrawableMap 내용: $imageToDrawableMap")
                                        }
                                    } else {
                                        Log.d("ARFunction2D", "⚠️ 렌더링 조건 미충족 - 상태: ${image.trackingState}, 방법: ${image.trackingMethod}")
                                    }
                                }

                            } catch (e: CameraNotAvailableException) {
                                Log.e("ARFunction2D", "Camera not available", e)
                            }
                        }
                    })
                    renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (showDebugInfo) {
            Text(
                text = message.value,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

private fun setupAugmentedImageDatabase(context: Context, config: Config, session: Session): Boolean {
    try {
        context.assets.open("augmented_images/augmented_image_database.imgdb").use { `is` ->
            val augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, `is`)
            config.augmentedImageDatabase = augmentedImageDatabase

            Log.d("ARFunction", "=" .repeat(50))
            Log.d("ARFunction", "✅ 이미지 데이터베이스 로드 성공!")
            Log.d("ARFunction", "📊 등록된 이미지 개수: ${augmentedImageDatabase.numImages}")

            // 이미지 목록 파일 읽기
            try {
                context.assets.open("augmented_images/augmented_image_database.imgdb-imglist.txt").use { listStream ->
                    val imageList = listStream.bufferedReader().readLines()
                    Log.d("ARFunction", "📋 이미지 목록:")
                    imageList.forEachIndexed { index, line ->
                        val imageName = line.split("|").firstOrNull() ?: "Unknown"
                        Log.d("ARFunction", "  [$index] $imageName")
                    }
                }
            } catch (e: Exception) {
                Log.w("ARFunction", "이미지 목록 파일 읽기 실패 (무시 가능)", e)
            }

            Log.d("ARFunction", "=" .repeat(50))
            return true
        }
    } catch (e: IOException) {
        Log.e("ARFunction", "❌ 이미지 데이터베이스 로드 실패", e)
    }
    return false
}

private fun handleFrame(
    frame: Frame,
    imageToModelMap: Map<String, Pair<String, Float>>,
    trackedImages: MutableMap<Int, Pair<AugmentedImage, Anchor>>,
    message: MutableState<String>
) {
    val camera = frame.camera

    // 카메라 추적 상태 체크 (PAUSED여도 이미지 인식은 시도)
    if (camera.trackingState == TrackingState.STOPPED) {
        Log.e("ARFunction", "❌ 카메라 추적 완전 중지됨")
        message.value = "카메라 추적 중지 - AR 세션을 재시작해주세요"
        return
    }

    if (camera.trackingState == TrackingState.PAUSED) {
        // 추적 실패 이유 확인
        val trackingFailureReason = camera.trackingFailureReason
        val detailReason = when (trackingFailureReason) {
            TrackingFailureReason.NONE -> "초기화 중 (천천히 주변을 둘러보세요)"
            TrackingFailureReason.BAD_STATE -> "잘못된 상태"
            TrackingFailureReason.INSUFFICIENT_LIGHT -> "조명 부족 - 밝은 곳으로 이동"
            TrackingFailureReason.EXCESSIVE_MOTION -> "과도한 움직임 - 천천히 움직이기"
            TrackingFailureReason.INSUFFICIENT_FEATURES -> "특징점 부족 - 텍스처가 있는 물체 비추기"
            TrackingFailureReason.CAMERA_UNAVAILABLE -> "카메라 사용 불가"
            else -> "알 수 없는 이유"
        }

        Log.w("ARFunction", "⚠️ 카메라 PAUSED (이유: $detailReason)")
        message.value = "⚠️ $detailReason"
        // PAUSED 상태여도 이미지 인식은 시도합니다
    } else {
        Log.d("ARFunction", "✅ 카메라 정상 추적 중")
        // 정상 추적 중일 때만 메시지 업데이트 (이미 인식된 경우는 유지)
        if (trackedImages.isEmpty() && !message.value.contains("인식 완료")) {
            message.value = "카메라를 이미지에 비춰주세요"
        }
    }

    val updatedTrackables = frame.getUpdatedTrackables(AugmentedImage::class.java)
    Log.d("ARFunction", "🔍 업데이트된 이미지 개수: ${updatedTrackables.size}")

    // 추적 중인 이미지 처리
    for (image in updatedTrackables) {
        val trackingMethodStr = when (image.trackingMethod) {
            AugmentedImage.TrackingMethod.FULL_TRACKING -> "FULL_TRACKING"
            AugmentedImage.TrackingMethod.LAST_KNOWN_POSE -> "LAST_KNOWN_POSE"
            AugmentedImage.TrackingMethod.NOT_TRACKING -> "NOT_TRACKING"
            else -> "UNKNOWN"
        }

        Log.d("ARFunction", "📸 이미지: ${image.name}, 상태: ${image.trackingState}, 방법: $trackingMethodStr, 인덱스: ${image.index}")

        when (image.trackingState) {
            TrackingState.TRACKING -> {
                // TRACKING 상태이면 모델/이미지 표시 (trackingMethod와 무관)
                if (!trackedImages.containsKey(image.index)) {
                    if (isTrackingEnabled) {
                        val anchor = image.createAnchor(image.centerPose)
                        trackedImages[image.index] = image to anchor
                        message.value = "✅ '${image.name}' 이미지 인식 완료!"
                        Log.d("ARFunction", "✅ 새 이미지 추적 시작: ${image.name} ($trackingMethodStr)")
                    }
                } else {
                    // 기존 앵커 업데이트
                    trackedImages[image.index] = image to trackedImages[image.index]!!.second
                    Log.d("ARFunction", "🔄 기존 이미지 업데이트: ${image.name}")
                }
            }
            TrackingState.PAUSED -> {
                // 추적이 일시 중지되면 제거 (사진이 보이지 않음)
                trackedImages.remove(image.index)?.second?.detach()
                message.value = "⏸️ 이미지 추적 중단 - 카메라를 천천히 움직여주세요"
                Log.w("ARFunction", "⏸️ 이미지 PAUSED: ${image.name}, 방법: $trackingMethodStr")
            }
            TrackingState.STOPPED -> {
                // 추적이 완전히 중지되면 제거
                trackedImages.remove(image.index)?.second?.detach()
                Log.d("ARFunction", "이미지 추적 중지: ${image.name}")
            }
        }
    }

    Log.d("ARFunction", "현재 추적 중인 이미지 개수: ${trackedImages.size}")
}

// AR 세션 관리를 위한 전역 변수
private var globalArSession: Session? = null
private var globalTrackedImagesRef: MutableMap<Int, Pair<AugmentedImage, Anchor>>? = null

/**
 * ARCore Config를 최적화 설정하는 헬퍼 함수
 * 추적 성능을 향상시키기 위한 설정들을 적용합니다.
 */
private fun configureArSession(config: Config) {
    config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE

    // Focus Mode 설정 - 자동 초점 활성화 (추적 성능 향상)
    config.focusMode = Config.FocusMode.AUTO

    // Plane Detection 비활성화 - 이미지 추적만 사용하므로 불필요한 평면 감지 비활성화 (빠른 초기화)
    config.planeFindingMode = Config.PlaneFindingMode.DISABLED

    // Light Estimation 비활성화 - 이미지 추적에는 불필요, 초기화 속도 향상
    config.lightEstimationMode = Config.LightEstimationMode.DISABLED

    // Instant Placement 비활성화 (이미지 추적에는 필요 없음)
    config.instantPlacementMode = Config.InstantPlacementMode.DISABLED

    // Depth 비활성화 - 이미지 추적에는 불필요
    config.depthMode = Config.DepthMode.DISABLED

    Log.d("ARConfig", "✅ ARCore 설정 완료: FocusMode=AUTO, PlaneFinding=DISABLED (이미지 추적 전용 최적화)")
}

/**
 * AR 세션을 완전히 종료하고 모든 리소스를 해제합니다.
 */
fun stopARSession() {
    Log.d("ARFunction", "stopARSession 호출됨")
    // 모델 먼저 제거
    globalTrackedImagesRef?.values?.forEach { it.second.detach() }
    globalTrackedImagesRef?.clear()
    // 세션 종료
    globalArSession?.pause()
    globalArSession?.close()
    globalArSession = null
    // 인식 설정 초기화
    isTrackingEnabled = true
    Log.d("ARFunction", "AR 세션 종료 완료 (인식 설정 초기화됨)")
}

/**
 * AR 세션을 재시작합니다. (초기화가 오래 걸릴 때 사용)
 * 기존 세션을 완전히 종료하고 새로 시작합니다.
 *
 * @param context Context
 * @return 재시작 성공 여부
 */
fun restartARSession(context: Context): Boolean {
    try {
        Log.d("ARFunction", "🔄 AR 세션 재시작 시작...")

        // 1. 기존 세션 완전히 종료
        globalTrackedImagesRef?.values?.forEach { it.second.detach() }
        globalTrackedImagesRef?.clear()
        globalArSession?.pause()
        globalArSession?.close()
        globalArSession = null

        // 2. 잠시 대기 (리소스 해제 시간)
        Thread.sleep(500)

        // 3. 새 세션 생성
        val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
            ArCoreApk.InstallStatus.INSTALLED -> {
                Session(context)
            }
            else -> {
                Log.e("ARFunction", "❌ ARCore 설치 필요")
                return false
            }
        }

        val config = Config(session)
        configureArSession(config)

        if (!setupAugmentedImageDatabase(context, config, session)) {
            Log.e("ARFunction", "❌ 이미지 데이터베이스 설정 실패")
            session.close()
            return false
        }

        session.configure(config)
        session.resume()
        globalArSession = session

        Log.d("ARFunction", "✅ AR 세션 재시작 완료")
        return true

    } catch (e: Exception) {
        Log.e("ARFunction", "❌ AR 세션 재시작 실패", e)
        return false
    }
}

/**
 * 현재 표시된 모든 3D 모델을 제거합니다. (AR 세션은 유지)
 */
fun clearAllModels() {
    Log.d("ARFunction", "clearAllModels 호출됨. 모델 개수: ${globalTrackedImagesRef?.size ?: 0}")
    globalTrackedImagesRef?.values?.forEach { it.second.detach() }
    globalTrackedImagesRef?.clear()
    Log.d("ARFunction", "모델 제거 완료")
    pauseImageTracking()
}

// 이미지 인식 제어용 전역 변수
private var isTrackingEnabled = true

/**
 * 새로운 이미지 인식을 중단합니다. (기존 모델은 유지)
 */
fun pauseImageTracking() {
    isTrackingEnabled = false
    Log.d("ARFunction", "이미지 인식 중단됨")
}

/**
 * 이미지 인식을 재개합니다.
 */
fun resumeImageTracking() {
    isTrackingEnabled = true
    Log.d("ARFunction", "이미지 인식 재개됨")
}


/**
 * 특정 이미지를 카메라로 인식하면 true를 반환하는 Composable 함수
 *
 * @param imageName 인식할 이미지 이름 (확장자 제외, 데이터베이스에 등록된 이름)
 * @return State<Boolean?> - null(검색 중), true(찾음)
 *
 * **이미지 데이터베이스 위치**: `app/src/main/assets/augmented_images/`
 * - 이미지는 `augmented_image_database.imgdb`에 등록되어 있어야 합니다.
 * - 등록된 이미지 목록은 `augmented_image_database.imgdb-imglist.txt`에서 확인할 수 있습니다.
 *

 * **주의사항**:
 * - 이 함수는 카메라를 사용하므로 카메라 권한이 필요합니다.
 * - 이미지가 인식되면 자동으로 AR 세션이 종료됩니다.
 * - Compose UI 내에서만 사용할 수 있습니다 (@Composable 함수 내에서 호출).
 */
@Composable
fun ImageSensing(
    imageName: String
): State<Boolean?> {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val result = remember { mutableStateOf<Boolean?>(null) }
    val message = remember { mutableStateOf("'$imageName' 이미지를 찾는 중...") }

    // ARCore 세션과 GLSurfaceView 인스턴스를 remember로 관리
    val arCoreSession = remember { mutableStateOf<Session?>(null) }
    val glSurfaceView = remember { GLSurfaceView(context) }
    val isDetected = remember { mutableStateOf(false) }

    // Rendering objects
    val displayRotationHelper = remember { DisplayRotationHelper(context) }
    val backgroundRenderer = remember { BackgroundRenderer() }
    val cameraTextureId = remember { IntArray(1) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                message.value = "이미지 인식을 시작합니다..."
            } else {
                message.value = "카메라 권한이 필요합니다."
                result.value = false
            }
        }
    )

    // AR 기능 설정
    fun setupImageSensingAR() {
        try {
            if (arCoreSession.value == null) {
                val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> Session(context)
                    else -> {
                        message.value = "ARCore 설치가 필요합니다."
                        result.value = false
                        return
                    }
                }
                arCoreSession.value = session

                val config = Config(session)
                configureArSession(config)  // 최적화된 설정 적용
                if (!setupAugmentedImageDatabase(context, config, session)) {
                    message.value = "증강 이미지 데이터베이스를 설정할 수 없습니다."
                    result.value = false
                    return
                }
                session.configure(config)
            }
        } catch (e: Exception) {
            message.value = "AR 설정 중 오류 발생: ${e.message}"
            result.value = false
            Log.e("ImageSensing", "Error setting up AR", e)
        }
    }

    // Lifecycle 이벤트 관찰
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!isDetected.value && result.value == null) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            setupImageSensingAR()
                            glSurfaceView.onResume()
                            arCoreSession.value?.resume()
                            displayRotationHelper.onResume()
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isDetected.value) {
                        arCoreSession.value?.pause()
                        glSurfaceView.onPause()
                        displayRotationHelper.onPause()
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    arCoreSession.value?.close()
                    arCoreSession.value = null
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            arCoreSession.value?.pause()
            arCoreSession.value?.close()
            arCoreSession.value = null
        }
    }


    // 인식되면 세션 종료
    LaunchedEffect(isDetected.value) {
        if (isDetected.value) {
            result.value = true
            Log.d("ImageSensing", "'$imageName' 이미지 인식 완료! 세션을 종료합니다.")
            arCoreSession.value?.pause()
            glSurfaceView.onPause()
            displayRotationHelper.onPause()
            delay(100)
            arCoreSession.value?.close()
            arCoreSession.value = null
        }
    }

    // 화면 UI (보이지 않는 레이어)
    if (!isDetected.value && result.value == null) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    glSurfaceView.apply {
                        preserveEGLContextOnPause = true
                        setEGLContextClientVersion(2)
                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                        setRenderer(object : GLSurfaceView.Renderer {
                            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                                GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                                GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                GLES20.glGenTextures(1, cameraTextureId, 0)
                                try {
                                    backgroundRenderer.createOnGlThread(context, cameraTextureId[0])
                                } catch (e: IOException) {
                                    Log.e("ImageSensing", "Failed to initialize renderer", e)
                                }
                            }

                            var surfaceWidth = 0
                            var surfaceHeight = 0

                            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                                surfaceWidth = width
                                surfaceHeight = height
                                displayRotationHelper.onSurfaceChanged(width, height)
                                GLES20.glViewport(0, 0, width, height)
                            }

                            override fun onDrawFrame(gl: GL10?) {
                                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                                val session = arCoreSession.value
                                if (session == null || isDetected.value || result.value != null) {
                                    return
                                }
                                displayRotationHelper.updateSessionIfNeeded(session)

                                try {
                                    session.setCameraTextureName(cameraTextureId[0])
                                    val frame = session.update()

                                    // 이미지 인식 체크
                                    if (frame.camera.trackingState == TrackingState.TRACKING) {
                                        val updatedTrackables = frame.getUpdatedTrackables(AugmentedImage::class.java)
                                        Log.d("ImageSensing", "프레임 업데이트 - 발견된 이미지 개수: ${updatedTrackables.size}")

                                        for (image in updatedTrackables) {
                                            Log.d("ImageSensing", "이미지 발견 - 이름: '${image.name}', 찾는 이름: '$imageName', 상태: ${image.trackingState}, 방법: ${image.trackingMethod}")

                                            if (image.trackingState == TrackingState.TRACKING && image.name == imageName) {
                                                Log.d("ImageSensing", "✅ '$imageName' 이미지 인식됨! (tracking method: ${image.trackingMethod})")
                                                isDetected.value = true
                                                break
                                            }
                                        }
                                    } else {
                                        Log.d("ImageSensing", "카메라 추적 상태 아님: ${frame.camera.trackingState}")
                                    }

                                    // Draw camera background
                                    backgroundRenderer.draw(frame)

                                } catch (e: CameraNotAvailableException) {
                                    Log.e("ImageSensing", "Camera not available", e)
                                } catch (e: Exception) {
                                    Log.e("ImageSensing", "Error in onDrawFrame", e)
                                }
                            }
                        })
                        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = message.value,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    return result
}

/**
 * ImageSensing을 Composable UI와 함께 사용하는 함수
 * @param imageName 인식할 이미지 이름 (확장자 제외)
 * @param onDetected 이미지가 인식되었을 때 호출되는 콜백
 */
@Composable
fun ImageSensingView(
    imageName: String,
    onDetected: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val message = remember { mutableStateOf("'$imageName' 이미지를 찾는 중...") }

    // ARCore 세션과 GLSurfaceView 인스턴스를 remember로 관리
    val arCoreSession = remember { mutableStateOf<Session?>(null) }
    val glSurfaceView = remember { GLSurfaceView(context) }
    val isDetected = remember { mutableStateOf(false) }


    // Rendering objects
    val displayRotationHelper = remember { DisplayRotationHelper(context) }
    val backgroundRenderer = remember { BackgroundRenderer() }
    val cameraTextureId = remember { IntArray(1) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                message.value = "이미지 인식을 시작합니다..."
            } else {
                message.value = "카메라 권한이 필요합니다."
            }
        }
    )

    // AR 기능 설정
    fun setupImageSensingAR() {
        try {
            if (arCoreSession.value == null) {
                val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> Session(context)
                    else -> {
                        message.value = "ARCore 설치가 필요합니다."
                        return
                    }
                }
                arCoreSession.value = session

                val config = Config(session)
                configureArSession(config)  // 최적화된 설정 적용
                if (!setupAugmentedImageDatabase(context, config, session)) {
                    message.value = "증강 이미지 데이터베이스를 설정할 수 없습니다."
                    return
                }
                session.configure(config)
            }
        } catch (e: Exception) {
            message.value = "AR 설정 중 오류 발생: ${e.message}"
            Log.e("ImageSensing", "Error setting up AR", e)
        }
    }

    // Lifecycle 이벤트 관찰
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    if (!isDetected.value) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            setupImageSensingAR()
                            glSurfaceView.onResume()
                            arCoreSession.value?.resume()
                            displayRotationHelper.onResume()
                        } else {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    if (!isDetected.value) {
                        arCoreSession.value?.pause()
                        glSurfaceView.onPause()
                        displayRotationHelper.onPause()
                    }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    arCoreSession.value?.close()
                    arCoreSession.value = null
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            arCoreSession.value?.pause()
            arCoreSession.value?.close()
            arCoreSession.value = null
        }
    }

    // 인식되면 세션 종료
    LaunchedEffect(isDetected.value) {
        if (isDetected.value) {
            Log.d("ImageSensing", "'$imageName' 이미지 인식 완료! 세션을 종료합니다.")
            arCoreSession.value?.pause()
            glSurfaceView.onPause()
            displayRotationHelper.onPause()
            delay(100) // 약간의 딜레이 후 종료
            arCoreSession.value?.close()
            arCoreSession.value = null
        }
    }

    // 화면 UI
    if (!isDetected.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    glSurfaceView.apply {
                        preserveEGLContextOnPause = true
                        setEGLContextClientVersion(2)
                        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

                        setRenderer(object : GLSurfaceView.Renderer {
                            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                                GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
                                GLES20.glEnable(GLES20.GL_DEPTH_TEST)
                                GLES20.glGenTextures(1, cameraTextureId, 0)
                                try {
                                    backgroundRenderer.createOnGlThread(context, cameraTextureId[0])
                                } catch (e: IOException) {
                                    Log.e("ImageSensing", "Failed to initialize renderer", e)
                                }
                            }

                            var surfaceWidth = 0
                            var surfaceHeight = 0

                            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                                surfaceWidth = width
                                surfaceHeight = height
                                displayRotationHelper.onSurfaceChanged(width, height)
                                GLES20.glViewport(0, 0, width, height)
                            }

                            override fun onDrawFrame(gl: GL10?) {
                                GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

                                val session = arCoreSession.value
                                if (session == null || isDetected.value) {
                                    return
                                }
                                displayRotationHelper.updateSessionIfNeeded(session)

                                try {
                                    session.setCameraTextureName(cameraTextureId[0])
                                    val frame = session.update()

                                    // 이미지 인식 체크
                                    if (frame.camera.trackingState == TrackingState.TRACKING) {
                                        val updatedTrackables = frame.getUpdatedTrackables(AugmentedImage::class.java)
                                        for (image in updatedTrackables) {
                                            if (image.trackingState == TrackingState.TRACKING &&
                                                image.name == imageName &&
                                                image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                                                Log.d("ImageSensing", "'$imageName' 이미지 인식됨!")
                                                isDetected.value = true
                                                onDetected(imageName)
                                                break
                                            }
                                        }
                                    }

                                    // Draw camera background
                                    backgroundRenderer.draw(frame)

                                } catch (e: CameraNotAvailableException) {
                                    Log.e("ImageSensing", "Camera not available", e)
                                }
                            }
                        })
                        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = message.value,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

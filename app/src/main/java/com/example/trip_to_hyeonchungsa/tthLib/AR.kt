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
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import com.example.arfunction.rendering.DisplayRotationHelper
import com.example.arfunction.rendering.ObjectRenderer
import com.example.arfunction.rendering.BackgroundRenderer

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
    scale: Float,
    modifier: Modifier = Modifier,
    onModelClick: ((String) -> Unit)? = null,
    showDebugInfo: Boolean = true,
    autoStart: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val message = remember { mutableStateOf("AR 기능을 준비 중입니다...") }

    // 내부에서 Map으로 변환
    val imageToModelMap = remember(imageName, modelPath, scale) {
        mapOf(imageName to Pair(modelPath, scale))
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
                val session = when (ArCoreApk.getInstance().requestInstall(context as ComponentActivity, true)) {
                    ArCoreApk.InstallStatus.INSTALLED -> Session(context)
                    else -> {
                        message.value = "ARCore 설치가 필요합니다."
                        return
                    }
                }
                arCoreSession.value = session

                val config = Config(session)
                config.updateMode = Config.UpdateMode.LATEST_CAMERA_IMAGE
                if (!setupAugmentedImageDatabase(context, config, session)) {
                    message.value = "증강 이미지 데이터베이스를 설정할 수 없습니다."
                    return
                }
                session.configure(config)
            }
        } catch (e: Exception) {
            message.value = "AR 설정 중 오류 발생: ${e.message}"
            Log.e("ARFunction", "Error setting up AR", e)
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
                                objectRenderer.createOnGlThread(context, "models/andy.obj")
                                objectRenderer.setMaterialProperties(0.0f, 3.5f, 1.0f, 6.0f)
                            } catch (e: IOException) {
                                Log.e("ARFunction", "Failed to read obj file", e)
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
                                for ((_, pair) in trackedImages) {
                                    val (image, anchor) = pair
                                    // 추적 중이고 추적 방법이 FULL_TRACKING인 경우에만 렌더링
                                    if (image.trackingState == TrackingState.TRACKING &&
                                        image.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                                        val modelData = imageToModelMap[image.name]
                                        if (modelData != null) {
                                            val (_, modelScale) = modelData
                                            val modelMatrix = FloatArray(16)
                                            anchor.pose.toMatrix(modelMatrix, 0)
                                            objectRenderer.updateModelMatrix(modelMatrix, modelScale)
                                            objectRenderer.draw(viewMatrix, projectionMatrix, null, floatArrayOf(1.0f, 0.0f, 0.0f, 1.0f))
                                        }
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

private fun setupAugmentedImageDatabase(context: Context, config: Config, session: Session): Boolean {
    try {
        context.assets.open("augmented_images/augmented_image_database.imgdb").use { `is` ->
            val augmentedImageDatabase = AugmentedImageDatabase.deserialize(session, `is`)
            config.augmentedImageDatabase = augmentedImageDatabase
            return true
        }
    } catch (e: IOException) {
        Log.e("ARFunction", "IO exception loading augmented image database.", e)
    }
    return false
}

private fun handleFrame(
    frame: Frame,
    imageToModelMap: Map<String, Pair<String, Float>>,
    trackedImages: MutableMap<Int, Pair<AugmentedImage, Anchor>>,
    message: MutableState<String>
) {
    if (frame.camera.trackingState != TrackingState.TRACKING) return

    val updatedTrackables = frame.getUpdatedTrackables(AugmentedImage::class.java)

    // 추적 중인 이미지 처리
    for (image in updatedTrackables) {
        when (image.trackingState) {
            TrackingState.TRACKING -> {
                if (!trackedImages.containsKey(image.index)) {
                    // 인식이 활성화된 경우에만 새 이미지 추적
                    if (isTrackingEnabled) {
                        val anchor = image.createAnchor(image.centerPose)
                        trackedImages[image.index] = image to anchor
                        message.value = "'${image.name}' 이미지 추적 성공!"
                    }
                } else {
                    // 기존 앵커 업데이트
                    trackedImages[image.index] = image to trackedImages[image.index]!!.second
                }
            }
            TrackingState.PAUSED -> {
                // 추적이 일시 중지되면 제거 (사진이 보이지 않음)
                trackedImages.remove(image.index)?.second?.detach()
                message.value = "이미지 추적 중단"
            }
            TrackingState.STOPPED -> {
                // 추적이 완전히 중지되면 제거
                trackedImages.remove(image.index)?.second?.detach()
            }
        }
    }
}

// AR 세션 관리를 위한 전역 변수
private var globalArSession: Session? = null
private var globalTrackedImagesRef: MutableMap<Int, Pair<AugmentedImage, Anchor>>? = null

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
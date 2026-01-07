package com.example.trip_to_hyeonchungsa.tthLib.rendering

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.opengl.GLUtils
import android.opengl.Matrix
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * AR 공간에 2D 이미지를 렌더링하는 클래스
 */
class ImageRenderer {
    private var program = 0
    private var positionAttrib = 0
    private var texCoordAttrib = 0
    private var modelViewProjectionUniform = 0
    private var textureUniform = 0
    private var textureId = IntArray(1)

    private var vertexBuffer: FloatBuffer? = null
    private var texCoordBuffer: FloatBuffer? = null

    private val modelViewProjectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    // 이미지 평면을 만들기 위한 정점 좌표 (사각형)
    private val vertexCoords = floatArrayOf(
        -0.5f,  0.5f, 0.0f,  // 왼쪽 위
        -0.5f, -0.5f, 0.0f,  // 왼쪽 아래
         0.5f, -0.5f, 0.0f,  // 오른쪽 아래
         0.5f,  0.5f, 0.0f   // 오른쪽 위
    )

    // 텍스처 좌표
    private val texCoords = floatArrayOf(
        0.0f, 0.0f,  // 왼쪽 위
        0.0f, 1.0f,  // 왼쪽 아래
        1.0f, 1.0f,  // 오른쪽 아래
        1.0f, 0.0f   // 오른쪽 위
    )

    // Vertex shader
    private val vertexShaderCode = """
        uniform mat4 u_ModelViewProjection;
        attribute vec4 a_Position;
        attribute vec2 a_TexCoord;
        varying vec2 v_TexCoord;
        
        void main() {
            gl_Position = u_ModelViewProjection * a_Position;
            v_TexCoord = a_TexCoord;
        }
    """.trimIndent()

    // Fragment shader
    private val fragmentShaderCode = """
        precision mediump float;
        uniform sampler2D u_Texture;
        varying vec2 v_TexCoord;
        
        void main() {
            gl_FragColor = texture2D(u_Texture, v_TexCoord);
        }
    """.trimIndent()

    /**
     * OpenGL 리소스 초기화 및 이미지 로드
     * @param context Context
     * @param drawableResId drawable 리소스 ID (예: R.drawable.my_image)
     */
    fun createOnGlThread(context: Context, drawableResId: Int) {
        // 버퍼 초기화
        vertexBuffer = ByteBuffer.allocateDirect(vertexCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(vertexCoords)
                position(0)
            }

        texCoordBuffer = ByteBuffer.allocateDirect(texCoords.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply {
                put(texCoords)
                position(0)
            }

        // 셰이더 컴파일
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode)

        // 프로그램 생성 및 링크
        program = GLES20.glCreateProgram()
        GLES20.glAttachShader(program, vertexShader)
        GLES20.glAttachShader(program, fragmentShader)
        GLES20.glLinkProgram(program)

        // 셰이더 변수 위치 가져오기
        positionAttrib = GLES20.glGetAttribLocation(program, "a_Position")
        texCoordAttrib = GLES20.glGetAttribLocation(program, "a_TexCoord")
        modelViewProjectionUniform = GLES20.glGetUniformLocation(program, "u_ModelViewProjection")
        textureUniform = GLES20.glGetUniformLocation(program, "u_Texture")

        // 텍스처 로드
        loadTexture(context, drawableResId)

        // 모델 매트릭스 초기화
        Matrix.setIdentityM(modelMatrix, 0)
    }

    /**
     * 셰이더를 컴파일하는 헬퍼 함수
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    /**
     * drawable에서 텍스처 로드
     */
    private fun loadTexture(context: Context, drawableResId: Int) {
        GLES20.glGenTextures(1, textureId, 0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])

        // 텍스처 파라미터 설정
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)

        // Bitmap 로드
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()
    }

    /**
     * 모델 매트릭스 업데이트
     * @param matrix 포즈 매트릭스
     * @param scaleFactor 크기 배율
     */
    fun updateModelMatrix(matrix: FloatArray, scaleFactor: Float) {
        System.arraycopy(matrix, 0, modelMatrix, 0, 16)

        // X축 기준으로 90도 회전 (이미지를 세워서 정면을 향하게 함)
        Matrix.rotateM(modelMatrix, 0, 90f, 1f, 0f, 0f)

        // 스케일 적용
        Matrix.scaleM(modelMatrix, 0, scaleFactor, scaleFactor, scaleFactor)
    }

    /**
     * 이미지를 렌더링
     * @param viewMatrix 뷰 매트릭스
     * @param projectionMatrix 프로젝션 매트릭스
     */
    fun draw(viewMatrix: FloatArray, projectionMatrix: FloatArray) {
        GLES20.glUseProgram(program)

        // ModelViewProjection 매트릭스 계산
        val modelViewMatrix = FloatArray(16)
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(modelViewProjectionMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0)

        // 블렌딩 활성화 (투명도 처리)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        // 유니폼 설정
        GLES20.glUniformMatrix4fv(modelViewProjectionUniform, 1, false, modelViewProjectionMatrix, 0)

        // 텍스처 바인딩
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId[0])
        GLES20.glUniform1i(textureUniform, 0)

        // 정점 속성 설정
        GLES20.glEnableVertexAttribArray(positionAttrib)
        GLES20.glVertexAttribPointer(positionAttrib, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer)

        GLES20.glEnableVertexAttribArray(texCoordAttrib)
        GLES20.glVertexAttribPointer(texCoordAttrib, 2, GLES20.GL_FLOAT, false, 0, texCoordBuffer)

        // 사각형 그리기
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4)

        // 정리
        GLES20.glDisableVertexAttribArray(positionAttrib)
        GLES20.glDisableVertexAttribArray(texCoordAttrib)
        GLES20.glDisable(GLES20.GL_BLEND)
    }
}


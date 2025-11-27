/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.trip_to_hyeonchungsa.tthLib.rendering

import android.content.Context
import android.opengl.GLES20
import android.opengl.Matrix
import com.example.trip_to_hyeonchungsa.tthLib.rendering.ShaderUtil.loadFragmentShader
import com.example.trip_to_hyeonchungsa.tthLib.rendering.ShaderUtil.loadVertexShader
import de.javagl.obj.Obj
import de.javagl.obj.ObjReader
import de.javagl.obj.ObjUtils
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import kotlin.math.max

/** Renders an object loaded from an OBJ file in OpenGL.  */
class ObjectRenderer {
  /**
   * Blend mode.
   *
   * @see .setBlendMode
   */
  enum class BlendMode {
    /** Multiplies the destination color by the source alpha.  */
    Shadow,

    /** Normal alpha blending.  */
    Grid
  }

  private val viewMatrix = FloatArray(16)
  private val modelViewMatrix = FloatArray(16)
  private val modelViewProjectionMatrix = FloatArray(16)
  private var program = 0
  private var positionAttrib = 0
  private var modelViewProjectionUniform = 0
  private var lightingParametersUniform = 0
  private var materialParametersUniform = 0
  private var colorCorrectionParameterUniform = 0
  private var colorUniform = 0
  private var blendModeUniform = 0
  private var vertices: FloatBuffer? = null
  private var normals: FloatBuffer? = null
  private var indices: ShortBuffer? = null
  private var numIndices = 0

  // Default color for the object (a nice blue)
  private val defaultColor = floatArrayOf(0.0f, 0.0f, 1.0f, 1.0f)

  /**
   * Creates and initializes OpenGL resources needed for rendering the model.
   *
   * @param context Context for loading the shader and below-named files.
   * @param objAssetName Name of the OBJ file containing the model geometry.
   */
  @Throws(IOException::class)
  fun createOnGlThread(
    context: Context,
    objAssetName: String
  ) {
    // Read the obj file.
    val objInputStream: InputStream = context.assets.open(objAssetName)
    val obj: Obj = ObjReader.read(objInputStream)

    // Prepare the Obj so that its structure is suitable for
    // rendering with OpenGL:
    // 1. Triangulate it
    // 2. Make sure that normals are not ambiguous
    // 3. Convert it to single-indexed data
    val renderableObj = ObjUtils.convertToRenderable(obj)

    // Now extract the data from the renderable Obj object
    // Extract vertices as FloatBuffer
    val numVertices = renderableObj.numVertices
    val localVertices = ByteBuffer.allocateDirect(numVertices * 3 * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    for (i in 0 until numVertices) {
        val v = renderableObj.getVertex(i)
        localVertices.put(v.x)
        localVertices.put(v.y)
        localVertices.put(v.z)
    }
    localVertices.position(0)

    // Extract normals as FloatBuffer
    val numNormals = renderableObj.numNormals
    val localNormals = ByteBuffer.allocateDirect(numNormals * 3 * BYTES_PER_FLOAT)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    for (i in 0 until numNormals) {
        val n = renderableObj.getNormal(i)
        localNormals.put(n.x)
        localNormals.put(n.y)
        localNormals.put(n.z)
    }
    localNormals.position(0)

    // Extract face indices
    val numFaces = renderableObj.numFaces
    numIndices = numFaces * 3
    val localIndices = ByteBuffer.allocateDirect(numIndices * BYTES_PER_SHORT)
        .order(ByteOrder.nativeOrder())
        .asShortBuffer()
    for (i in 0 until numFaces) {
        val face = renderableObj.getFace(i)
        localIndices.put(face.getVertexIndex(0).toShort())
        localIndices.put(face.getVertexIndex(1).toShort())
        localIndices.put(face.getVertexIndex(2).toShort())
    }
    localIndices.position(0)

    // Convert object data into a buffer format that can be rendered in OpenGL
    vertices = localVertices
    normals = localNormals
    indices = localIndices

    val vertexShader =
      loadVertexShader(context, "shaders/object.vert")
    val fragmentShader =
      loadFragmentShader(context, "shaders/object.frag")
    program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program, vertexShader)
    GLES20.glAttachShader(program, fragmentShader)
    GLES20.glLinkProgram(program)
    GLES20.glUseProgram(program)
    positionAttrib = GLES20.glGetAttribLocation(program, "a_Position")
    modelViewProjectionUniform = GLES20.glGetUniformLocation(program, "u_ModelViewProjection")
    lightingParametersUniform = GLES20.glGetUniformLocation(program, "u_LightingParameters")
    materialParametersUniform = GLES20.glGetUniformLocation(program, "u_MaterialParameters")
    colorCorrectionParameterUniform =
      GLES20.glGetUniformLocation(program, "u_ColorCorrectionParameters")
    colorUniform = GLES20.glGetUniformLocation(program, "u_ObjColor")
    blendModeUniform = GLES20.glGetUniformLocation(program, "u_BlendMode")
  }

  /**
   * Selects the blending mode for rendering.
   *
   * @param blendMode The blend mode. Null indicates no blending (opaque rendering).
   */
  fun setBlendMode(blendMode: BlendMode?) {
    GLES20.glUseProgram(program)
    GLES20.glUniform1i(blendModeUniform, blendMode?.ordinal ?: -1)
  }

  /**
   * Updates the object model matrix and applies scaling.
   *
   * @param modelMatrix A 4x4 model-to-world matrix.
   * @param scaleFactor A separate scaling factor to apply before the `modelMatrix`.
   * @see Matrix
   */
  fun updateModelMatrix(modelMatrix: FloatArray?, scaleFactor: Float) {
    if (modelMatrix == null) return
    
    val scaleMatrix = FloatArray(16)
    Matrix.setIdentityM(scaleMatrix, 0)
    scaleMatrix[0] = scaleFactor
    scaleMatrix[5] = scaleFactor
    scaleMatrix[10] = scaleFactor
    Matrix.multiplyMM(this.modelViewMatrix, 0, modelMatrix, 0, scaleMatrix, 0)
  }

  /**
   * Sets the surface material properties.
   *
   * @param ambient The ambient reflectance of the material.
   * @param diffuse The diffuse reflectance of the material.
   * @param specular The specular reflectance of the material.
   * @param specularPower The specular power of the material.
   */
  fun setMaterialProperties(
    ambient: Float,
    diffuse: Float,
    specular: Float,
    specularPower: Float
  ) {
    GLES20.glUseProgram(program)
    GLES20.glUniform4f(materialParametersUniform, ambient, diffuse, specular, specularPower)
  }

  /**
   * Draws the model.
   *
   * @param cameraView A 4x4 view matrix, in column-major order.
   * @param cameraPerspective A 4x4 projection matrix, in column-major order.
   * @param colorCorrectionRgba Illumination correction values to apply to the model color.
   * @see .setBlendMode
   * @see .updateModelMatrix
   * @see .setMaterialProperties
   */
  fun draw(
    cameraView: FloatArray?,
    cameraPerspective: FloatArray?,
    colorCorrectionRgba: FloatArray?,
    objColor: FloatArray?
  ) {
    if (cameraView == null || cameraPerspective == null) return
    
    Matrix.multiplyMM(modelViewMatrix, 0, cameraView, 0, modelViewMatrix, 0)
    Matrix.multiplyMM(modelViewProjectionMatrix, 0, cameraPerspective, 0, modelViewMatrix, 0)
    GLES20.glUseProgram(program)

    // Set the lighting environment properties
    val lightDirection = floatArrayOf(0.0f, 1.0f, 0.0f, 0.0f)
    Matrix.multiplyMV(viewMatrix, 0, cameraView, 0, lightDirection, 0)
    normalize(viewMatrix)
    GLES20.glUniform4f(
      lightingParametersUniform,
      viewMatrix[0],
      viewMatrix[1],
      viewMatrix[2],
      1f
    )
    GLES20.glUniform4fv(colorCorrectionParameterUniform, 1, colorCorrectionRgba ?: floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f), 0)
    GLES20.glUniform4fv(colorUniform, 1, objColor ?: defaultColor, 0)


    // Set the object material properties
    GLES20.glUniform4f(materialParametersUniform, 0.0f, 3.5f, 1.0f, 6.0f)

    // Set the ModelViewProjection matrix in the shader.
    GLES20.glUniformMatrix4fv(modelViewProjectionUniform, 1, false, modelViewProjectionMatrix, 0)

    // Enable vertex arrays
    GLES20.glEnableVertexAttribArray(positionAttrib)
    GLES20.glVertexAttribPointer(
      positionAttrib,
      3,
      GLES20.GL_FLOAT,
      false,
      0,
      vertices
    )

    // Draw the model.
    GLES20.glDrawElements(GLES20.GL_TRIANGLES, numIndices, GLES20.GL_UNSIGNED_SHORT, indices)

    // Disable vertex arrays
    GLES20.glDisableVertexAttribArray(positionAttrib)
  }

  private fun normalize(v: FloatArray) {
    val norm =
      (v[0] * v[0] + v[1] * v[1] + v[2] * v[2]).toDouble().let { kotlin.math.sqrt(it) }.toFloat()
    v[0] /= norm
    v[1] /= norm
    v[2] /= norm
  }

  companion object {
    private val TAG = ObjectRenderer::class.java.simpleName
    private const val BYTES_PER_FLOAT = Float.SIZE_BYTES
    private const val BYTES_PER_SHORT = Short.SIZE_BYTES
  }
}

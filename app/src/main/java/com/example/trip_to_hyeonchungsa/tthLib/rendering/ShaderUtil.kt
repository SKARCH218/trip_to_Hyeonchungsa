/*
 * Copyright 2017 Google LLC
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
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/** Shader helper functions.  */
object ShaderUtil {
  private val TAG = ShaderUtil::class.java.simpleName

  /**
   * Converts a raw text file into a string.
   *
   * @param context The context for resource file access.
   * @param resId The resource ID of the raw text file.
   * @return The contents of the text file, or null in case of error.
   */
  @Throws(IOException::class)
  fun loadString(context: Context, filename: String): String {
    val inputStream = context.assets.open(filename)
    BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8)).use { reader ->
      val sb = StringBuilder()
      var line: String?
      while (reader.readLine().also { line = it } != null) {
        sb.append(line).append("\n")
      }
      return sb.toString()
    }
  }

  /**
   * Loads and compiles a vertex shader from the provided asset file.
   *
   * @param context The context for asset file access.
   * @param filename The asset file name for the vertex shader.
   * @return An OpenGL shader handle.
   */
  fun loadVertexShader(context: Context, filename: String): Int {
    return loadShader(context, filename, GLES20.GL_VERTEX_SHADER)
  }

  /**
   * Loads and compiles a fragment shader from the provided asset file.
   *
   * @param context The context for asset file access.
   * @param filename The asset file name for the fragment shader.
   * @return An OpenGL shader handle.
   */
  fun loadFragmentShader(context: Context, filename: String): Int {
    return loadShader(context, filename, GLES20.GL_FRAGMENT_SHADER)
  }

  /**
   * Loads and compiles a shader from the provided asset file.
   *
   * @param context The context for asset file access.
   * @param filename The asset file name for the shader.
   * @param type The shader type.
   * @return An OpenGL shader handle.
   */
  private fun loadShader(context: Context, filename: String, type: Int): Int {
    val shaderCode =
      try {
        loadString(context, filename)
      } catch (e: IOException) {
        Log.e(TAG, "Failed to load shader file: $filename", e)
        return 0
      }
    var shader = GLES20.glCreateShader(type)
    GLES20.glShaderSource(shader, shaderCode)
    GLES20.glCompileShader(shader)

    // Get the compilation status.
    val compileStatus = IntArray(1)
    GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0)

    // If the compilation failed, delete the shader.
    if (compileStatus[0] == 0) {
      Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader))
      GLES20.glDeleteShader(shader)
      shader = 0
    }
    if (shader == 0) {
      throw RuntimeException("Error creating shader.")
    }
    return shader
  }

  /**
   * Creates a shader program, attaches the specified vertex and fragment shaders, and links the
   * program.
   *
   * @param vertexShader The vertex shader handle.
   * @param fragmentShader The fragment shader handle.
   * @return An OpenGL program handle.
   */
  fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
    var program = GLES20.glCreateProgram()
    GLES20.glAttachShader(program, vertexShader)
    GLES20.glAttachShader(program, fragmentShader)
    GLES20.glLinkProgram(program)

    // Get the link status.
    val linkStatus = IntArray(1)
    GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0)

    // If the link failed, delete the program.
    if (linkStatus[0] == 0) {
      Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(program))
      GLES20.glDeleteProgram(program)
      program = 0
    }
    if (program == 0) {
      throw RuntimeException("Error creating program.")
    }
    return program
  }

  /**
   * Validates an OpenGL program. Should only be used for debugging.
   *
   * @param programHandle The program handle.
   */
  fun validateProgram(programHandle: Int) {
    GLES20.glValidateProgram(programHandle)
    val validateStatus = IntArray(1)
    GLES20.glGetProgramiv(programHandle, GLES20.GL_VALIDATE_STATUS, validateStatus, 0)
    Log.v(
      TAG,
      "Results of validating program: " +
        validateStatus[0] +
        "\nLog: " +
        GLES20.glGetProgramInfoLog(programHandle)
    )
  }
}
/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.pdsd;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import com.example.pdsd.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 2.0.
 */
public class Triangle {

    /*
	private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";
   */
	String vertexShaderCode =
			"uniform mat4 uMVPMatrix;" +
		    "attribute vec4 vPosition;" +
		    "attribute vec2 aTexCoords;" +
		    "varying vec2 vTexCoords;" +
			"void main()" +
			"{" +
			    "gl_Position = uMVPMatrix * vPosition;" +
			    "vTexCoords = aTexCoords;" +
			"}";
	String fragmentShaderCode = 
			"precision mediump float;" +
			"varying vec2 vTexCoords;" +
			"uniform sampler2D uTexId;" +
			"void main()" +
			"{" +
				"gl_FragColor = texture2D(uTexId, vTexCoords);" +
			"}";
    private final FloatBuffer vertexBuffer;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
	private int mTexCoords;
	private int mTexLoc;
	private int mTexId;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 5;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f,  0.0122008459f, 0.0f,0.3f,0.3f,   // top
           -0.08f, -0.111004243f, 0.0f,0.3f,0.5f,   // bottom left
            0.08f, -0.111004243f, 0.0f,0.5f,0.5f    // bottom right
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private GLSurfaceView mGLview;
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 0.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public Triangle(GLSurfaceView GLview) {
    	mGLview = GLview;
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        
       
        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables

    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    
	public static int loadTexture(GLSurfaceView view, int imgResID){
		Log.d("Utils", "Loadtexture");
		Bitmap img = null;
		int textures[] = new int[1];
		try {
			img = BitmapFactory.decodeResource(view.getResources(), imgResID);
			GLES20.glGenTextures(1, textures, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, img, 0);
			Log.d("LoadTexture", "Loaded texture"+":H:"+img.getHeight()+":W:"+img.getWidth());
		} catch (Exception e){
			Log.d("LoadTexture", e.toString()+ ":" + e.getMessage()+":"+e.getLocalizedMessage());
		}
		img.recycle();
		return textures[0];		
	}
    
    
    public void draw(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
		mTexCoords = GLES20.glGetAttribLocation(mProgram, "aTexCoords");
		mTexLoc = GLES20.glGetUniformLocation(mProgram, "uTexId");
		
		mTexId = loadTexture(mGLview, R.drawable.basket);
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle,3,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		
		vertexBuffer.position(3);
		GLES20.glVertexAttribPointer(mTexCoords, 2, GLES20.GL_FLOAT, false, 5* 4, vertexBuffer);
		GLES20.glEnableVertexAttribArray(mTexCoords);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId);
		GLES20.glUniform1i(mTexLoc, 0);
		
       // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

}

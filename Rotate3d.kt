package com.woman.beautylive.util

import android.graphics.Camera
import android.view.animation.Animation
import android.view.animation.Transformation

/**
 * @author Heyi
 * @since 1.0.0
 */
class Rotate3d:Animation() {
    private var mFromDegrees: Float?=null
    private var mToDegrees: Float?=null
    private var mCenterX: Float?=null
    private var mCenterY: Float?=null
    private var mDepthZ: Float?=null
    private var mCamera: Camera? = null
    private var mDirection: Int?=null
    private val ROTATE_X = 0//沿着x轴旋转
    private val ROTATE_Y = 1//沿着y轴旋转

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair of
     * X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length of
     * the translation can be specified, as well as whether the translation
     * should be reversed in time.

     * @param direction
     * *            the direction of the 3D rotation
     * *
     * @param fromDegrees
     * *            the start angle of the 3D rotation
     * *
     * @param toDegrees
     * *            the end angle of the 3D rotation
     * *
     * @param centerX
     * *            the X center of the 3D rotation
     * *
     * @param centerY
     * *            the Y center of the 3D rotation
     */
    fun initData(direction: Int, fromDegrees: Float, toDegrees: Float,
                      centerX: Float, centerY: Float, depthZ: Float) {
        mDirection = direction
        mFromDegrees = fromDegrees
        mToDegrees = toDegrees
        mCenterX = centerX
        mCenterY = centerY
        mDepthZ = depthZ
    }

    override fun initialize(width: Int, height: Int, parentWidth: Int,
                            parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val fromDegrees = mFromDegrees
        val degrees = fromDegrees!! + (mToDegrees!! - fromDegrees) * interpolatedTime

        val centerX = mCenterX
        val centerY = mCenterY
        val camera = mCamera

        val matrix = t.matrix

        camera!!.save()

        if (centerX != 0f) {
            if (interpolatedTime < 0.5) {
                camera.translate(0.0f, 0.0f, mDepthZ!! * interpolatedTime)
            } else {
                camera.translate(0.0f, 0.0f, mDepthZ!! * (1.0f - interpolatedTime))
            }
        }

        when (mDirection) {
            ROTATE_X -> camera.rotateX(degrees)
            ROTATE_Y -> camera.rotateY(degrees)
        }

        camera.getMatrix(matrix)
        camera.restore()
        matrix.preTranslate(-centerX!!, -centerY!!)
        matrix.postTranslate(centerX, centerY)
    }
}
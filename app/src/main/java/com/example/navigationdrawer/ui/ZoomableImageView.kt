package com.example.navigationdrawer.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.abs

class ZoomableImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs),
    ScaleGestureDetector.OnScaleGestureListener {

    private var matrixA = Matrix()
    private var mode = NONE

    // Remember some things for zooming
    private var last = PointF()
    private var start = PointF()
    private var minScale = 1f
    private var maxScale = 3f
    private var m: FloatArray

    private var viewWidth = 0
    private var viewHeight = 0
    private var saveScale = 1f
    private var origWidth = 0f
    private var origHeight = 0f

    private var mScaleDetector: ScaleGestureDetector

    init {
        super.setClickable(true)
        mScaleDetector = ScaleGestureDetector(context, this)
        matrixA.setTranslate(1f, 1f)
        m = FloatArray(9)
        imageMatrix = matrixA
        scaleType = ScaleType.MATRIX
    }

    fun setImageAndCenter(bitmap: Bitmap?) {
        super.setImageBitmap(bitmap)
        if (bitmap != null) {
            val bmWidth = bitmap.width
            val bmHeight = bitmap.height
            val scaleX = viewWidth.toFloat() / bmWidth.toFloat()
            val scaleY = viewHeight.toFloat() / bmHeight.toFloat()
            val scale = scaleX.coerceAtMost(scaleY)
            matrixA.setScale(scale, scale)
            saveScale = 1f

            // Center the image
            var redundantYSpace = viewHeight.toFloat() - scale * bmHeight.toFloat()
            var redundantXSpace = viewWidth.toFloat() - scale * bmWidth.toFloat()
            redundantYSpace /= 2f
            redundantXSpace /= 2f
            matrixA.postTranslate(redundantXSpace, redundantYSpace)
            origWidth = viewWidth - 2 * redundantXSpace
            origHeight = viewHeight - 2 * redundantYSpace
            imageMatrix = matrixA
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        mScaleDetector.onTouchEvent(event)
        val curr = PointF(event.x, event.y)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                last.set(curr)
                start.set(last)
                mode = DRAG
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                val deltaX = curr.x - last.x
                val deltaY = curr.y - last.y
                val fixTransX = getFixDragTrans(deltaX, viewWidth.toFloat(), origWidth * saveScale)
                val fixTransY = getFixDragTrans(deltaY, viewHeight.toFloat(), origHeight * saveScale)
                matrixA.postTranslate(fixTransX, fixTransY)
                fixTrans()
                last.set(curr.x, curr.y)
            }
            MotionEvent.ACTION_UP -> {
                mode = NONE
                val xDiff = abs(curr.x - start.x).toInt()
                val yDiff = abs(curr.y - start.y).toInt()
                if (xDiff < CLICK && yDiff < CLICK) performClick()
            }
            MotionEvent.ACTION_POINTER_UP -> mode = NONE
        }
        imageMatrix = matrixA
        invalidate()
        return true
    }

    private fun fixTrans() {
        matrixA.getValues(m)
        val transX = m[Matrix.MTRANS_X]
        val transY = m[Matrix.MTRANS_Y]
        val fixTransX = getFixTrans(transX, viewWidth.toFloat(), origWidth * saveScale)
        val fixTransY = getFixTrans(transY, viewHeight.toFloat(), origHeight * saveScale)
        if (fixTransX != 0f || fixTransY != 0f) matrixA.postTranslate(fixTransX, fixTransY)
    }

    private fun getFixTrans(trans: Float, viewSize: Float, contentSize: Float): Float {
        val minTrans: Float
        val maxTrans: Float
        if (contentSize <= viewSize) {
            minTrans = 0f
            maxTrans = viewSize - contentSize
        } else {
            minTrans = viewSize - contentSize
            maxTrans = 0f
        }
        if (trans < minTrans) return -trans + minTrans
        return if (trans > maxTrans) -trans + maxTrans else 0f
    }

    private fun getFixDragTrans(delta: Float, viewSize: Float, contentSize: Float): Float {
        return if (contentSize <= viewSize) {
            0f
        } else delta
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        var mScaleFactor = detector.scaleFactor
        val origScale = saveScale
        saveScale *= mScaleFactor
        if (saveScale > maxScale) {
            saveScale = maxScale
            mScaleFactor = maxScale / origScale
        } else if (saveScale < minScale) {
            saveScale = minScale
            mScaleFactor = minScale / origScale
        }
        if (origWidth * saveScale <= viewWidth || origHeight * saveScale <= viewHeight) matrixA.postScale(
            mScaleFactor,
            mScaleFactor,
            viewWidth / 2f,
            viewHeight / 2f
        ) else matrixA.postScale(
            mScaleFactor,
            mScaleFactor,
            detector.focusX,
            detector.focusY
        )
        fixTrans()
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        mode = ZOOM
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {}

    companion object {
        // Touch modes
        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
        const val CLICK = 3
    }
}

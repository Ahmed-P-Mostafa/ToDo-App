package com.polotika.pointyNotes.pojo.adapters

import android.animation.ValueAnimator
import android.os.SystemClock
import android.view.InputDevice
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


abstract class SwipeHelper :ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.START) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}


object SwipeUtils {
    /**
     * Programmatically swipe RecyclerView item
     * @param recyclerView RecyclerView which item will be swiped
     * @param index Position of item
     * @param distance Swipe distance
     * @param direction Swipe direction, can be [ItemTouchHelper.START] or [ItemTouchHelper.END]
     * @param time Animation time in milliseconds
     */
    fun swipeRecyclerViewItem(
        recyclerView: RecyclerView,
        index: Int,
        distance: Int,
        direction: Int,
        time: Long
    ) {
        val childView = recyclerView.getChildAt(index) ?: return
        val x = childView.width / 2F
        val viewLocation = IntArray(2)
        childView.getLocationInWindow(viewLocation)
        val y = (viewLocation[1] + childView.height) / 2F
        val downTime = SystemClock.uptimeMillis()
        recyclerView.dispatchTouchEvent(
            MotionEvent.obtain(
                downTime,
                downTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                0
            )
        )
        ValueAnimator.ofInt(0, distance).apply {
            duration = time
            addUpdateListener {
                val dX = it.animatedValue as Int
                val mX = when (direction) {
                    ItemTouchHelper.END -> x + dX
                    ItemTouchHelper.START -> x - dX
                    else -> 0F
                }
                recyclerView.dispatchTouchEvent(
                    MotionEvent.obtain(
                        downTime,
                        SystemClock.uptimeMillis(),
                        MotionEvent.ACTION_MOVE,
                        mX,
                        y,
                        0
                    )
                )
            }
        }.start()
    }
}

fun ViewGroup.performSwipeToLeft(target: View, distance: Float) {
    this.performSwipe(target, distanceX = -distance, distanceY = 0f)
}
fun ViewGroup.performSwipeToRight(target: View, distance: Float) {
    this.performSwipe(target, distanceX = +distance, distanceY = 0f)
}

fun ViewGroup.performSwipe(target: View, distanceX: Float, distanceY: Float) {
    val parentCoords = intArrayOf(0, 0)
    this.getLocationInWindow(parentCoords)

    val childCoords = intArrayOf(0, 0)
    target.getLocationInWindow(childCoords)

    val initGlobalX = childCoords[0].toFloat() + 1f
    val initGlobalY = childCoords[1].toFloat() + 1f

    val initLocalX = (childCoords[0] - parentCoords[0]).toFloat() + 1f
    val initLocalY = (childCoords[1] - parentCoords[1]).toFloat() + 1f

    val downTime = SystemClock.uptimeMillis()
    var eventTime = SystemClock.uptimeMillis()

    this.dispatchTouchEvent(
        MotionEvent.obtain(
            downTime,
            eventTime,
            MotionEvent.ACTION_DOWN,
            initGlobalX,
            initGlobalY,
            0
        ).apply {
            setLocation(initLocalX, initLocalY)
            source = InputDevice.SOURCE_TOUCHSCREEN
        }
    )

    val steps = 20
    var i = 0
    while (i in 0..steps) {
        val globalX = initGlobalX + i * distanceX / steps
        val globalY = initGlobalY + i * distanceY / steps
        val localX = initLocalX + i * distanceX / steps
        val localY = initLocalY + i * distanceY / steps
        if (globalX <= 10f || globalY <= 10f) {
            break
        }
        this.dispatchTouchEvent(
            MotionEvent.obtain(
                downTime,
                ++eventTime,
                MotionEvent.ACTION_MOVE,
                globalX,
                globalY,
                0
            ).apply {
                setLocation(localX, localY)
                source = InputDevice.SOURCE_TOUCHSCREEN
            }
        )
        i++
    }

    this.dispatchTouchEvent(
        MotionEvent.obtain(
            downTime,
            ++eventTime,
            MotionEvent.ACTION_UP,
            initGlobalX + i * distanceX,
            initGlobalY + i * distanceY,
            0
        ).apply {
            setLocation(initLocalX + i * distanceX, initLocalY + i * distanceY)
            source = InputDevice.SOURCE_TOUCHSCREEN
        }
    )
}
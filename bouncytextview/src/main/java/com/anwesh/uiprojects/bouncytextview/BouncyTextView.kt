package com.anwesh.uiprojects.bouncytextview

/**
 * Created by anweshmishra on 11/11/19.
 */

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.content.Context
import android.app.Activity

val text : String = "HELLO"
val scGap : Float = 0.02f
val strokeFactor : Int = 90
val sizeFactor : Float = 2.9f
val delay : Long = 30
val foreColor : Int = Color.parseColor("#BDBDBD")
val backColor : Int = Color.parseColor("#212121")

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawBouncyText(ch : Char, scale : Float, paint : Paint) {
    val sc : Float = 0.5f + 0.5f * scale.sinify()
    val text : String = "" + ch
    save()
    scale(sc, sc)
    drawText(text, -paint.measureText(text) / 2, paint.textSize / 4, paint)
    restore()
}

fun Canvas.drawLine(i : Int, scale : Float, gap : Float, paint : Paint) {
    save()
    translate(i * gap, paint.strokeWidth / 2)
    drawLine(0f, 0f, gap * scale, 0f, paint)
    restore()
}

fun Canvas.drawBTNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val gap : Float = h / (text.length + 1)
    val size : Float = gap / sizeFactor
    paint.textSize = gap
    paint.color = foreColor
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLine(i, scale.divideScale(1, 2), w / (text.length), paint)
    save()
    translate(w / 2, gap * (i + 1))
    drawBouncyText(text[i], scale.divideScale(0, 2), paint)
    restore()
}

class BouncyTextView(ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class BTNode(var i : Int, val state : State = State()) {

        private var next : BTNode? = null
        private var prev : BTNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < text.length - 1) {
                next = BTNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBTNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : BTNode {
            var curr : BTNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class BouncyText(var i : Int) {

        private val root : BTNode = BTNode(0)
        private var curr : BTNode = root
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            root.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : BouncyTextView) {

        private val animator : Animator = Animator(view)
        private val bt : BouncyText = BouncyText(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(backColor)
            bt.draw(canvas, paint)
            animator.animate {
                bt.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            bt.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : BouncyTextView {
            val view : BouncyTextView = BouncyTextView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
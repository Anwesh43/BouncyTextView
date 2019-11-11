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
    paint.textSize = size
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

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
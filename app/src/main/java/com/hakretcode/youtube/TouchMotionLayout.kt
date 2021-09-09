package com.hakretcode.youtube

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.constraintlayout.motion.widget.MotionLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.video_content_expanded.view.*
import kotlin.math.abs

class TouchMotionLayout(context: Context, attributeSet: AttributeSet) :
    MotionLayout(context, attributeSet) {

    private var startX: Float = 0f
    private var startY: Float = 0f
    private var isPaused = false

    private lateinit var animFadeIn: AnimatorSet
    private lateinit var animFadeOut: AnimatorSet

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        val isInTarget = touchEventInsideTargetView(video_container, event!!)
        val isInProgress = (progress > 0.0f && progress < 1.0f)

        return if (isInProgress || isInTarget) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    private fun touchEventInsideTargetView(v: View, ev: MotionEvent): Boolean {
        if ((ev.x > v.left && ev.x < v.right) && (ev.y > v.top && ev.y < v.bottom)) return true
        return false
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = ev.x
                startY = ev.y
            }
            MotionEvent.ACTION_UP -> {
                val endX = ev.x
                val endY = ev.y

                if (isAClick(startX, endX, startY, endY)) {
                    if (touchEventInsideTargetView(video_container, ev)) {
                        if (doClick(video_surface)) {
                            return true
                        }
                    }
                }

            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
        val differenceX = abs(startX - endX)
        val differenceY = abs(startY - endY)
        return !(differenceX > 200 || differenceY > 200)
    }

    private fun doClick(view: View): Boolean {
        var isClickHandled = false

        if (progress > 0.05f) {
            isClickHandled = true

            when (view) {
                video_surface -> {
                    if (isPaused) {

                    } else {
                        animateFade {
                            animFadeOut.startDelay = 1000
                            animFadeOut.start()
                        }
                    }
                }
            }
        }

        return isClickHandled
    }

    private fun animateFade(onAnimationEndOn: () -> Unit) {
        animFadeOut = AnimatorSet()
        animFadeIn = AnimatorSet()

        fade(
            animFadeIn, arrayOf(
                cl_controls
            ), true
        )


        /*animFadeIn.play(
            ObjectAnimator.ofFloat(video, View.ALPHA, 0f, .5f)
        )*/

        val valueFadeIn = ValueAnimator.ofInt(0, 255)
            .apply {
                addUpdateListener {
                    seek_bar_video.thumb.mutate().alpha = it.animatedValue as Int
                }
                duration = 200
            }

        animFadeIn.play(valueFadeIn)

        fade(
            animFadeOut, arrayOf(
                cl_controls
            ), false
        )

        val valueFadeOut = ValueAnimator.ofInt(255, 0)
            .apply {
                addUpdateListener {
                    seek_bar_video.thumb.mutate().alpha = it.animatedValue as Int
                }
                duration = 200
            }

        animFadeOut.play(valueFadeOut)

        animFadeIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEndOn()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

        })

        animFadeIn.start()
    }

    private fun fade(animatorSet: AnimatorSet, view: Array<View>, toZero: Boolean) {
        view.forEach {
            animatorSet.play(
                ObjectAnimator.ofFloat(
                    it, View.ALPHA,
                    if (toZero) 0f else 1f,
                    if (toZero) 1f else 0f
                )
            )
        }
    }

}
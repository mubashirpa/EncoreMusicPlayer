package com.encore.music.presentation.ui.widgets

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.widget.LinearLayout
import com.encore.music.R

class AutoHideLinearLayout
    @JvmOverloads
    constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0,
    ) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {
        private var hideDelay: Long = 5000
        private val handler = Handler(Looper.getMainLooper())

        init {
            context.theme
                .obtainStyledAttributes(
                    attrs,
                    R.styleable.AutoHideLinearLayout,
                    defStyleAttr,
                    defStyleRes,
                ).apply {
                    try {
                        hideDelay =
                            getInteger(
                                R.styleable.AutoHideLinearLayout_hideDelay,
                                hideDelay.toInt(),
                            ).toLong()
                    } finally {
                        recycle()
                    }
                }

            post {
                startAutoHide()
            }
        }

        override fun onDetachedFromWindow() {
            super.onDetachedFromWindow()
            handler.removeCallbacksAndMessages(null)
        }

        private fun startAutoHide() {
            handler.postDelayed({
                hide()
            }, hideDelay)
        }

        private fun hide() {
            visibility = GONE
        }

        private fun show() {
            visibility = VISIBLE
        }

        fun setHideDelay(delay: Long) {
            hideDelay = delay
        }

        fun resetAutoHide(newDelay: Long? = null) {
            newDelay?.let {
                hideDelay = it
            }
            show()
            handler.removeCallbacksAndMessages(null)
            startAutoHide()
        }
    }

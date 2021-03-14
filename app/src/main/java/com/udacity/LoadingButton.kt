package com.udacity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = ""
    private var loadingAngle = 0f // 0° to 360° Circle starting point

    private var circleAnimator = ValueAnimator()
    private var buttonAnimator = ValueAnimator()

    //Paint Object for our Button
    private val paintButton = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = resources.getDimension(R.dimen.default_text_size)
        typeface = Typeface.create("", Typeface.BOLD)
    }

    //Paint Object for the circle
    private val paintCircle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        // Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true
        style = Paint.Style.FILL
        color = context.getColor(R.color.colorAccent)
    }

    //Paint Object for Button Text
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = resources.getDimension(R.dimen.text_size_large)
        color = context.getColor(R.color.white)
        color = Color.WHITE
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {
                //Different ways to retrieve the Text
                buttonText = resources.getText(R.string.loading_text).toString()

                circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                        .apply {
                            duration = 3000


                            interpolator = AccelerateInterpolator(1f)
                            addUpdateListener { animation ->
                                animation.repeatCount = ValueAnimator.INFINITE
                                animation.repeatMode = ValueAnimator.REVERSE
                                loadingAngle = animatedValue as Float
                                invalidate()
                            }
                            circleAnimator.start()
                        }
                buttonAnimator = ValueAnimator.ofInt(0, widthSize)
                        .apply {
                            buttonAnimator.duration = 3000
                            repeatMode = ValueAnimator.RESTART
                            repeatCount = 1
                            invalidate()
                        }
                buttonAnimator.start()
            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.download_text)
                circleAnimator.end()
                buttonAnimator.end()
            }
        }
    }

    init {
        //Clickable property that enables the View to accept User Input
        isClickable = true
        buttonText = "Download"
        buttonState = ButtonState.Clicked

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawButtonAndCircle(canvas)
    }

    fun drawButtonAndCircle(canvas: Canvas?) {

        paintButton.color = context.getColor(R.color.colorPrimary)
        //Draw the Rectangle
        canvas!!.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintButton)

        //Draw Text
        paintText.textAlign = Paint.Align.CENTER
        canvas.drawText(
                buttonText,
                widthSize.toFloat() / 2,
                heightSize / 1.7f,
                paintText
        )
        //Drawing dynamic Circle from 0-360f Angle
        canvas.drawArc(
                widthSize - 140f, heightSize / 2 - 40f,
                widthSize - 75f, heightSize / 2 + 40f,
                0f, loadingAngle, true, paintCircle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
                MeasureSpec.getSize(w),
                heightMeasureSpec,
                0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}

package ru.svolf.core_ui

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import com.google.android.material.R
import com.google.android.material.button.MaterialButton
import kotlin.math.roundToInt


class ProgressMaterialButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.materialButtonStyle
) : MaterialButton(context, attrs, defStyleAttr) {

    companion object {
        private const val PROGRESS_SIZE = 24
    }

    private val progressDrawable by lazy {
        val value = TypedValue()
        context.theme.resolveAttribute(android.R.attr.progressBarStyle, value, false)
        val progressBarStyle = value.data
        val attributes = intArrayOf(android.R.attr.indeterminateDrawable)
        val typedArray: TypedArray = context.obtainStyledAttributes(progressBarStyle, attributes)
        val drawable = typedArray.getDrawable(0)
        typedArray.recycle()
        drawable
    }

    private val progressState by lazy {
        HolderState(
            text = null,
            icon = progressDrawable,
            iconSize = (PROGRESS_SIZE * resources.displayMetrics.density).roundToInt(),
            iconGravity = ICON_GRAVITY_TEXT_START,
            iconPadding = 0
        )
    }

    private var normalState: HolderState? = null

    private var currentLoading = false

    fun setProgress(loading: Boolean) {
        if (currentLoading == loading) return
        currentLoading = loading
        if (loading) {
            normalState = createState()
            applyState(progressState)
        } else {
            normalState?.also {
                applyState(it)
            }
        }
        setAnimationProgress(loading)
    }

    private fun setAnimationProgress(enabled: Boolean) {
        val animatable = (progressDrawable as? Animatable) ?: return
        if (enabled) {
            animatable.start()
        } else {
            animatable.stop()
        }
    }

    private fun applyState(state: HolderState) {
        text = state.text
        icon = state.icon
        iconSize = state.iconSize
        iconGravity = state.iconGravity
        iconPadding = state.iconPadding
    }

    private fun createState(): HolderState = HolderState(
        text = text,
        icon = icon,
        iconSize = iconSize,
        iconGravity = iconGravity,
        iconPadding = iconPadding
    )

    private data class HolderState(
        val text: CharSequence?,
        val icon: Drawable?,
        val iconSize: Int,
        val iconGravity: Int,
        val iconPadding: Int
    )
}
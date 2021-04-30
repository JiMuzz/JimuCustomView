package com.jimu.customview.sucktop

import android.content.Context
import android.graphics.*
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.jimu.customview.R
import com.jimu.customview.utils.DisplayUtil


class TaskDecoration(val context: Context, val decorationCallback: DecorationCallback) :
    RecyclerView.ItemDecoration() {

    private var paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var textPaint: Paint = Paint()
    private var mDecorationHeight = 0

    init {
        //高度
        mDecorationHeight = DisplayUtil.dp2px(context, 40f)
        //背景画笔
        paint.color = context.resources.getColor(R.color.background_gray)
        //文字画笔
        textPaint.color = Color.BLACK
        textPaint.typeface = Typeface.DEFAULT_BOLD
        textPaint.isAntiAlias = true
        textPaint.textSize = DisplayUtil.sp2px(context, 16f).toFloat()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val pos = parent.getChildAdapterPosition(view)
        if (isFirstInGroup(pos)) {
            outRect.top = mDecorationHeight
        } else {
            outRect.top = 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)

        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val view = parent.getChildAt(i)
            val position = parent.getChildAdapterPosition(view)
            val title = decorationCallback.getGroupTitle(position)
            if (isFirstInGroup(position)) {
                val top = view.top - mDecorationHeight
                val bottom = view.top
                val fontMetrics = textPaint.fontMetrics
                val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
                c.drawRect(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), paint)
                c.drawText(title, DisplayUtil.sp2px(context, 14f).toFloat(), top + (bottom - top) / 2.toFloat()+distance, textPaint)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        //吸顶效果，需要就打开注释即可

        val itemCount = state.itemCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val view: View = parent.getChildAt(0)
        val position = parent.getChildAdapterPosition(view)
        var groupId = decorationCallback.getGroupTitle(position)


        var top = 0f
        var bottom = mDecorationHeight.toFloat()
        val fontMetrics = textPaint.fontMetrics
        val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom

        //计算顶上去的条件
        if (position + 1 < itemCount) {
            val nextGroupId = decorationCallback.getGroupTitle(position + 1)
            if (nextGroupId != groupId && view.bottom <= mDecorationHeight) {
                //组内最后一个view进入了header
                bottom = view.bottom.toFloat()
                top = bottom - mDecorationHeight
            }
        }

        c.drawRect(
            left.toFloat(),
            top,
            right.toFloat(),
            bottom,
            paint
        )
        c.drawText(
            groupId,
            DisplayUtil.sp2px(context, 14f).toFloat(),
            top + (bottom - top) / 2.toFloat() + distance,
            textPaint
        )
    }

    private fun isFirstInGroup(pos: Int): Boolean {
        return if (pos == 0) {
            true
        } else {
            val lastTitle = decorationCallback.getGroupTitle(pos - 1)
            val title = decorationCallback.getGroupTitle(pos)
            lastTitle != title
        }
    }


    interface DecorationCallback {
        fun getGroupTitle(position: Int): String
    }
}
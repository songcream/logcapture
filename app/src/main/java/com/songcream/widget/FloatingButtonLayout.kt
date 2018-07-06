package com.songcream.widget

import android.animation.ValueAnimator
import android.content.Context
import android.support.design.widget.FloatingActionButton
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import com.songcream.util.SystemUtil

/**
 * Created by gengsong on 2018/6/30.
 */
class FloatingButtonLayout : RelativeLayout{
    val floatingButtonList=ArrayList<FloatingActionButton>()
    val animationList=ArrayList<ValueAnimator>()
    var buttonMargin=50;
    var isShow=false;

    constructor(context: Context?) : super(context){
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
    }

    init{
        post(Runnable {
            floatingButtonList.clear()
            var lastChild:FloatingActionButton?=null;
            buttonMargin=SystemUtil.dip2px(context,70f);
            val childCount=getChildCount()
            for(i in 0..childCount-1){
                val floatingActionButton=getChildAt(i) as FloatingActionButton
                if(i==childCount-1) lastChild=floatingActionButton
                floatingButtonList.add(floatingActionButton)
                (floatingActionButton.layoutParams as RelativeLayout.LayoutParams).addRule(ALIGN_PARENT_BOTTOM)
                val animator=ValueAnimator.ofFloat(0f,-buttonMargin*(childCount-1-i)*1f)
                animator.addUpdateListener {
                    floatingActionButton.translationY=it.animatedValue as Float
                    Log.e("translationY",""+floatingActionButton.translationY)
                };
                animator.setDuration(300)
                animationList.add(animator)
            }
            lastChild?.setOnClickListener({
                animationList.forEachIndexed { index, valueAnimator ->
                    if(isShow){
                        valueAnimator.reverse()
                    }else {
                        valueAnimator.start()
                    }
                }
                isShow=!isShow
            })
        })
    }
}
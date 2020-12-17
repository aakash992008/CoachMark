package com.aakash_solution.coach_mark

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.ContentFrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.contains
import com.aakash_solution.coach_mark.databinding.CoachLayoutBinding


class CoachMarkOverlay {


    private val builder:Builder
    private val context: Context


    private var viewGroup:ViewGroup
    private var targetView: View?=null
    private val listOfViews = ArrayList<CoachMarkData>()
    private var positionCoachMarks=0
    private val mBinding:CoachLayoutBinding
    private var textToSpeech:TextToSpeech?=null

    constructor(context: Context, builder: Builder){
        this.context=context
        this.builder=builder
        this.targetView=builder.getTargetView()
        this.viewGroup=builder.getViewGroup()
        this.textToSpeech=builder.getTextToSpeech()
        positionCoachMarks=0
        mBinding= CoachLayoutBinding.inflate(LayoutInflater.from(context))
        mBinding.bottomTransparent.requestFocus()
        listOfViews.addAll(builder.getTargetViewList())
        if(listOfViews.isEmpty()){
            showCoachMark(viewGroup, targetView!!, "")
        }else{
            showListOfCoachMarks(positionCoachMarks)
        }
    }
    private fun showListOfCoachMarks(position: Int){
        if(position<listOfViews.size) {
            showCoachMark(
                viewGroup,
                listOfViews[position].targetView,
                listOfViews[position].description
            )
        }else{
            viewGroup.removeView(mBinding.root)
        }
    }


    fun restartCoachMark(){
        positionCoachMarks=0
        showListOfCoachMarks(positionCoachMarks)
    }

    private fun hideKeyboard(){
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(viewGroup.windowToken, 0)
    }

    private fun showCoachMark(viewGroup: ViewGroup, targetView: View, description: String){

        if(viewGroup is ConstraintLayout){
            hideKeyboard()
            if(!viewGroup.contains(mBinding.root)){
                viewGroup.addView(mBinding.root)
            }
            generateTransparentView(targetView)
            pointerLocation(targetView)
            pointerDescription(targetView, description)
        }else if(viewGroup is LinearLayoutCompat){
            hideKeyboard()
            if(!viewGroup.contains(mBinding.root)){
                viewGroup.addView(mBinding.root)
            }
            generateTransparentView(targetView)
            pointerLocation(targetView)
            pointerDescription(targetView, description)
        }


        mBinding.nextBtn.setOnClickListener {
            nextCoachMark()
        }
    }

    private fun generateTransparentView(tView: View){
        mBinding.mainLayout.layoutParams= matchParentLayoutParams()
            val parentX=getWidthFromParent(0f,tView)
            val parentY= getHeightFromParent(0f,tView)
            Log.e("COMPARE","Height -> ${parentY+tView.y} ${getHeightFromParent(0f,tView)}  width->   ${parentX+tView.x} ${getWidthFromParent(0f,tView)}  ")
            val constraintSet = ConstraintSet()
            constraintSet.clone(mBinding.mainLayout)
            constraintSet.constrainHeight(R.id.top_transparent, parentY.toInt() + 1)
            constraintSet.constrainWidth(R.id.top_transparent, viewGroup.width)
            constraintSet.connect(
                R.id.top_transparent,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )

            constraintSet.constrainHeight(
                R.id.bottom_transparent, viewGroup.height - (parentY.toInt()+tView.height) + 1
            )
            constraintSet.constrainWidth(R.id.bottom_transparent, viewGroup.width)
            constraintSet.connect(
                R.id.bottom_transparent,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )

            constraintSet.constrainHeight(R.id.right_transparent, tView.height + 1)
            constraintSet.constrainWidth(
                R.id.right_transparent,
                viewGroup.width - (tView.x.toInt() + tView.width) + 1
            )
            constraintSet.connect(
                R.id.right_transparent,
                ConstraintSet.RIGHT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.RIGHT
            )
            constraintSet.connect(
                R.id.right_transparent,
                ConstraintSet.TOP, R.id.top_transparent,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.right_transparent,
                ConstraintSet.BOTTOM, R.id.bottom_transparent,
                ConstraintSet.TOP
            )

            constraintSet.constrainHeight(R.id.left_transparent, tView.height + 1)
            constraintSet.constrainWidth(R.id.left_transparent, (tView.x + 1).toInt())
            constraintSet.connect(
                R.id.left_transparent,
                ConstraintSet.LEFT,
                ConstraintSet.PARENT_ID,
                ConstraintSet.LEFT
            )
            constraintSet.connect(
                R.id.left_transparent,
                ConstraintSet.TOP, R.id.top_transparent,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                R.id.left_transparent,
                ConstraintSet.BOTTOM, R.id.bottom_transparent,
                ConstraintSet.TOP
            )
            constraintSet.applyTo(mBinding.mainLayout)


    }
    private fun pointerLocation(tView: View){
        if(tView.height>tView.width){
            //Vertical View

        }else{
            //Horizontal View
        }

        val parentX=getWidthFromParent(0f,tView)
        val parentY= getHeightFromParent(0f,tView)

        if(parentY+tView.height+(context.resources.getDimension(R.dimen._30sdp))<viewGroup.height){
            mBinding.pointer.setImageResource(R.drawable.hand)
            val middleHeight = parentY+(tView.height/2)
            val middleWidth = parentX+(tView.width/2)
            mBinding.pointer.x=middleWidth
            mBinding.pointer.y=middleHeight
        }else if(parentY-(context.resources.getDimension(R.dimen._30sdp))>10){
            val x = parentX+(tView.width/2)
            val y = parentY-(context.resources.getDimension(R.dimen._20sdp))
            mBinding.pointer.setImageResource(R.drawable.finger_down)
            mBinding.pointer.x=x
            mBinding.pointer.y=y
        }else if(parentX-(context.resources.getDimension(R.dimen._30sdp))>10){
            val x = parentX-(context.resources.getDimension(R.dimen._30sdp))
            val y = parentY+(tView.height/2.toFloat())
            mBinding.pointer.setImageResource(R.drawable.hand_right)
            mBinding.pointer.x=x
            mBinding.pointer.y=y
        }else if(tView.x+tView.width+(context.resources.getDimension(R.dimen._30sdp))<viewGroup.width+10){
            val x = parentX+(tView.width)
            val y = parentY+(tView.height/2.toFloat())
            mBinding.pointer.setImageResource(R.drawable.hand_left)
            mBinding.pointer.x=x
            mBinding.pointer.y=y
        }
        handAnimator(mBinding.pointer)
    }


    private fun  matchParentLayoutParams():ViewGroup.LayoutParams{
        if(viewGroup is ConstraintLayout){
            return ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        }else if(viewGroup is LinearLayoutCompat){
            return LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.MATCH_PARENT)
        }else{
            return ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT)
        }
    }
    private fun pointerDescription(tView: View, description: String){
        val parentX=getWidthFromParent(0f,tView)
        val parentY= getHeightFromParent(0f,tView)
        mBinding.descriptionText.text=description
        var done=false
        mBinding.descriptionCard.viewTreeObserver.addOnGlobalLayoutListener {
            //Checking Bottom
            if(!done){
                if(mBinding.descriptionCard.height<viewGroup.height-(parentY+tView.height)){
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintSet)
                    constraintSet.constrainHeight(R.id.description_card, ConstraintSet.WRAP_CONTENT)
                    constraintSet.constrainWidth(
                        R.id.description_card,
                        ConstraintSet.MATCH_CONSTRAINT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.TOP, R.id.bottom_transparent,
                        ConstraintSet.TOP, 80
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.LEFT, R.id.left_transparent,
                        ConstraintSet.RIGHT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.RIGHT, R.id.right_transparent,
                        ConstraintSet.LEFT
                    )
                    constraintSet.applyTo(mBinding.mainLayout)
                }else if(mBinding.descriptionCard.height<parentY){
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintSet)
                    constraintSet.constrainHeight(R.id.description_card, ConstraintSet.WRAP_CONTENT)
                    constraintSet.constrainWidth(
                        R.id.description_card,
                        ConstraintSet.MATCH_CONSTRAINT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.BOTTOM, R.id.top_transparent,
                        ConstraintSet.BOTTOM, 80
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.LEFT, R.id.left_transparent,
                        ConstraintSet.RIGHT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.RIGHT, R.id.right_transparent,
                        ConstraintSet.LEFT
                    )
                    constraintSet.applyTo(mBinding.mainLayout)
                }else if(parentX>mBinding.descriptionCard.width+10){
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintSet)
                    constraintSet.constrainHeight(R.id.description_card, ConstraintSet.WRAP_CONTENT)
                    constraintSet.constrainWidth(
                        R.id.description_card,
                        ConstraintSet.MATCH_CONSTRAINT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.BOTTOM, R.id.bottom_transparent,
                        ConstraintSet.TOP
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.TOP, R.id.top_transparent,
                        ConstraintSet.BOTTOM
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.LEFT,ConstraintSet.PARENT_ID,
                        ConstraintSet.LEFT
                    )
                    constraintSet.applyTo(mBinding.mainLayout)
                }else if(viewGroup.width-(parentX+tView.width)>mBinding.descriptionCard.width+10){
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintSet)
                    constraintSet.constrainHeight(R.id.description_card, ConstraintSet.WRAP_CONTENT)
                    constraintSet.constrainWidth(
                        R.id.description_card,
                        ConstraintSet.MATCH_CONSTRAINT
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.BOTTOM, R.id.bottom_transparent,
                        ConstraintSet.TOP
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.TOP, R.id.top_transparent,
                        ConstraintSet.BOTTOM
                    )
                    constraintSet.connect(
                        R.id.description_card,
                        ConstraintSet.RIGHT,ConstraintSet.PARENT_ID,
                        ConstraintSet.RIGHT
                    )
                    constraintSet.applyTo(mBinding.mainLayout)
                }
                done=true
            }

        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech?.speak(description, TextToSpeech.QUEUE_FLUSH, null, null)
        }else{
            textToSpeech?.speak(description, TextToSpeech.QUEUE_FLUSH, null)
        }
    }

    private fun nextCoachMark(){
        positionCoachMarks++
        showListOfCoachMarks(positionCoachMarks)
    }
    private fun handAnimator(view: View){
        val xAnimate= ObjectAnimator.ofFloat(view, "scaleX", 0.5f, 1f)
        xAnimate.repeatCount=400
        xAnimate.duration=900
        xAnimate.start()

        val yAnimate= ObjectAnimator.ofFloat(view, "scaleY", 0.5f, 1f)
        yAnimate.repeatCount=400
        yAnimate.duration=900
        yAnimate.start()


    }


    private fun getHeightFromParent(h:Float,view:View):Float{
         var height=h
        if(view == viewGroup){
            return height+view.y
        }else{
            height += view.y + getHeightFromParent(height, view.parent as View)
        }
        return height
    }
    private fun getWidthFromParent(w:Float,view:View):Float{
        var width=w
        if(view == viewGroup){
            return width+view.x
        }else{
            width += view.x + getWidthFromParent(width, view.parent as View)
        }
        return width
    }

    class Builder{
        //For Single CoachMark
        private  var targetView: View?=null
        //For List of CoachMarks
        private val listOfViews = ArrayList<CoachMarkData>()
        //Root View Where the background overlay will be drawn
        private lateinit var viewGroup: ViewGroup
        //Current Context
        private var context: Context
        private var textToSpeech: TextToSpeech?=null
        private  var coachMark:CoachMarkOverlay?=null

        constructor(context: Context){
            this.context=context
        }

        fun getTargetView(): View?= targetView
        fun getViewGroup(): ViewGroup = viewGroup
        fun getTargetViewList():ArrayList<CoachMarkData> = listOfViews
        fun getTextToSpeech(): TextToSpeech? = textToSpeech


        fun setTargetView(targetView: View):Builder{
            this.targetView=targetView
            return this
        }

        fun setTargetViews(targetView: ArrayList<CoachMarkData>):Builder{
            this.listOfViews.clear()
            this.listOfViews.addAll(targetView)
            return this
        }

        fun setTextToSpeech(speech: TextToSpeech):Builder{
            this.textToSpeech=speech
            return this
        }

        fun setViewGroup(viewGroup: ViewGroup):Builder{
            this.viewGroup=viewGroup
            return this
        }

        fun showCoachMark():CoachMarkOverlay{
            if(coachMark==null){
                coachMark = CoachMarkOverlay(context, this)
            }
            return coachMark!!
        }
    }




}
package com.aakash_solution.coachmark

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.databinding.DataBindingUtil
import com.aakash_solution.coach_mark.CoachMarkData
import com.aakash_solution.coach_mark.CoachMarkOverlay
import com.aakash_solution.coachmark.databinding.ActivityPaymentBinding
import java.util.*
import kotlin.collections.ArrayList

class PaymentActivity : AppCompatActivity() {


    private lateinit var mBinding:ActivityPaymentBinding
    private var coachMarkOverlay:CoachMarkOverlay?=null
    private var tts:TextToSpeech?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding=DataBindingUtil.setContentView(this,R.layout.activity_payment)
        tts= TextToSpeech(this) {
            if(it==TextToSpeech.SUCCESS){
                tts?.language = Locale("hi","IN")
                speakText("भुगतान प्रक्रिया में आपका स्वागत है")
            }else{
            }
        }

        mBinding.question.setOnClickListener {
            startCoachMark()
        }


    }


    private fun startCoachMark(){
        if(coachMarkOverlay==null){
            coachMarkOverlay= CoachMarkOverlay.Builder(this)
                .setTargetViews(getListOfCoachMarkData())
                .setViewGroup(mBinding.mainLayout)
                .setTextToSpeech(tts!!)
                .showCoachMark()
        }else{
            coachMarkOverlay?.restartCoachMark()
        }
    }


    private fun speakText(message:String){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(message,TextToSpeech.QUEUE_FLUSH,null,null)
        }else{
            tts?.speak(message,TextToSpeech.QUEUE_FLUSH,null)
        }
    }


    private fun getListOfCoachMarkData():ArrayList<CoachMarkData>{
        return arrayListOf(
            CoachMarkData(mBinding.cardNumber,"16 अंकों का खाता नंबर दर्ज करें"),
            CoachMarkData(mBinding.cardExpiry,"समाप्ति की तारीख दर्ज करें"),
            CoachMarkData(mBinding.cardCvv,"3 अंक cvv नंबर दर्ज करें"),
            CoachMarkData(mBinding.payAmount,"यह राशि डेबिट की जाएगी"),
            CoachMarkData(mBinding.payBtn,"पे बटन दबाएं"))
    }
}
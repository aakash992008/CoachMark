package com.aakash_solution.coachmark

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.Voice
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.aakash_solution.coach_mark.CoachMarkData
import com.aakash_solution.coach_mark.CoachMarkOverlay
import com.aakash_solution.coachmark.databinding.ActivityMainBinding
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

  private lateinit var mBinding:ActivityMainBinding
  private lateinit var textToSpeech:TextToSpeech
  private val listOfVoices:ArrayList<VoiceModel> = ArrayList()
  private var coachMark:CoachMarkOverlay?=null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
    onLanguageSelected()
    seekBarListener()

    textToSpeech= TextToSpeech(this) {status->
      if(status!=TextToSpeech.ERROR){
        textToSpeech.language = Locale("hi","IN")
        speakText("हम आपका स्वागत करते है")
        loadVoice()
      }else{
        Log.e("TTS","Failed to Initialise")
      }
    }

    mBinding.speakButton.setOnClickListener {
      if(!mBinding.etText.text.isNullOrEmpty()){
        speakText(mBinding.etText.text.toString())
      }else{
        speakText("कृपया पाठ दर्ज करें")
      }
    }


    mBinding.pay.setOnClickListener {
      startActivity(Intent(this,PaymentActivity::class.java))
    }


    mBinding.coach.setOnClickListener {
      startCoachMark()
    }

  }
  private fun onLanguageSelected(){
    mBinding.radioGroup.setOnCheckedChangeListener { _, id ->
      when(id){
        R.id.radio_hindi -> textToSpeech.language = Locale("hi","IN")
        R.id.radio_english -> textToSpeech.language = Locale.ENGLISH
        R.id.radio_marathi -> textToSpeech.language = Locale("mr","IN")
        R.id.radio_kannada -> textToSpeech.language = Locale("kn","IN")
        R.id.radio_gujrati -> textToSpeech.language = Locale("gu","IN")
      }
    }
  }



  private fun loadVoice(){
    textToSpeech.voices.forEach {
      if(it.locale.country.contains("IN",true)){
        listOfVoices.add(VoiceModel(it.locale.language,it))
      }
    }
    val adapter =  VoiceAdapter(listOfVoices,object:VoiceAdapter.VoiceSelectedListener{
      override fun onVoiceSelected(voice: Voice) {
        textToSpeech.setVoice(voice)
      }
    })
    mBinding.recyclerView.layoutManager=GridLayoutManager(this,2)
    mBinding.recyclerView.adapter=adapter
  }

  private fun showToast(message:String){
    Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
  }

  private fun seekBarListener(){
    mBinding.seekbar.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
      override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}

      override fun onStartTrackingTouch(p0: SeekBar?) {}

      override fun onStopTrackingTouch(p0: SeekBar?) {
        textToSpeech.setPitch(p0!!.progress.toFloat())
      }

    })
  }


  private fun getListOfCoachMarkData():ArrayList<CoachMarkData>{
    return arrayListOf(CoachMarkData(mBinding.recyclerView,"यहां से आवाज का चयन करें"),
      CoachMarkData(mBinding.radioGroup,"यहां से भाषा का चयन करें"),
      CoachMarkData(mBinding.seekbar,"यहां से पिच का चयन करें"),
      CoachMarkData(mBinding.etText,"यहां अपना टेक्स्ट दर्ज करें"),
      CoachMarkData(mBinding.speakButton,"बटन दबाएं"),
      CoachMarkData(mBinding.coach,"फिर से शुरू करने के लिए"))

  }


  private fun startCoachMark(){
    if(coachMark==null){
      coachMark=CoachMarkOverlay.Builder(this)
        .setTargetViews(getListOfCoachMarkData())
        .setTextToSpeech(textToSpeech)
        .setViewGroup(mBinding.mainLayout).showCoachMark()
    }else{
      coachMark?.restartCoachMark()
    }
  }


  private fun speakText(message:String){
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
      textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,null,null)
    }else{
      textToSpeech.speak(message,TextToSpeech.QUEUE_FLUSH,null)
    }
  }


  override fun onDestroy() {
    super.onDestroy()
    textToSpeech.shutdown()
  }
}
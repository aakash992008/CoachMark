package com.aakash_solution.coachmark

import android.speech.tts.Voice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aakash_solution.coachmark.databinding.VoicesBinding

class VoiceAdapter(private val list:ArrayList<VoiceModel>,private  val listener:VoiceSelectedListener) : RecyclerView.Adapter<VoiceAdapter.VoiceViewHolder>(){






    inner class VoiceViewHolder(val mBinding:VoicesBinding) : RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VoiceViewHolder {
        val mBinding= VoicesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VoiceViewHolder(mBinding)
    }

    override fun onBindViewHolder(holder: VoiceViewHolder, position: Int) {
        holder.mBinding.data=list[position]
        holder.mBinding.callBack=this
    }

    fun onVoiceSelected(voice:Voice){
        listener.onVoiceSelected(voice)
    }

    override fun getItemCount(): Int = list.size


    interface VoiceSelectedListener{
        fun onVoiceSelected(voice: Voice)
    }
}
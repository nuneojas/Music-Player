package com.example.musicplayer;

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayer.databinding.ItemAudioBinding

class MusicAdapter(private var list: List<MusicData>, val itemClick : (MusicData) -> Unit) :
    RecyclerView.Adapter<MusicAdapter.MusicViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAudioBinding.inflate(inflater, parent, false)
        return MusicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MusicViewHolder, position: Int) {
        val phoneRecords = list[position]
        holder.bind(phoneRecords)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(list: List<MusicData>){
        this.list = list
        //refresh adapter
        notifyDataSetChanged()
    }

    inner class MusicViewHolder( val binding: ItemAudioBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MusicData) {
            binding.tvSongName.text = item.songName
            binding.tvArtistName.text = item.songArtist
            binding.root.setOnClickListener {
                itemClick(item)
            }
        }
    }
}

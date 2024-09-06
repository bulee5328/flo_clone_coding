package com.example.flo.ui.song

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.data.entities.Album
import com.example.flo.databinding.ItemSongBinding

class SongRVAdapter(private val albumList: ArrayList<Album>): RecyclerView.Adapter<SongRVAdapter.ViewHolder>() {

    private val nowStatus= SparseBooleanArray()
    interface MyItemClickListener{
        fun onItemClick(album: Album)
        fun onRemoveSong(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener
    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    fun addItem(album: Album){
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int){
        val songId = albumList[position].id //position에 고유 id 부여
        nowStatus.delete(songId)
        albumList.removeAt(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])
        holder.itemView.setOnClickListener{ mItemClickListener.onItemClick(albumList[position])}
        holder.binding.itemSongMoreIv.setOnClickListener { mItemClickListener.onRemoveSong(position) }
    }

    override fun getItemCount(): Int = albumList.size

    inner class ViewHolder(val binding: ItemSongBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(album: Album) {
            binding.itemSongTitleTv.text = album.title
            binding.itemSongSingerTv.text = album.singer
            binding.itemSongImgIv.setImageResource(album.coverImg!!)

            val songId = album.id

            if(nowStatus.get(songId, false)){
                binding.itemSongPlayIv.visibility = View.GONE
                binding.itemSongPauseIv.visibility = View.VISIBLE
            } else{
                binding.itemSongPlayIv.visibility = View.VISIBLE
                binding.itemSongPauseIv.visibility = View.GONE
            }
            binding.itemSongPlayIv.setOnClickListener {
                nowStatus.put(songId, true)
                notifyDataSetChanged()
            }
            binding.itemSongPauseIv.setOnClickListener {
                nowStatus.put(songId, false)
                notifyDataSetChanged()
            }
        }
    }
}
package com.example.flo.ui.main.album

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flo.data.entities.Album
import com.example.flo.databinding.ItemSongBinding

class AlbumLockerRVAdapter (private val albumList:ArrayList<Album>): RecyclerView.Adapter<AlbumLockerRVAdapter.ViewHolder>() {
    private val status=SparseBooleanArray()

    interface MyItemClickListener{
        fun onItemClick(album: Album)
        fun onRemoveAlbum(position: Int)
    }

    private lateinit var mItemClickListener: MyItemClickListener

    fun setMyItemClickListener(itemClickListener: MyItemClickListener){
        mItemClickListener = itemClickListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemSongBinding = ItemSongBinding.inflate(LayoutInflater.from(viewGroup.context),viewGroup,false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(albumList[position])

        holder.itemView.setOnClickListener {
            mItemClickListener.onItemClick(albumList[position])
        }

        holder.binding.itemSongMoreIv.setOnClickListener {
            mItemClickListener.onRemoveAlbum(position)
        }
    }

    override fun getItemCount(): Int = albumList.size

    fun addItem(album: Album) {
        albumList.add(album)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        albumList.removeAt(position)
        status.put(position,false)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemSongBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(album: Album){
            binding.itemSongImgIv.setImageResource(album.coverImg!!)
            binding.itemSongTitleTv.text = album.title
            binding.itemSongSingerTv.text = album.singer

            if (status.get(position, false)) {
                binding.itemSongPlayIv.visibility = View.GONE
                binding.itemSongPauseIv.visibility = View.VISIBLE
            } else {
                binding.itemSongPlayIv.visibility = View.VISIBLE
                binding.itemSongPauseIv.visibility = View.GONE
            }
            binding.itemSongPlayIv.setOnClickListener {
                status.put(position, true)
                notifyDataSetChanged()
            }
            binding.itemSongPauseIv.setOnClickListener {
                status.put(position, false)
                notifyDataSetChanged()
            }
        }
    }

}
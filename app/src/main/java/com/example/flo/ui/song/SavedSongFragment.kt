package com.example.flo.ui.song

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.databinding.FragmentLockerSavedsongBinding
import com.example.flo.ui.main.home.BottomSheetFragment

class SavedSongFragment : Fragment(), BottomSheetFragment.BottomSheetListener {
    lateinit var binding: FragmentLockerSavedsongBinding
    lateinit var songDB: SongDatabase
    var isSelect: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SavedSongRVAdapter()

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId) //클릭리스너 구체화해서 DB도 업데이트
            }
        })

        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

        songRVAdapter.addSongs(songDB.songDao().getLikeSongs(true) as ArrayList<Song>) //좋아요 한 노래 더하기

        binding.lockerSelectAllTv.setOnClickListener {
            setSelectAllLayout(isSelect)
        }
    }

    private fun setSelectAllLayout(isSelect: Boolean) {
        if (!isSelect) {
            val bottomSheetFragment = BottomSheetFragment()
            bottomSheetFragment.setBottomSheetListener(this)  // 리스너 설정
            bottomSheetFragment.show(requireFragmentManager(), "BottomSheetDialog")
            binding.lockerSelectAllTv.text = "선택해제"
            binding.lockerSelectAllTv.setTextColor(Color.parseColor("#0019F4"))
            binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_on)
            this.isSelect = true
        } else {
            binding.lockerSelectAllTv.text = "전체선택"
            binding.lockerSelectAllTv.setTextColor(Color.parseColor("000000"))
            binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_off)
            this.isSelect = false
        }
    }
    override fun onButtonClicked() {
        binding.lockerSelectAllTv.text = "전체선택"
        binding.lockerSelectAllTv.setTextColor(Color.parseColor("#000000"))
        binding.lockerSelectAllImgIv.setImageResource(R.drawable.btn_playlist_select_off)
        this.isSelect = false
    }

}
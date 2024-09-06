package com.example.flo.ui.main.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.ui.main.album.AlbumFragment
import com.example.flo.ui.main.album.AlbumRVAdapter
import com.example.flo.R
import com.example.flo.ui.song.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.remote.SendDataInterface
import com.example.flo.databinding.FragmentHomeBinding
import com.example.flo.ui.main.MainActivity
import com.google.gson.Gson
import java.util.ArrayList
import java.util.Timer
import java.util.TimerTask

class HomeFragment : Fragment(), SendDataInterface {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private lateinit var songDB: SongDatabase

    private val timer = Timer() //timer 객체를 이용해 주기적으로 슬라이드 변경 작업을 수행한다.
    private val handler = Handler(Looper.getMainLooper()) //UI 스레드와 다른 스레드 간에 통신을 위해 사용한다

    override fun sendData(album: Album){
        if(activity is MainActivity){
            val activity = activity as MainActivity
            activity.updateMainPlayerCl(album.id)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums())

        val albumRVAdapter = AlbumRVAdapter(albumDatas) //어댑터와 datalist연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter //recyclerView와 어댑터 연결
        binding.homeTodayMusicAlbumRv.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        //레이아웃 매니저 설정

        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener { //recyclerView item 클릭시 전환
            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayClick(album: Album) {
                sendData(album)
            }

        })

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp)) //리스트 안에 생성한 fragment를 추가
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2)) //인자 값 활용
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter //뷰페이저와 어댑터 연결
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL //방향

        val pannelAdapter = PannelVPAdapter(this) //홈 패널
        binding.homePannelBackgroundVp.adapter = pannelAdapter
        binding.homePannelBackgroundVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        binding.homePannelCircleIndicator.setViewPager(binding.homePannelBackgroundVp)
        //CircleIndicator 적용, setViewPager로 indicator와 viewpager 연결

        autoSlide(pannelAdapter) //자동 슬라이드 사용

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply { //Bundle로 album데이터를 받아서 arguments에 담아 전달
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }

    private fun autoSlide(adapter: PannelVPAdapter) { //홈 프레그먼트 자동 슬라이드 인디케이터
        timer.scheduleAtFixedRate(object : TimerTask() { //scheduleAtFixedRate: 주기적으로 실행할 작업을 지정한다
            override fun run() {
                handler.post {
                    val nextItem = binding.homePannelBackgroundVp.currentItem + 1
                    if (nextItem < adapter.itemCount) {
                        binding.homePannelBackgroundVp.currentItem = nextItem
                    } else {
                        binding.homePannelBackgroundVp.currentItem = 0 // 순환
                    }
                }
            }
        }, 3000, 3000)
    }
}
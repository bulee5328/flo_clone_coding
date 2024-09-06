package com.example.flo.ui.main.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.flo.R
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Like
import com.example.flo.databinding.FragmentAlbumBinding
import com.example.flo.ui.main.MainActivity
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.song.SongDatabase
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson

class AlbumFragment : Fragment() {
    lateinit var binding : FragmentAlbumBinding
    private var gson : Gson = Gson()

    private val information = arrayListOf("수록곡", "상세정보", "영상") //TabLayout 내용

    private var isLiked : Boolean = false

    override fun onCreateView( //프래그먼트 최초 생성시 써야하는 함수
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAlbumBinding.inflate(inflater,container,false)

        val albumJson = arguments?.getString("album") //받아온 arguments 데이터 꺼내기
        val album = gson.fromJson(albumJson, Album::class.java)

        isLiked = isLikedAlbum(album.id) //좋아요 유무 선언
        setInit(album)
        setOnClickListeners(album)


        binding.albumBackIv.setOnClickListener { //뒤로가기 버튼으로 홈 프래그먼트로 돌아가기, 바인딩 활용
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, HomeFragment())
                .commitAllowingStateLoss()
        }

        val albumAdapter = AlbumVPAdapter(this) //albumAdapter 변수 선언해서 뷰페이저와 adapter 연결
        binding.albumContentVp.adapter = albumAdapter
        TabLayoutMediator(binding.albumContentTb, binding.albumContentVp) {
            tab, position ->                        //TabLayoutMediator는 TabLayout과 ViewPager를 연결 시키는 역할
            tab.text = information[position]
        }.attach()                                  //.attach() : TabLayout과 ViewPager를 붙여줌


//        binding.songLalacLayout.setOnClickListener { //토스트 메시지 출력
//            Toast.makeText(activity, "LILAC", Toast.LENGTH_SHORT).show()
//        }   //Toast.makeText(어디서 띄울건지, 문구, 얼마나 띄울건지).show()
//
//        binding.songFluLayout.setOnClickListener { //토스트 메시지 출력
//            Toast.makeText(activity, "Flu", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.songCoinLayout.setOnClickListener { //토스트 메시지 출력
//            Toast.makeText(activity, "Coin", Toast.LENGTH_SHORT).show()
//        }
//
//        binding.songSpringLayout.setOnClickListener { //토스트 메시지 출력
//            Toast.makeText(activity, "Spring", Toast.LENGTH_SHORT).show()
//        }

        return binding.root //fragment_album이라는 xml파일의 뷰들 맘대로 사용한다는 뜻
    }

    private fun setInit(album: Album){
        binding.albumAlbumIv.setImageResource(album.coverImg!!)
        binding.albumMusicTitleTv.text = album.title.toString()
        binding.albumSingerNameTv.text = album.singer.toString()
        if (isLiked){
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun getJwt():Int{ //LoginActivity에 있는 saveJwt에서 jwt값을 받아오는 함수
        val spf = activity?.getSharedPreferences("auth", AppCompatActivity.MODE_PRIVATE) //activity?. : fragment에서 쓸 때 사용
        return spf!!.getInt("jwt",0)
    }

    //앨범에 좋아요를 눌렀을 때 Like 테이블에 정보 추가
    private fun likeAlbum(userId : Int, albumId : Int){
        val songDB = SongDatabase.getInstance(requireContext())!!
        val like = Like(userId,albumId)

        songDB.albumDao().likeAlbum(like)
    }

    //앨범을 눌렀을 때 좋아요 유무 파악
    private fun isLikedAlbum(albumId: Int):Boolean{
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        val likeId : Int? = songDB.albumDao().isLikedAlbum(userId, albumId)

        return likeId != null
    }

    //사용자가 좋아요를 해제하면 Like 테이블에서 데이터 삭제
    private fun disLikedAlbum(albumId: Int){
        val songDB = SongDatabase.getInstance(requireContext())!!
        val userId = getJwt()

        songDB.albumDao().disLikedAlbum(userId, albumId)
    }

    private fun setOnClickListeners(album: Album){ //좋아요 버튼 클릭 함수
        val userId = getJwt()
        binding.albumLikeIv.setOnClickListener {
            if(isLiked){
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_off)
                disLikedAlbum(album.id)
            }else{
                binding.albumLikeIv.setImageResource(R.drawable.ic_my_like_on)
                likeAlbum(userId, album.id)
            }
        }
    }

}
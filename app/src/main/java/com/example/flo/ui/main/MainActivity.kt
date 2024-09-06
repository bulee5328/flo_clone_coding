package com.example.flo.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.R
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
import com.example.flo.ui.song.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var mediaPlayer: MediaPlayer? = null
    private var timer: Timer? = null
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    val albums = arrayListOf<Album>()
    lateinit var songDB: SongDatabase
    lateinit var albumDB: SongDatabase
    private var nowPos = 0

    fun updateMainPlayerCl(albumId: Int){
        startTimer()
        nowPos = getPlayingSongPosition(albumId)
        songs[nowPos].second = 0
        startTimer()
        resetSong()
        playSong()

        binding.mainMiniplayerTitleTv.text=songs[nowPos].title
        binding.mainMiniplayerSingerTv.text=songs[nowPos].singer
        binding.mainProgressSb.progress=0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummySongs()
        inputDummyAlbums()
        initPlayList()
        initBottomNavigation()

        //val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString()
        //    ,0,60,false,"music_lilac")
        // 메인 액티비티에서 Song 데이터클래스의 title,singer에 대한 정보를 song이라는 변수를 선언해서 입력시킴
        // 타입은 Song 데이터클래스
        // 나중에 선언한 gson으로 sharedPreferences에 저장된 값을 가져와서 필요없지만 일단 남겨둠

        binding.mainPlayerCl.setOnClickListener { //mainPlayerCl:액티비티에 있는 미니플레이어를 의미
            //startActivity(Intent(this, SongActivity::class.java)) //플레이어 눌렀을 때 Main -> SongActivity로 전환
            //startActivity() 메소드를 사용해서 액티비티 전환 가능
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId",songs[nowPos].id)

            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
        binding.mainMiniplayerNextBtn.setOnClickListener {
            moveSong(1)
            playSong()
        }
        binding.mainMiniplayerPreviousBtn.setOnClickListener {
            moveSong(-1)
            playSong()
        }
        binding.mainMiniplayerBtn.setOnClickListener {
            playSong()
        }

        initData()
    }

    private fun currentSongStatus(){
        val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
        editor.putInt("songId", songs[nowPos].id)
        editor.putInt("songSec", songs[nowPos].second)
        editor.apply()
    }

    private fun initData() {
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)
        val songSec = spf.getInt("songSec", 0)

        songDB = SongDatabase.getInstance(this)!!
        albumDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
        albums.addAll(albumDB.albumDao().getAlbums())

        songs[nowPos] = if (songId == 0) {
            songDB.songDao().getSong(1)
        } else {
            songDB.songDao().getSong(songId)
        }
        songs[nowPos].second = songSec
        resetSong()
        mediaPlayer?.seekTo(songs[nowPos].second * 1000)

        setMiniPlayer(songs[nowPos])
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun startTimer() {
        timer?.interrupt()
        timer = Timer(songs[nowPos].playTime, songs[nowPos].second, songs[nowPos].isPlaying)
        timer?.start()
    }

    private fun setMiniPlayer(song: Song) { //미니플레이어에 song 데이터를 실제로 반영해주는 함수
        binding.mainMiniplayerTitleTv.text = song.title
        binding.mainMiniplayerSingerTv.text = song.singer

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val second = sharedPreferences.getInt("second",0)

        binding.mainProgressSb.progress = (second*100000)/song.playTime
    }


    private fun initPlayList(){ //songs에 songDB저장
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())
    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0) {
            Toast.makeText(this, "first song", Toast.LENGTH_SHORT).show()
            return
        }
        if (nowPos + direct >= songs.size) {
            Toast.makeText(this, "last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        songs[nowPos].second = 0
        timer?.interrupt()
        startTimer()
        resetSong()

        binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
        binding.mainMiniplayerTitleTv.text = songs[nowPos].title
        binding.mainMiniplayerSingerTv.text = songs[nowPos].singer
        binding.mainProgressSb.progress = 0
    }

    override fun onPause() {
        super.onPause()

        binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
        songs[nowPos].isPlaying = false
        timer?.isPlaying = false
        mediaPlayer?.pause()

        //DB에 second 값을 올리는 코드 작성
        val songDB = SongDatabase.getInstance(this)!!
        val song = songs[nowPos]

        val second = (binding.mainProgressSb.progress * songs[nowPos].playTime / 100)/1000 //초 단위로 저장
        songDB.songDao().updateSecondById(second, song.id)

        currentSongStatus()
    }

    override fun onResume() {
        super.onResume()

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)

        setMiniPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for(i in 0 until songs.size){
            if(songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.interrupt()
        mediaPlayer?.pause()

        currentSongStatus()
    }

    private fun playSong() {
        if (!songs[nowPos].isPlaying) {
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplay_pause)
            songs[nowPos].isPlaying = true
            mediaPlayer?.start()
            timer?.isPlaying = true
        } else {
            binding.mainMiniplayerBtn.setImageResource(R.drawable.btn_miniplayer_play)
            songs[nowPos].isPlaying = false
            mediaPlayer?.pause()
            timer?.isPlaying = false
        }
    }

    private fun resetSong() {
        mediaPlayer?.reset()
        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        mediaPlayer?.seekTo(songs[nowPos].second * 1000)
    }

    private fun inputDummySongs(){ //데이터값이 존재하면 종료, 비어있으면 더미데이터 추가
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if(songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                1
            )
        )
        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                2
            )
        )
        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                230,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )
        songDB.songDao().insert(
            Song(
                "Boy With Luv",
                "방탄소년단 (BTS)",
                0,
                230,
                false,
                "music_boy",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )
        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )
        songDB.songDao().insert(
            Song(
                "Weekend",
                "태연 (Tae Yeon)",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp6,
                false,
                6
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }

    private fun inputDummyAlbums() {
        songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                1,
                "Butter",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp,
            )
        )
        songDB.albumDao().insert(
            Album(
                2,
                "IU 5th Album 'LILAC'",
                "아이유 (IU)", R.drawable.img_album_exp2
            )
        )
        songDB.albumDao().insert(
            Album(
                3,
                "iScreaM Vol.10 : Next Level Remixes",
                "에스파 (aespa)",
                R.drawable.img_album_exp3,
            )
        )
        songDB.albumDao().insert(
            Album(
                4,
                "MAP OF THE SOUL : PERSONA",
                "방탄소년단 (BTS)",
                R.drawable.img_album_exp4,
            )
        )
        songDB.albumDao().insert(
            Album(
                5,
                "GREAT!",
                "모모랜드 (MomoLand)",
                R.drawable.img_album_exp5,
            )
        )
        songDB.albumDao().insert(
            Album(
                6,
                "Weekend",
                "태연 (TaeYeon)",
                R.drawable.img_album_exp6,
            )
        )
        val _albums = songDB.albumDao().getAlbums()
        Log.d("album data", _albums.toString())
    }

    inner class Timer(private val playTime: Int, private var second: Int, var isPlaying: Boolean = true) : Thread() {
        private var mills: Float = (second * 1000).toFloat()

        override fun run() {
            super.run()
            try {
                while (true) {
                    if (songs[nowPos].second >= playTime) {
                        songs[nowPos].second = 0
                        mills = 0f
                        resetSong()
                        runOnUiThread {
                            playSong()
                        }
                    }
                    if (isPlaying) {
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.mainProgressSb.progress = ((mills / playTime) * 100).toInt()
                        }
                        if (mills % 1000 == 0f) {
                            songs[nowPos].second++
                        }
                    }
                }
            } catch (e: InterruptedException) {
                Log.d("Song", "쓰레드가 죽었습니다 ${e.message}")
            }
        }
    }

}
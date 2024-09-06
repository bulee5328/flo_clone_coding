package com.example.flo.ui.song

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.databinding.ActivitySongBinding
import com.example.flo.ui.main.home.CustomSnackbar
import com.google.gson.Gson

class SongActivity : AppCompatActivity() { //AppCompatActivity 클래스는 안드로이드에서 액티비티의 기능을 사용할 수 있도록 만듬
    //전역 변수
    lateinit var binding : ActivitySongBinding /*바인딩 선언, ActivitySongBinding이라는 타입을 작성해서
                                                            activity_song이라는 xml파일 연결*/
    //lateinit : 초기화 나중에 할 것이라는 뜻
    lateinit var timer : Timer
    private var mediaPlayer: MediaPlayer?= null //?는 null값이 들어올 수 있다는 뜻
    private var gson : Gson = Gson() //gson 선언

    val songs = arrayListOf<Song>() //songs로 노래 관리
    lateinit var songDB: SongDatabase //데이터베이스 전역변수 초기화
    var nowPos = 0

    override fun onCreate(savedInstanceState: Bundle?) { //액티비티 처음 생성됬을 때 무조건 써야하는 함수 onCreate()
        super.onCreate(savedInstanceState)
        binding = ActivitySongBinding.inflate(layoutInflater) //바인딩 값 초기화(inflate는 메모리 값 할당)
        setContentView(binding.root) //activity_song이라는 xml파일의 뷰들 맘대로 사용한다는 뜻

        initPlayList()
        initSong() //initSong 함수 실행
        initClickListener()

        //if (intent.hasExtra("title") && intent.hasExtra("singer")) { //title,singer 값이 intent에 존재한다면
        //    binding.songMusicTitleTv.text = intent.getStringExtra("title") //SongActivity 액티비티의 해당 텍스트 값은
        //    //intent의 title이라는 키 값을 갖는 문자로 바뀐다
        //    binding.songSingerNameTv.text = intent.getStringExtra("singer")
        //}
    }

    //SongDatabase에 저장되어 있는 song 리스트를 뽑아와서 songs에 저장
    private fun initPlayList(){
        songDB = SongDatabase.getInstance(this)!!
        songs.addAll(songDB.songDao().getSongs())

    }

    private fun initClickListener(){
        binding.songDownIb.setOnClickListener { // setOnClickListener는 버튼 클릭 시 수행할 동작을 지정
            finish() //startActivity()로 띄워진 액티비티가 사라지게 하는 역할
        }
        binding.songMiniplayerIv.setOnClickListener { //만든 함수 사용
            setPlayerStatus(true)
        }
        binding.songPauseIv.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.songRepeatIv.setOnClickListener {
            setRepeatStatus(false)
        }
        binding.songRepeatBackgroundIv.setOnClickListener {
            setRepeatStatus(true)
        }
        binding.songRandomIv.setOnClickListener {
            setRandomStatus(false)
        }
        binding.songRandomBackgroundIv.setOnClickListener {
            setRandomStatus(true)
        }

        binding.songNextIv.setOnClickListener {
            moveSong(+1)
        }

        binding.songPreviousIv.setOnClickListener {
            moveSong(-1)
        }

        binding.songLikeIv.setOnClickListener {
            setLike(songs[nowPos].isLike)
            CustomSnackbar.make(binding.root, "좋아요 한 곡에 담겼습니다.").show()
        }
    }

    private fun initSong(){ //SharedPreferences에서 id값을 받아오기
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId", 0)

        nowPos = getPlayingSongPosition(songId) //현재 보여지는 song이 songs[nowPos]

        startTimer()
        setPlayer(songs[nowPos]) //데이터 렌더링
    }

    private fun setLike(isLike: Boolean){
        songs[nowPos].isLike = !isLike
        songDB.songDao().updateIsLikeById(!isLike,songs[nowPos].id)

        if(!isLike){
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }
    }

    private fun moveSong(direct: Int){
        if(nowPos + direct < 0){
            CustomSnackbar.make(binding.root, "처음 곡입니다.").show()
            return
        }
        if(nowPos + direct >= songs.size){
            CustomSnackbar.make(binding.root, "마지막 곡입니다").show()
            return
        }

        nowPos += direct

        timer.interrupt()
        resetSong()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{ //SharedPreferences에서 받아온 id값을 songs와 비교해서 index값을 구함
        for(i in 0 until songs.size){
            if(songs[i].id == songId){
                return i
            }
        }
        return 0
    }

    private fun setPlayer(song: Song){ //Song 데이터값을 플레이어로 가져옴(데이터 렌더링 역할)
        binding.songMusicTitleTv.text = song.title
        binding.songSingerNameTv.text = song.singer
        binding.songStartTimeTv.text = String.format("%02d:%02d",song.second / 60, song.second % 60)
        binding.songEndTimeTv.text = String.format("%02d:%02d",song.playTime / 60, song.playTime % 60)
        binding.songProgressSb.progress = (song.second * 100000 / song.playTime)
        binding.songAlbumIv.setImageResource(song.albumImg!!)

        val music = resources.getIdentifier(song.music, "raw", this.packageName) //music 파일 받아오기
        mediaPlayer = MediaPlayer.create(this, music) //mediaPlayer에게 music 할당하기

        if (mediaPlayer == null) resetSong()
        else {
            if (song.second != 0) mediaPlayer?.seekTo(song.second * 1000)
        }

        if(song.isLike){ //song데이터에 따라 좋아요 버튼 변경
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_on)
        }else{
            binding.songLikeIv.setImageResource(R.drawable.ic_my_like_off)
        }

        startTimer()

        setPlayerStatus(song.isPlaying) //플레이어 상태 변경
    }


    fun setPlayerStatus(isPlaying : Boolean) { //재생 버튼 전환 함수
        songs[nowPos].isPlaying = isPlaying //재생 버튼 누를 때마다 seekbar, timer 시간 변경
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.songMiniplayerIv.visibility = View.GONE
            binding.songPauseIv.visibility = View.VISIBLE
            mediaPlayer?.start() //mediaPlayer 재생
        }
        else{
            binding.songMiniplayerIv.visibility = View.VISIBLE
            binding.songPauseIv.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){ //mediaPlayer 정지, if문 안쓰면 오류 발생
                mediaPlayer?.pause() //mediaPlayer 일시중지
            }
        }
    }

    fun setRepeatStatus(isRepeat : Boolean) { //반복재생 버튼 전환 함수
        if(isRepeat) {
            binding.songRepeatIv.visibility = View.VISIBLE
            binding.songRepeatBackgroundIv.visibility = View.GONE
        }
        else {
            binding.songRepeatIv.visibility = View.GONE
            binding.songRepeatBackgroundIv.visibility = View.VISIBLE
            mediaPlayer!!.isLooping = true
        }
    }

    fun setRandomStatus(isRandom : Boolean) { //랜덤재생 버튼 전환 함수
        if(isRandom) {
            binding.songRandomIv.visibility = View.VISIBLE
            binding.songRandomBackgroundIv.visibility = View.GONE
        }
        else {
            binding.songRandomIv.visibility = View.GONE
            binding.songRandomBackgroundIv.visibility = View.VISIBLE
        }
    }

    private fun startTimer(){ //timer 객체 생성과 시작 동시에
        timer = Timer(songs[nowPos].playTime, songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime : Int, var isPlaying: Boolean = true): Thread(){ //Timer 내부 클래스

        private var second : Int = songs[nowPos].second
        private var mills: Float = second.toFloat() * 1000


        override fun run() { //seekBar 구현, run 함수 내부의 코드가 종료되면 자동으로 Thread 종료
            super.run()
            try { //오류 발생 처리
                while(true){
                    if(second >= playTime) {
                        second = 0
                        mills = 0f
                        resetSong() //resetSong 함수 호출
                        if(binding.songRepeatIv.visibility == View.VISIBLE) {
                            runOnUiThread{ //뷰를 렌더링하는 것은 handler,runOnUiThread 사용(Visible,Gone)
                                setPlayerStatus(false)
                            }
                        }
                        else {
                            runOnUiThread {
                                setPlayerStatus(true)
                            }
                        }
                    }

                    if(isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.songProgressSb.progress = ((mills / playTime)*100).toInt()
                        }
                        if(mills % 1000 == 0f) { //진행하는 시간표시 타이머
                            runOnUiThread {
                                binding.songStartTimeTv.text = String.format("%02d:%02d",second/60,second%60)
                            }
                            second++
                        }
                    }
                }
            }catch(e: InterruptedException){ //오류발생 했을 때
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }
        }

    }
    // 사용자가 포커스를 잃었을 때 음악이 중지
    override fun onPause() {
        super.onPause()
        songs[nowPos].second = ((binding.songProgressSb.progress * songs[nowPos].playTime)/100)/1000 //현재 노래 재생시간
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE) //앱 종료되도 재생시간 정보 저장
        val editor = sharedPreferences.edit() //sharedPreferences를 사용하기 위해서는 editor 필요

        editor.putInt("songId",songs[nowPos].id) //editor에 id값 넣기
        editor.putInt("second", songs[nowPos].second)

        editor.apply() //editor에 실제 적용, 꼭 써줘야 함
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt() //interrupt() : InterruptedException 오류를 발생시킴, 쓰레드 종료
        mediaPlayer?.release() // 미디어 플레이어가 갖고 있던 리소스 해제, 불필요한 데이터 낭비 방지
        mediaPlayer = null // 미디어 플레이어 해제
    }

    private fun resetSong() {
        mediaPlayer?.reset()
        val music = resources.getIdentifier(songs[nowPos].music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this,music)
    }


}
package com.example.flo.ui.main.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class BannerVPAdapter(fragment:Fragment) : FragmentStateAdapter(fragment) {
    //Adapter는 인자를 써줘야함, 뷰페이저에 여러 fragment를 넣어 슬라이드,
    //FragmentStateAdapter(fragment)를 상속받음

    private val fragmentlist : ArrayList<Fragment> = ArrayList() //배열에 Fragment만 넣음

    override fun getItemCount(): Int { //뷰페이저에 데이터 몇 개를 전달할지
        return fragmentlist.size
    }

    override fun createFragment(position: Int): Fragment { //fragmentlist안에 속해있는 fragment를 만드는 함수
        return fragmentlist[position]
    }

    fun addFragment(fragment: Fragment) { //새로운 fragment를 더함
        fragmentlist.add(fragment)
        notifyItemInserted(fragmentlist.size -1) //리스트에 새로운 값이 추가될 때 뷰페이저에 알리고 출력
    }

}
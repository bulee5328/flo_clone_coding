package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentPannelthreeBinding

class PannelThreeFragment : Fragment() {

    lateinit var binding : FragmentPannelthreeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPannelthreeBinding.inflate(inflater, container, false)

        return binding.root
    }

}
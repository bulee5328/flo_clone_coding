package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentPanneltwoBinding

class PannelTwoFragment : Fragment() {

    lateinit var binding : FragmentPanneltwoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPanneltwoBinding.inflate(inflater, container, false)

        return binding.root
    }

}
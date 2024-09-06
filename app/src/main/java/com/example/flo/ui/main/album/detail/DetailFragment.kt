package com.example.flo.ui.main.album.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.flo.databinding.FragmentDatailBinding

class DetailFragment : Fragment() {

    lateinit var binding: FragmentDatailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDatailBinding.inflate(inflater,container,false)

        return binding.root
    }
}
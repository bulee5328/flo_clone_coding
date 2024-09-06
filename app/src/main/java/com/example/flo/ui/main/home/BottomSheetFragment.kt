package com.example.flo.ui.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.flo.databinding.FragmentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetFragment: BottomSheetDialogFragment() {
    lateinit var binding: FragmentBottomSheetBinding
    private var listener: BottomSheetListener?=null

    interface BottomSheetListener {
        fun onButtonClicked()
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        this.listener = listener
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding=FragmentBottomSheetBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bottomSheetIv1.setOnClickListener {
            Toast.makeText(requireActivity(), "듣기 버튼 클릭", Toast.LENGTH_SHORT).show()
        }
        binding.bottomSheetIv2.setOnClickListener {
            Toast.makeText(requireActivity(), "재생목록 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSheetIv3.setOnClickListener {
            Toast.makeText(requireActivity(), "내 리스트 버튼 클릭", Toast.LENGTH_SHORT).show()
        }

        binding.bottomSheetIv4.setOnClickListener {
            listener?.onButtonClicked()
            dismiss()
        }
    }
}
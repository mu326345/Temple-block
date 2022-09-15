package com.example.templeblock

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import com.example.templeblock.databinding.FragmentHomeBinding
import com.example.templeblock.databinding.PopupWindowBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var soundpool: SoundPool
    private var sound: Int = -1
    private lateinit var popupWindow: PopupWindow
    private val viewModel : HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        initSound()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentSound.observe(viewLifecycleOwner) {
            initSound(it)
        }

        binding.soundSelect.setOnClickListener {
            selectSound(it)
        }

        binding.blockImg.setOnTouchListener { view, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    clickOne()
                    playSound()
                    popupWindow.dismiss()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    clickTwo()
                    true
                }
                else -> false
            }
        }
    }

    private fun clickOne() {
        binding.blockImg.visibility = View.INVISIBLE
        binding.blockSmallImg.visibility = View.VISIBLE
    }

    private fun clickTwo() {
        binding.blockSmallImg.visibility = View.INVISIBLE
        binding.blockImg.visibility = View.VISIBLE
    }

    private fun initSound(soundId: Int? = R.raw.sound1) {
        soundpool = SoundPool.Builder()
            .setMaxStreams(30)
            .build()
        soundId?.let {
            sound = soundpool.load(requireContext(), it, 1)
        }
    }

    private fun playSound() {
            soundpool.play(sound, 1.0f, 1.0f, 0, 0,  1.0f)
    }

    private fun selectSound(v: View) {
        val binding = PopupWindowBinding.inflate(LayoutInflater.from(requireContext()),null, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        popupWindow = PopupWindow(binding.root).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            showAsDropDown(v)
            isTouchable = true
            contentView.setOnClickListener {
                dismiss()
            }
        }
    }
}
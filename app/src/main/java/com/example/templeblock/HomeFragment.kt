package com.example.templeblock

import android.annotation.SuppressLint
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.templeblock.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var soundpool: SoundPool
    private var sound: Int = -1
    private lateinit var popupWindow: PopupWindow

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

    private fun initSound() {
        soundpool = SoundPool.Builder()
            .setMaxStreams(30)
            .build()
        sound = soundpool.load(requireContext(), R.raw.sound1, 1)
    }

    private fun playSound() {
            soundpool.play(sound, 1.0f, 1.0f, 0, 0,  1.0f)
    }

    private fun selectSound(v: View) {
        val popupView = LayoutInflater.from(requireContext()).inflate(R.layout.popup_window, null)
        popupWindow = PopupWindow(popupView).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            showAsDropDown(v)
//            setBackgroundDrawable(ColorDrawable(0x00000000))
            isTouchable = true
            contentView.setOnClickListener {
                dismiss()
            }
        }
    }
}
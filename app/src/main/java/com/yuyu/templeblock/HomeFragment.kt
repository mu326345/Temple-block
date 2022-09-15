package com.yuyu.templeblock

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.preference.PreferenceManager
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.PopupWindow
import androidx.fragment.app.viewModels
import com.yuyu.templeblock.databinding.FragmentHomeBinding
import com.yuyu.templeblock.databinding.PopupWindowBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var soundpool: SoundPool
    private var sound: Int = -1
    private lateinit var popupWindow: PopupWindow
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var handler: Handler
    private lateinit var mainHandler: Handler
    private var loop: Boolean = false
    private var isInit: Boolean = false
    private lateinit var pref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        val handlerThread = HandlerThread("soundThread").apply {
            start()
        }
        handler = Handler(handlerThread.looper)
        mainHandler = Handler(Looper.getMainLooper())
        pref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        loop = pref.getBoolean(PREF_LOOP, false)
        initPopupWindow()

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loopStop.setOnClickListener {
            it.visibility = View.GONE
            loop = true
            pref.edit().putBoolean(PREF_LOOP, loop).apply()
            with(popupWindow) {
                if(isShowing) {
                    dismiss()
                }
            }
        }

        binding.loop.setOnClickListener {
            binding.loopStop.visibility = View.VISIBLE
            loop = false
            pref.edit().putBoolean(PREF_LOOP, loop).apply()
            with(popupWindow) {
                if(isShowing) {
                    dismiss()
                }
            }
        }

        viewModel.currentSound.observe(viewLifecycleOwner) {
            with(popupWindow) {
                if(isShowing) {
                    dismiss()
                }
            }
            // init handler
            if (isInit) {
                initSound(it.rawId)
                // sotre preference
                pref.edit().putInt(PREF_SOUND, it.index).apply()
            } else {
                val temp = pref.getInt(PREF_SOUND, SoundType.SOUND1.index)
                val type =SoundType.values().get(temp-1)
                initSound(type.rawId)
                // after init sound, we start handler infinite loop
                handler.post {
                    while (true) {
                        if (loop) {
                            mainHandler.post {
                                clickScaleDown()
                            }
                            mainHandler.postDelayed({
                                clickScaleUp()
                            }, 200)
                            playSound()
                        }
                        Thread.sleep(500)
                    }
                }
                viewModel.setId(type)
                isInit = true
            }
        }

        binding.soundSelect.setOnClickListener {
            popupWindow.showAsDropDown(it)
        }

        binding.blockImg.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    clickScaleDown()
                    playSound()
                    popupWindow.dismiss()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    clickScaleUp()
                    true
                }
                else -> false
            }
        }
    }

    private fun clickScaleDown() {
        binding.blockImg.visibility = View.INVISIBLE
        binding.blockSmallImg.visibility = View.VISIBLE
    }

    private fun clickScaleUp() {
        binding.blockSmallImg.visibility = View.INVISIBLE
        binding.blockImg.visibility = View.VISIBLE
    }

    private fun initSound(soundId: Int) {
        soundpool = SoundPool.Builder()
            .setMaxStreams(30)
            .build()
        sound = soundpool.load(requireContext(), soundId, 1)
    }

    private fun playSound() {
        soundpool.play(sound, 1.0f, 1.0f, 0, 0, 1.0f)
    }

    private fun initPopupWindow() {
        val binding = PopupWindowBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        popupWindow = PopupWindow(binding.root).apply {
            width = ViewGroup.LayoutParams.WRAP_CONTENT
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            isTouchable = true
            contentView.setOnClickListener {
                dismiss()
            }
        }
    }

    companion object {
        const val PREF_LOOP = "loop"
        const val PREF_SOUND = "sound"
    }
}
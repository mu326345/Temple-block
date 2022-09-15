package com.example.templeblock

import android.view.View
import android.widget.RadioButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _currentSound = MutableLiveData(SoundType.SOUND1.id)
    val currentSound : LiveData<Int>
    get() = _currentSound

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                SoundType.SOUND1.viewId -> {
                    if(checked) {
                        _currentSound.value = SoundType.SOUND1.id
                    }
                }
                SoundType.SOUND2.viewId -> {
                    if(checked) {
                        _currentSound.value = SoundType.SOUND2.id
                    }
                }
                SoundType.SOUND3.viewId -> {
                    if(checked) {
                        _currentSound.value = SoundType.SOUND3.id
                    }
                }
//                SoundType.SOUND4.viewId -> {
//                    if(checked) {
//                        _currentSound.value = SoundType.SOUND4.id
//                    }
//                }
            }
        }
    }
}
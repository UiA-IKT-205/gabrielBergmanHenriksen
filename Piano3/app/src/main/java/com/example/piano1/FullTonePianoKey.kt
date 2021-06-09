package com.example.piano1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.example.piano1.databinding.FragmentFullTonePianoKeyBinding
import kotlinx.android.synthetic.main.fragment_full_tone_piano_key.view.*

class FullTonePianoKey : Fragment() {

    private lateinit var note:String
    private var _binding:FragmentFullTonePianoKeyBinding? = null
    private val binding get() = _binding!!

    var onWhiteKeyDown:((note:String) -> Unit)? = null
    var onWhiteKeyUp:((not:String) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getString("NOTE") ?: "?"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFullTonePianoKeyBinding.inflate(layoutInflater)
        val view = binding.root

        view.fullToneKeyBt.setOnTouchListener(object: View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean{
                when(event?.action){
                    MotionEvent.ACTION_DOWN -> this@FullTonePianoKey.onWhiteKeyDown?.invoke(note)
                    MotionEvent.ACTION_UP -> this@FullTonePianoKey.onWhiteKeyUp?.invoke(note)
                }
                return true
            }
        })
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(note: String) =
            FullTonePianoKey().apply {
                arguments = Bundle().apply {
                    putString("NOTE", note)
                }
            }
    }
}
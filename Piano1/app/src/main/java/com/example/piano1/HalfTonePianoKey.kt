package com.example.piano1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.piano1.databinding.FragmentHalfTonePianoKeyBinding

class HalfTonePianoKey : Fragment() {

    private lateinit var note:String
    private var _binding: FragmentHalfTonePianoKeyBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            note = it.getString("NOTE") ?: "?"
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_half_tone_piano_key, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(note: String) =
            HalfTonePianoKey().apply {
                arguments = Bundle().apply {
                    putString("NOTE", note)
                }
            }
    }
}
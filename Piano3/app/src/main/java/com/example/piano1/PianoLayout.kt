package com.example.piano1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.piano1.databinding.FragmentPianoBinding
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_piano.view.*


class PianoLayout : Fragment() {

    private var _binding: FragmentPianoBinding? = null
    private val binding get() = _binding!!
    private val fullTones = listOf("C","D","E","F","G","A","B","C2","D2","E2","F2","G2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPianoBinding.inflate(layoutInflater)
        val view = binding.root

        val fm = childFragmentManager
        val ft = fm.beginTransaction()

        fullTones.forEach {
            val fullTonePianoKey = FullTonePianoKey.newInstance(it)
            fullTonePianoKey.onWhiteKeyDown = { println("Piano key down, note $it") }
            fullTonePianoKey.onWhiteKeyUp = { println("Piano key up, note $it") }
            ft.add(view.FullToneKeyLinearLayout.id, fullTonePianoKey, "note_$it")

        }

        ft.commit()

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_piano, container, false)
        return view
    }

    /*companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PianoLayout().apply {

            }
    }*/
}
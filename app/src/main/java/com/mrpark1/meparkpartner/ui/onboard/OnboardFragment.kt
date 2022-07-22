package com.mrpark1.meparkpartner.ui.onboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class OnboardFragment : Fragment() {
    //온보딩에서 사용하는 평범한 Fragment

    private var layoutId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null && args.containsKey(KEY_LAYOUT_ID)) layoutId = args.getInt(KEY_LAYOUT_ID)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(layoutId, container, false)
    }

    companion object {
        private const val KEY_LAYOUT_ID = "layoutId"

        @JvmStatic
        fun newInstance(layoutId: Int): OnboardFragment {
            val fragment = OnboardFragment()

            val args = Bundle()
            args.putInt(KEY_LAYOUT_ID, layoutId)
            fragment.arguments = args
            return fragment
        }
    }
}
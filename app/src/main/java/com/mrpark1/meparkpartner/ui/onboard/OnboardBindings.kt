package com.mrpark1.meparkpartner.ui.onboard

import androidx.databinding.BindingAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

//온보딩 화면의 indicator(점 세개) 구현
@BindingAdapter("bindTabLayoutMediator")
fun bindTabMediator(tabLayout: TabLayout, pager: ViewPager2) {
    if (pager.adapter == null) return
    TabLayoutMediator(tabLayout, pager) { tab: TabLayout.Tab, _: Int ->
        tab.view.run {
            isClickable = false
            isFocusable = false
        }
    }.attach()
}

//ViewPager 콜백 구현
@BindingAdapter("bindViewPagerCallback")
fun bindPagerCallback(pager: ViewPager2, callback: ViewPager2.OnPageChangeCallback) {
    pager.registerOnPageChangeCallback(callback)
}
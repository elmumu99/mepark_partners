package com.mrpark1.meparkpartner.ui.onboard

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.databinding.ActivityOnboardBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.util.SharedPrefUtil
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnboardActivity : BaseActivity<ActivityOnboardBinding>(R.layout.activity_onboard) {
    //온보딩 액티비티

    @Inject
    lateinit var sharedPrefUtil: SharedPrefUtil

    private val viewModel: OnboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //보여줄 Fragment와 ViewPager 초기화
        val fragments = initOnboardFragments()
        binding.maxPage = fragments.size
        binding.vpOnboard.adapter = ViewPagerAdapter(this, fragments)
        binding.vpOnboard.offscreenPageLimit = fragments.size - 1

        binding.btOnboardStart.setOnClickListener { finish() }
    }

    private fun initOnboardFragments(): List<Fragment> {
        return listOf(
            OnboardFragment.newInstance(
                R.layout.fragment_onboard_1
            ),
            OnboardFragment.newInstance(
                R.layout.fragment_onboard_2
            ),
            OnboardFragment.newInstance(
                R.layout.fragment_onboard_3
            ),
        )
    }

    //뒤로가기 시 이전 온보딩 페이지로
    override fun onBackPressed() {
        val page = viewModel.currentPage.value!!
        if (page == 0) super.onBackPressed()
        else binding.vpOnboard.currentItem = page - 1
    }

    //온보딩 끝날때 최초 실행 여부 저장
    override fun finish() {
        sharedPrefUtil.put(SharedPrefUtil.KEY_FIRST_LAUNCH, false)
        super.finish()
    }
}
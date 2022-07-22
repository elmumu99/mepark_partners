package com.mrpark1.meparkpartner.ui.charge

import android.annotation.SuppressLint
import android.content.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.mrpark1.meparkpartner.R
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.databinding.ActivityFillPointBinding
import com.mrpark1.meparkpartner.ui.common.BaseActivity
import com.mrpark1.meparkpartner.ui.dialogs.CommonDialog
import com.mrpark1.meparkpartner.ui.dialogs.spinner.BottomSheetSpinner
import com.mrpark1.meparkpartner.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URISyntaxException
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class FillPointActivity : BaseActivity<ActivityFillPointBinding>(R.layout.activity_fill_point) {
    //예치금 충전 액티비티입니다. 이니시스가 좀 유별나서 로직이 복잡합니다.

    private lateinit var client: WebViewClient
    private lateinit var chromeClient: WebChromeClient

    private var paymentType = 0 //0: 신용카드, 1: 가상계좌

    private val viewModel: FillPointViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.viewModel = viewModel

        //주차장 intent로 가져오기
        val parkingLot = intent.getSerializableExtra("parkingLot") as ParkingLot

        //주문번호 생성 (결제시간 + 주차장 번호)
        viewModel.parkingLot = parkingLot
        viewModel.orderKey = SimpleDateFormat(
            "yyyyMMddHHmmss",
            Locale.getDefault()
        ).format(Date()) + parkingLot.ParkingLN

        //금액 선택 다이얼로그
        binding.btFillPointPriceSpinner.setOnClickListener {
            val list = resources.getStringArray(R.array.point_prices)
            BottomSheetSpinner().setInfo(
                "금액 선택",
                list.asList(),
                onItemClick = {
                    viewModel.setPrice(list[it])
                },
            ).run { show(supportFragmentManager, tag) }
        }

        binding.btFillPointTypeSpinner.setOnClickListener{
            val list = resources.getStringArray(R.array.charge_type)
            BottomSheetSpinner().setInfo(
                "결제 방식 선택",
                list.asList(),
                onItemClick = {
                    viewModel.setChargeType(list[it])
                },
            ).run { show(supportFragmentManager, tag) }
        }

        binding.tbFillPoint.setNavigationOnClickListener { finish() }

        //결제 시작 / 가상계좌번호 복사 버튼
        binding.btFillPoint.setOnClickListener {

            //가상계좌번호 복사 시
            if (!viewModel.bankNum.value.isNullOrBlank()) {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText(
                    "bankNum",
                    viewModel.bankName.value + " " + viewModel.bankNum.value
                )
                clipboard.setPrimaryClip(clip)
                snackBar("클립보드에 복사되었습니다!")
                return@setOnClickListener
            }

            //결제 시작 시 이니시스 결제 페이지로 POST (이니시스 매뉴얼 참고)
            binding.wvFillPoint.visibility = View.VISIBLE


            var notiURL = ""
            var nextURL = ""
            var chargeType = ""
            //결제 시작 시 이니시스 결제 페이지로 POST (이니시스 매뉴얼 참고)
            if(viewModel.selectedChargeType.value.equals("신용카드")){
                paymentType=0
                chargeType = "&P_INI_PAYMENT=CARD"
                nextURL = "https://eclv33ar05.execute-api.ap-northeast-2.amazonaws.com/GetInIpayCardNextURL"
            }else if(viewModel.selectedChargeType.value.equals("무통장입금")){
                paymentType=1
                chargeType = "&P_INI_PAYMENT=VBANK" //PAYMENT를 CARD로 바꾸고 P_NOTI_URL을 없애면 카드결제로 진행됩니다.
                nextURL = "https://eclv33ar05.execute-api.ap-northeast-2.amazonaws.com/GetInIpayNextURL"
                notiURL = "&P_NOTI_URL=${
                    URLEncoder.encode(
                        "https://eclv33ar05.execute-api.ap-northeast-2.amazonaws.com/GetInIpayResponse",
                        "UTF-8"
                    )
                }"
            }else{
                Toast.makeText(this,"결제 타입을 선택해주세요",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            binding.wvFillPoint.visibility = View.VISIBLE
            val postData =  chargeType +
                    "&P_MID=mrpark1com" +
                    "&P_OID=${viewModel.orderKey}" +
                    "&P_AMT=${viewModel.realPrice.value!!}" +
                    "&P_GOODS=${URLEncoder.encode("미팍 파트너스 예치금", "EUC-KR")}" +
                    "&P_UNAME=${URLEncoder.encode(intent.getStringExtra("name"), "EUC-KR")}" +
                    "&P_NEXT_URL=${
                        URLEncoder.encode(
                            nextURL,
                            "UTF-8"
                        )
                    }" +
                    notiURL +
                    "&P_NOTI=${parkingLot.PartnerBN}"




            binding.wvFillPoint.postUrl(
                "https://mobile.inicis.com/smart/payment/",
                postData.toByteArray()
            )

            //결제 결과를 통보받기 위해 백엔드와 웹소켓 연결
            viewModel.connectWebSocket()
        }

        viewModel.response.observe(this) {
            Log.d("TEST@","테스트좀해보자 :: ....")

            //결제 오류 발생 시 오류메시지 보여주고 activity 종료
            if (it.P_STATUS != "00") {
                CommonDialog(this@FillPointActivity,
                    message = it.P_RMESG1,
                    onPositive = {
                        finish()
                    },
                    cancelable= false).show()
                return@observe
            }


            if(paymentType==0){
                Log.d("TEST@","point33333")
                loadingDialog.cancel()
                binding.wvFillPoint.visibility = View.GONE

                //결제 완료시 activity 종료
                CommonDialog(this@FillPointActivity,
                    message = "성공적으로 처리 하였습니다.",
                    onPositive = {
                        finish()
                    },
                    cancelable= false).show()
            }else{

                Log.d("TEST@","테스트좀해보자 :: else")
                //결제 승인 후 최종 결제 결과(가상계좌 시 입금계좌) 요청
                val postData = "&P_TID=${it.P_TID}" +
                        "&P_MID=mrpark1com"
                binding.wvFillPoint.postUrl(
                    it.P_REQ_URL,
                    postData.toByteArray()
                )
            }

        }

        client = object : WebViewClient() {
            //카드결제 시 웹뷰에서 카카오페이, 토스 등 타 앱을 열기 위한 코드 (딥링크)
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request!!.url.toString()
                if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
                    val intent: Intent = try {
                        Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                    } catch (ex: URISyntaxException) {
                        return false
                    }
                    try {
                        startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        if (url.startsWith("intent")) {
                            return try {
                                val tempIntent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                val strParams = tempIntent.dataString
                                val intent2 = Intent(Intent.ACTION_VIEW)
                                intent2.data = Uri.parse(strParams)
                                startActivity(intent2)
                                true
                            } catch (e1: Exception) {
                                e1.printStackTrace()
                                val intent3: Intent?
                                try {
                                    intent3 = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                                    val marketIntent = Intent(Intent.ACTION_VIEW)
                                    marketIntent.data =
                                        Uri.parse("market://details?id=" + intent3.getPackage())
                                    startActivity(marketIntent)
                                } catch (e2: Exception) {
                                    e2.printStackTrace()
                                }
                                true
                            }
                        }
                    }
                } else {
                    view!!.loadUrl(url)
                    return false
                }
                return true
            }
        }

        chromeClient = object : WebChromeClient() {
            //웹뷰 사이트 변경 시
            override fun onReceivedTitle(view: WebView, title: String?) {
                super.onReceivedTitle(view, title)

                Log.d("TEST@","WebChromeClient view.url ::" + view.url)

                if(paymentType==0){ //신용카드

                }else{
                    //결제 요청 후 백엔드에서 값 받는 동안 대기
                    if (view.url!!.contains("GetInIpayCardNextURL")) {
                        binding.wvFillPoint.visibility = View.GONE
                        loadingDialog.show()
                        lifecycleScope.launch {
                            delay(10000)
                            if (viewModel.bankNum.value.isNullOrBlank()) {
                                Toast.makeText(
                                    this@FillPointActivity,
                                    "잠시 뒤에 시도해 주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        }
                    }

                    Log.d("TEST@","지나갑니다요")
                    binding.wvFillPoint.evaluateJavascript(
                        "(function() { return (document.getElementsByTagName('html')[0].innerHTML); })();"
                    ) { html ->

                        //P_REQ_URL 요청 후에 html 파싱해서 결과값 가져오기
                        //카드 결제거나 오류 발생 경우 (이 부분부터 구현 진행)
                        //오류 발생시 적절히 대응, 카드 결제시 결과값 파싱해서 백엔드 AddMyPoint로 전송
                        if (!html.contains("P_VACT_NUM")) { //입금할 계좌번호가 없을경우
                            Log.d("TEST@","point11111")

                            if(!html.contains("P_TYPE")){ //오류 발생
                                Log.d("TEST@","point22222")
                                Toast.makeText(
                                    this@FillPointActivity,
                                    "오류 발생",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }else{ //카드 결제

                            }
                            return@evaluateJavascript
                        }else{
                            Log.d("TEST@","point44444")
                            //가상계좌 결제 시 입금계좌 파싱, 화면에 보여줌
                            loadingDialog.cancel()
                            binding.wvFillPoint.visibility = View.GONE
                            val arr = html.split("&amp;")
                            for (s in arr) {
                                if (s.startsWith("P_VACT_NUM=")) {
                                    val num = s.substring("P_VACT_NUM=".length)
                                    viewModel.bankNum.postValue(num)
                                    Log.d(TAG, "onReceivedTitle: $num")
                                }
                                if (s.startsWith("P_FN_NM=")) {
                                    val name = s.substring("P_FN_NM=".length)
                                    viewModel.bankName.postValue(name)
                                    Log.d(TAG, "onReceivedTitle: $name")
                                }
                            }
                        }
                    }
                }
            }
        }

        //웹뷰 쿠키 허용
        CookieManager.getInstance().run {
            setAcceptCookie(true)
            setAcceptThirdPartyCookies(binding.wvFillPoint, true)
        }
        //웹뷰 기본 설정
        binding.wvFillPoint.run {
            settings.run {
                @SuppressLint("SetJavaScriptEnabled")
                javaScriptEnabled = true
                domStorageEnabled = true
                setSupportMultipleWindows(true)
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = client
            webChromeClient = chromeClient
        }
    }

    override fun onDestroy() {
        loadingDialog.cancel()
        super.onDestroy()

    }
}
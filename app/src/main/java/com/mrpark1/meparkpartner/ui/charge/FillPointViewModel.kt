package com.mrpark1.meparkpartner.ui.charge

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mrpark1.meparkpartner.data.model.common.ParkingLot
import com.mrpark1.meparkpartner.data.model.payment.InicisResponse
import com.mrpark1.meparkpartner.util.Constants.TAG
import com.mrpark1.meparkpartner.util.WebSocketListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class FillPointViewModel @Inject constructor(private val okHttpClient: OkHttpClient) : ViewModel() {

    lateinit var orderKey: String
    lateinit var parkingLot: ParkingLot

    private var webSocketListener: WebSocketListener? = null

    //금액 선택 변수들
    val selectedPrice = MutableLiveData("")
    val selectedChargeType = MutableLiveData("")
    val showRealPrice = MutableLiveData(false)
    val realPrice = MutableLiveData("")

    val bankNum = MutableLiveData("")
    val bankName = MutableLiveData("")

    private val _response = MutableLiveData<InicisResponse>()
    val response: LiveData<InicisResponse> = _response

    init {
        this.selectedPrice.value = "300,000원"
        realPrice.value = "300000"
        this.selectedChargeType.value = "신용카드"
    }

    //선택 금액에 따라 실제 금액 설정
    fun setPrice(selectedPrice: String) {
        this.selectedPrice.value = selectedPrice
        realPrice.value = when (selectedPrice) {
            "300,000원" -> "300000"
            "500,000원" -> "500000"
            "1,000,000원" -> "1000000"
            "2,000,000원" -> "2000000"
            else -> ""
        }
        showRealPrice.value = selectedPrice == "직접 입력"
    }

    fun setChargeType(chargeType: String) {
        this.selectedChargeType.value = chargeType
    }
    


    //서버로부터 결제 정보 받기 위한 소켓
    fun connectWebSocket() {
        if (webSocketListener != null) return
        webSocketListener = WebSocketListener {
            Log.d(TAG, "connectWebSocket: $it")
            val json = JSONObject(it)
            val result = json.get("Result").toString()
            val inicisResponse = Gson().fromJson(result, InicisResponse::class.java)

            Log.d("Socket@Listener","inicisResponse.P_RMESG1 :: " + inicisResponse.P_RMESG1)
            Log.d("Socket@Listener","inicisResponse.P_NOTI :: " + inicisResponse.P_NOTI)
            Log.d("Socket@Listener","inicisResponse.P_STATUS :: " + inicisResponse.P_STATUS)
            Log.d("Socket@Listener","inicisResponse.P_REQ_URL :: " + inicisResponse.P_REQ_URL)
            Log.d("Socket@Listener","inicisResponse.P_AMT :: " + inicisResponse.P_AMT)
            Log.d("Socket@Listener","inicisResponse.P_TID :: " + inicisResponse.P_TID)

            if (inicisResponse.P_NOTI == parkingLot.PartnerBN)
                _response.postValue(Gson().fromJson(result, InicisResponse::class.java))
        }

        val request: Request = Request.Builder()
            .url("wss://47c3xq46r0.execute-api.ap-northeast-2.amazonaws.com/production")
            .build()


        viewModelScope.launch {
            okHttpClient.newWebSocket(request, webSocketListener!!)
            //okHttpClient.dispatcher().executorService().shutdown()
        }
    }

    override fun onCleared() {
        okHttpClient.dispatcher().cancelAll()
        super.onCleared()
    }

}
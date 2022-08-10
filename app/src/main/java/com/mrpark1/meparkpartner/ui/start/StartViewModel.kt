package com.mrpark1.meparkpartner.ui.start

import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.messaging.FirebaseMessaging
import com.mrpark1.meparkpartner.data.model.common.User
import com.mrpark1.meparkpartner.data.model.user.AddMyFrtRequest
import com.mrpark1.meparkpartner.data.model.user.GetMyAccessTokenRequest
import com.mrpark1.meparkpartner.data.model.user.GetMyInfoRequest
import com.mrpark1.meparkpartner.data.repository.implementations.StartRepositoryImpl
import com.mrpark1.meparkpartner.di.StartModule
import com.mrpark1.meparkpartner.ui.Status
import com.mrpark1.meparkpartner.util.Constants
import com.mrpark1.meparkpartner.util.Constants.TAG
import com.mrpark1.meparkpartner.util.OneTapCoroutine
import com.mrpark1.meparkpartner.util.SharedPrefUtil
import com.squareup.moshi.JsonDataException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val startRepository: StartRepositoryImpl,
    private val sharedPrefUtil: SharedPrefUtil,
    @StartModule.FilterByAuthorizedTrue private val signInRequest: BeginSignInRequest, //가입된 구글계정만 보여주는 옵션
    @StartModule.FilterByAuthorizedFalse private val signUpRequest: BeginSignInRequest, //모든 구글계정 보여주는 옵션
    private val oneTapClient: SignInClient
) : ViewModel() {
    lateinit var oneTapCoroutine: OneTapCoroutine //원탭 로그인을 코루틴으로 구현

    private val _currentStatus = MutableLiveData<Status>()
    val currentStatus: LiveData<Status>
        get() = _currentStatus

    private lateinit var fcmToken: String //FCM 토큰
    private lateinit var _idToken: String //구글 로그인 토큰
    val idToken: String
        get() = _idToken

    private lateinit var _user: User
    val user: User
        get() = _user

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, e ->
        e.printStackTrace()
        when (e) {
            is UnknownHostException -> {
                _currentStatus.value = Status.ERROR_INTERNET //인터넷 미연결시 5초마다 재시도
                viewModelScope.launch {
                    delay(5000)
                    startLogic()
                }
            }
            is JsonDataException -> _currentStatus.value = Status.ERROR
        }
    }

    //로그인 로직 시작
    fun startLogic() {
        if (currentStatus.value == Status.START_LOGIC_STARTED) return
        _currentStatus.value = Status.START_LOGIC_STARTED

        viewModelScope.launch(coroutineExceptionHandler) {
            checkFirstLaunch()
        }
    }

    //최초 실행 여부 체크 (온보딩)
    private suspend fun checkFirstLaunch() {
        val isFirstLaunch = sharedPrefUtil.getBoolean(SharedPrefUtil.KEY_FIRST_LAUNCH, true)
        if (isFirstLaunch) _currentStatus.value = Status.START_FIRST_LAUNCH
        else getLocalAccessToken()
    }

    //로컬에 저장된 세션 토큰 확인
    private suspend fun getLocalAccessToken() {
        val accessToken = sharedPrefUtil.getString(SharedPrefUtil.KEY_ACCESS_TOKEN, null)
        if (accessToken == null) getIDT(signUpRequest) //저장된 토큰 없으면 구글로그인
        else getMyInfo() //토큰으로 유저 정보 조회
    }

    //구글 로그인
    private suspend fun getIDT(request: BeginSignInRequest) {
        try {
            //가입된 계정으로 로그인 시도
            val beginSignInResult = oneTapClient.beginSignIn(request).await()
            val intentSenderRequest = IntentSenderRequest
                .Builder(beginSignInResult.pendingIntent.intentSender)
                .build()

            val oneTapResult = oneTapCoroutine(intentSenderRequest)
            val credential = oneTapClient.getSignInCredentialFromIntent(oneTapResult.data)

            val idToken = credential.googleIdToken!!
            _idToken = idToken
            Constants.IDT = idToken

        } catch (e: ApiException) {
            Log.d(TAG,"login exception :: " + e.message)
            when (e.statusCode) { //가입된 계정 없을 경우 기기의 모든 구글계정 보여줌
                16 -> { //16: Cannot find a matching credential.
                    if (request.googleIdTokenRequestOptions.filterByAuthorizedAccounts())
                        getIDT(signUpRequest)
                    else {
                        _currentStatus.value = Status.START_ERROR_NO_GOOGLE
                    }
                }
                CommonStatusCodes.CANCELED -> {
                    _currentStatus.value = Status.START_ERROR_ONE_TAP_CANCELED
                }
                CommonStatusCodes.NETWORK_ERROR -> {
                    _currentStatus.value = Status.ERROR_INTERNET
                }
                else -> _currentStatus.value = Status.ERROR
            }
            e.printStackTrace()
            return
        }
        getMyAccessToken()
    }

    //구글 로그인 토큰으로 세션 토큰 요청
    private suspend fun getMyAccessToken() {
        Log.d("TEST@","point 1")
        val response = withContext(Dispatchers.IO) {
            startRepository.getMyAccessToken(
                GetMyAccessTokenRequest(IDT = idToken)
            )
        }
        when {
            response.isSuccessful -> {
                val body = response.body()!!
                val accessToken = body.AccessToken
                sharedPrefUtil.put(SharedPrefUtil.KEY_ACCESS_TOKEN, accessToken)
                getMyInfo() //성공 시 유저 정보 조회
            }
            response.code() == 403 || response.code() == 400 ->
                _currentStatus.value = Status.START_NEW_USER //계정이 없을 경우 회원가입으로
            else -> _currentStatus.value = Status.ERROR
        }
    }

    //유저 정보 조회
    private suspend fun getMyInfo() {
        Log.d("TEST@","point 2")
        val response = withContext(Dispatchers.IO) {
            startRepository.getMyInfo(GetMyInfoRequest())
        }
        when {
            response.isSuccessful -> {
                val body = response.body()!!
                _user = body
                Constants.PartnerBN = body.PartnerBN
                if(body.Role!=null&&body.Role=="Administrator"){
                    Constants.isAdmin = true
                }
                if(body.Role!=null&&body.Role=="SubAdministrator"){
                    Constants.isAdmin = true
                }

                Constants.CommutingStatus = body.CommutingStatus

                getFRT() //FCM 토큰 가져오기
            }
            response.code() == 403 ->
                getIDT(signInRequest) //세션 토큰 만료 시 구글 로그인부터
            else -> _currentStatus.value = Status.ERROR
        }
    }

    //FCM 토큰 조회
    private suspend fun getFRT() {
        val fcmToken = FirebaseMessaging.getInstance().token.await()
        this.fcmToken = fcmToken
        addMyFRT()
    }

    //FCM 토큰 갱신
    private suspend fun addMyFRT() {
        val response = withContext(Dispatchers.IO) {
            startRepository.addMyFRT(AddMyFrtRequest(fcmToken))
        }
        when {
            response.isSuccessful -> {
                sharedPrefUtil.put(SharedPrefUtil.KEY_USER_NAME, user.Name) //유저이름 로컬 저장
                when (user.PartnerStatus) {
                    "Normal" -> _currentStatus.value = Status.SUCCESS //파트너에 소속된 정상 유저
                    "Pending" -> _currentStatus.value = Status.SUCCESS //파트너 승인 진행중이나, 테스트를 위해 임의로 넘어가도록 함
                    else -> _currentStatus.value = Status.START_NO_PARTNER //파트너 미소속
                }
            }
            else -> _currentStatus.value = Status.ERROR
        }
    }
}
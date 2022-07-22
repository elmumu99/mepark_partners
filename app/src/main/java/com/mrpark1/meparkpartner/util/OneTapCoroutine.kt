package com.mrpark1.meparkpartner.util

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

class OneTapCoroutine(activity: ComponentActivity) {
    //구글 로그인을 코루틴으로 사용할 수 있게 함

    private var oneTapContinuation: CancellableContinuation<ActivityResult>? = null

    private val oneTapLauncher =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            oneTapContinuation?.resumeWith(Result.success(result))
        }

    suspend operator fun invoke(request: IntentSenderRequest) =
        suspendCancellableCoroutine<ActivityResult> { continuation ->
            oneTapContinuation = continuation
            oneTapLauncher.launch(request)
            continuation.invokeOnCancellation {
                oneTapContinuation = null
            }
        }
}
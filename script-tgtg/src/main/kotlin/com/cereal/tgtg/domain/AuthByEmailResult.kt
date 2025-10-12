package com.cereal.tgtg.domain

/**
 * Domain model representing the result of an email-based authentication request.
 * Either a pollingId is returned (normal flow) OR a captchaUrl (challenge flow).
 */
data class AuthByEmailResult(
    val pollingId: String? = null,
    val captchaUrl: String? = null,
) {
    val isCaptchaChallenge: Boolean get() = captchaUrl != null && pollingId == null
}

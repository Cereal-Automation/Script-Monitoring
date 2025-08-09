package com.cereal.tgtg.data

import com.cereal.command.monitor.data.tgtg.TgtgApiClient
import com.cereal.command.monitor.data.tgtg.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.models.AuthPollResponse
import com.cereal.tgtg.domain.TgtgAuthRepository

/**
 * Implementation of TgtgAuthRepository that uses TgtgApiClient internally.
 */
class TgtgAuthRepositoryImpl(
    private val tgtgApiClient: TgtgApiClient,
) : TgtgAuthRepository {
    override suspend fun login(): Boolean {
        return tgtgApiClient.login()
    }

    override suspend fun authByEmail(): AuthByEmailResponse {
        return tgtgApiClient.authByEmail()
    }

    override suspend fun authPoll(pollingId: String): AuthPollResponse {
        return tgtgApiClient.authPoll(pollingId)
    }
}

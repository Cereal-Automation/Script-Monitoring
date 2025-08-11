package com.cereal.tgtg.data

import com.cereal.command.monitor.data.tgtg.apiclients.TgtgApiClient
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthByEmailResponse
import com.cereal.command.monitor.data.tgtg.apiclients.models.AuthPollResponse
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

    override suspend fun authByEmail(email: String): AuthByEmailResponse {
        return tgtgApiClient.authByEmail(email)
    }

    override suspend fun authPoll(
        pollingId: String,
        email: String,
    ): AuthPollResponse {
        return tgtgApiClient.authPoll(pollingId, email)
    }
}

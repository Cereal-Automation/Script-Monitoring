package com.cereal.script.commands.tgtglogin

import com.cereal.script.clients.toogoodtogo.TgtgClient
import com.cereal.script.commands.Command
import com.cereal.script.commands.CommandResult

class TooGoodToGoLoginCommand(
    private val client: TgtgClient,
    private val emailAddress: String,
) : Command {
    override suspend fun shouldRun(): Boolean = true

    override suspend fun execute(): CommandResult {
        client.login()

        return CommandResult.Completed
    }

    override fun getDescription(): String = "Start login process"
}

package com.cereal.script.commands

/**
 * Command is the smallest amount of work possible
 */
interface Command {
    /**
     * true if this command should run, false if the command is already in its end state (ie. when the commands
     * purpose is to login a user but the user is already known false can be returned).
     */
    suspend fun shouldRun(): Boolean

    /**
     * Executes the command. This method may throw exceptions if it couldn't successfully complete its job.
     * Keep in mind the command should have 1 single thing to do, this makes it easier to retry that specific
     * part of it fails.
     */
    suspend fun execute(): CommandResult

    /**
     * Returns a description written in a way that represents an action. For example: "Registering a new account"
     */
    fun getDescription(): String
}

enum class CommandResult {
    Completed,

    /**
     * The command wants to be executed again. An example where this is useful is when a command scrapes multiple pages
     * of data.
     */
    Repeat,
    Skip,
}

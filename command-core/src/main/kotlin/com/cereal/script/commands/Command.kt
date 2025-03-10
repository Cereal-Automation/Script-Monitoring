package com.cereal.script.commands

import kotlin.time.Duration

/**
 * Command is the smallest amount of work possible
 */
interface Command {
    /**
     * true if this command should run, false if the command is already in its end state (ie. when the commands
     * purpose is to login a user but the user is already known RunDecision.Skip can be returned). This is also the way
     * to make the command repeat execution, for example to monitor products.
     *
     * @param context The context in which the command's execution is being evaluated,
     *                encapsulating necessary state and data.
     * @return A RunDecision indicating whether the command should run, skip, or run after a delay.
     */
    suspend fun shouldRun(context: ChainContext): RunDecision

    /**
     * Executes the command. This method may throw exceptions if it couldn't successfully complete its job.
     * Keep in mind the command should have 1 single thing to do, this makes it easier to retry that specific
     * part of it fails.
     */
    suspend fun execute(context: ChainContext)

    /**
     * Returns a description written in a way that represents an action. For example: "Registering a new account"
     */
    fun getDescription(): String
}

/**
 * Represents the result of evaluating whether a command should run.
 */
sealed class RunDecision {
    /**
     * Indicates that the command should not run.
     */
    data object Skip : RunDecision()

    /**
     * Indicates that the command should run immediately.
     */
    data object RunNow : RunDecision()

    /**
     * Indicates that the command should run after a specified delay.
     *
     * @param delay The duration to wait before executing the command.
     */
    data class RunWithDelay(
        val delay: Duration,
    ) : RunDecision()
}

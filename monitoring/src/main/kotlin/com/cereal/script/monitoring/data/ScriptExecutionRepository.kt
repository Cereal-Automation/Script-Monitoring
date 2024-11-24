package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.repository.ExecutionRepository
import java.util.NoSuchElementException

class ScriptExecutionRepository : ExecutionRepository {
    private val executions = mutableListOf<Execution>()

    override fun getAll(): List<Execution> = executions

    override fun get(sequenceNumber: Int): Execution =
        executions.find { it.sequenceNumber == sequenceNumber }
            ?: throw NoSuchElementException("Execution with sequenceNumber $sequenceNumber not found")

    override fun create(execution: Execution) {
        executions.add(execution)
    }

    override fun update(execution: Execution) {
        executions
            .indexOfFirst { it.sequenceNumber == execution.sequenceNumber }
            .takeIf { it >= 0 }
            ?.let { index -> executions[index] = execution }
    }
}

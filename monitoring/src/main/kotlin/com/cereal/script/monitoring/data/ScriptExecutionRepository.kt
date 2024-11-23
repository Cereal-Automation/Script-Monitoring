package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.repository.ExecutionRepository

class ScriptExecutionRepository : ExecutionRepository {
    private val executions = mutableListOf<Execution>()

    override fun getExecutions(): List<Execution> = executions

    override fun addExecution(execution: Execution) {
        executions.add(execution)
    }

    override fun updateExecution(execution: Execution) {
        executions
            .indexOfFirst { it.sequenceNumber == execution.sequenceNumber }
            .takeIf { it >= 0 }
            ?.let { index -> executions[index] = execution }
    }
}

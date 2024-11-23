package com.cereal.script.monitoring.domain.repository

import com.cereal.script.monitoring.domain.models.Execution

interface ExecutionRepository {
    fun getExecutions(): List<Execution>

    fun addExecution(execution: Execution)

    fun updateExecution(execution: Execution)
}

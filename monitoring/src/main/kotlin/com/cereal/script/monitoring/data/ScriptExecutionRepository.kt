package com.cereal.script.monitoring.data

import com.cereal.script.monitoring.domain.models.Execution
import com.cereal.script.monitoring.domain.repository.ExecutionRepository

class ScriptExecutionRepository : ExecutionRepository {
    private var execution: Execution? = null

    override fun exists(): Boolean = execution != null

    override fun get(): Execution = execution ?: throw Exception("Execution not found")

    override fun set(execution: Execution) {
        this.execution = execution
    }
}

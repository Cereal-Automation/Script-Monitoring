package com.cereal.script.monitoring.domain.repository

import com.cereal.script.monitoring.domain.models.Execution

interface ExecutionRepository {
    fun exists(): Boolean

    fun get(): Execution

    fun set(execution: Execution)
}

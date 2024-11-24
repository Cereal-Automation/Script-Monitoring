package com.cereal.script.monitoring.domain.repository

import com.cereal.script.monitoring.domain.models.Execution

interface ExecutionRepository {
    fun getAll(): List<Execution>

    fun get(sequenceNumber: Int): Execution

    fun create(execution: Execution)

    fun update(execution: Execution)
}

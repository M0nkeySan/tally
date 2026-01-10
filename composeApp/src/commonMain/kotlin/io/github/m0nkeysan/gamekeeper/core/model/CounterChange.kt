package io.github.m0nkeysan.gamekeeper.core.model

data class CounterChange(
    val id: String,
    val counterId: String,
    val counterName: String,
    val counterColor: Long,
    val previousValue: Int,
    val newValue: Int,
    val changeDelta: Int,
    val isDeleted: Boolean,
    val timestamp: Long,
    val createdAt: Long
)

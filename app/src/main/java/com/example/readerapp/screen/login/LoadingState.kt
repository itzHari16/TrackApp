package com.example.readerapp.screen.login

data class LoadingState(
    val status: Status,
    val msg: String? = null
) {
    companion object {
        val IDLE = LoadingState(Status.IDLE)
        val SUCCESS = LoadingState(Status.SUCCESS)
        val FAILED = LoadingState(Status.FAILED)
        val RUNNING = LoadingState(Status.RUNNING)
    }

    enum class Status {
        SUCCESS,
        FAILED,
        RUNNING,
        IDLE
    }
}
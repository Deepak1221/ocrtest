package com.example.ocrtest

     data class AppResource<out T>(val status: Status, val data: T?, val message: String?) {
        companion object {

            fun <T> success(data: T?): AppResource<T> {
                return AppResource(Status.SUCCESS, data, null)
            }

            fun <T> error(msg: String, data: T?): AppResource<T> {
                return AppResource(Status.ERROR, data, msg)
            }

            fun <T> loading(data: T?): AppResource<T> {
                return AppResource(Status.LOADING, data, null)
            }

        }
    }

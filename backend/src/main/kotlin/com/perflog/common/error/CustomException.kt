package com.perflog.common.error

class CustomException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
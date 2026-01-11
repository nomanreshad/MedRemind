package com.nomanhassan.medremind.core.util

enum class PlatformType {
    ANDROID, IOS
}

expect fun getPlatformType(): PlatformType
package org.cmpbuildconfig.demo

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
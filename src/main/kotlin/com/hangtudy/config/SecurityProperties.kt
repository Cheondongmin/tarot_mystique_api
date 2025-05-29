package com.hangtudy.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "security.ip-whitelist")
data class SecurityProperties(
    var enabled: Boolean = true,
    var allowedIps: List<String> = listOf(),
    var excludedPaths: List<String> = listOf()
)

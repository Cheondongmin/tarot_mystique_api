package com.hangtudy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
@EnableConfigurationProperties
class HangtudyApplication

fun main(args: Array<String>) {
	runApplication<HangtudyApplication>(*args)
}

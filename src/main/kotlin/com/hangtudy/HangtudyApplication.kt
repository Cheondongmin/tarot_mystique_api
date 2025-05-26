package com.hangtudy

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class HangtudyApplication

fun main(args: Array<String>) {
	runApplication<HangtudyApplication>(*args)
}

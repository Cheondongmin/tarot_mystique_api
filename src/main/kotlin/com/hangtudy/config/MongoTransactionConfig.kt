package com.hangtudy.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class MongoTransactionConfig {

    @Bean
    fun transactionManager(mongoDatabaseFactory: MongoDatabaseFactory): PlatformTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory)
    }
}

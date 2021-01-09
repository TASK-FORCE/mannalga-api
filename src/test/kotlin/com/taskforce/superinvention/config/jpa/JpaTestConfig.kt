package com.taskforce.superinvention.config.jpa

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager

@TestConfiguration
class JpaTestConfig(
    private val entityManager: EntityManager
) {

    @Bean
    fun jpaQueryFactory(): JPAQueryFactory = JPAQueryFactory(entityManager)
}
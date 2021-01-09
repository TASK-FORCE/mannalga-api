package com.taskforce.superinvention.config.jpa

import com.blazebit.persistence.Criteria
import com.blazebit.persistence.CriteriaBuilderFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import javax.persistence.EntityManager

@TestConfiguration
class CriteriaConfig(
    private val entityManager: EntityManager
) {

    @Bean
    fun createCriteriaBuilderFactory() : CriteriaBuilderFactory {
        val config = Criteria.getDefault()
        return config.createCriteriaBuilderFactory(entityManager.entityManagerFactory)
    }
}
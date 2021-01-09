package com.taskforce.superinvention.common.config.jpa

import com.blazebit.persistence.Criteria
import com.blazebit.persistence.CriteriaBuilderFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.persistence.EntityManager

@Configuration
class CriteriaConfig(
    private val entityManager: EntityManager
) {

    @Bean
    fun createCriteriaBuilderFactory() : CriteriaBuilderFactory {
        val config = Criteria.getDefault()
        return config.createCriteriaBuilderFactory(entityManager.entityManagerFactory)
    }
}
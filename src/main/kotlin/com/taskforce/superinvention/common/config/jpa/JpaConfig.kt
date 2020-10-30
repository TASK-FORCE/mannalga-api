package com.taskforce.superinvention.common.config.jpa

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.jpa.JpaVendorAdapter
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter


@Configuration
class JpaConfig {

    @Bean
    fun jpaVendorAdapter(): JpaVendorAdapter {
        val adapter: AbstractJpaVendorAdapter = HibernateJpaVendorAdapter()
        adapter.setShowSql(true)
        adapter.setDatabase(Database.MYSQL)
        adapter.setDatabasePlatform("com.taskforce.superinvention.common.config.jpa.dialect.CustomMysqlDialect")
        adapter.setGenerateDdl(false)
        return adapter
    }
}
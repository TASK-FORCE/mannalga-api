package com.taskforce.superinvention.common.config.jpa.dialect

import org.hibernate.dialect.MySQL8Dialect
import org.hibernate.dialect.function.SQLFunctionTemplate
import org.hibernate.type.StandardBasicTypes

class CustomMysqlDialect() : MySQL8Dialect() {

    init {
        this.registerFunction("group_concat", SQLFunctionTemplate(StandardBasicTypes.STRING, "group_concat(?1)"))
    }
}
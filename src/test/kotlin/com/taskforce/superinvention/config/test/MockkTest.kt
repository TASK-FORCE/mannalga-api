package com.taskforce.superinvention.config.test

import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles

@ExtendWith(MockKExtension::class)
@ActiveProfiles("test")
abstract class MockkTest
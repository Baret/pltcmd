package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import org.hexworks.cobalt.logging.api.LoggerFactory

class ProjectConfig: AbstractProjectConfig() {
    val log = LoggerFactory.getLogger(ProjectConfig::class)
    var time = 0L
    val executionTimes: MutableList<Long> = mutableListOf()

    override fun beforeAll() {
        log.info("Starting tests, measuring time")
        time = System.currentTimeMillis()
    }

    override fun afterAll() {
        val delta = System.currentTimeMillis() - time
        log.info("Tests complete! Execution took $delta ms")
        log.info("Average test execution time: ${executionTimes.average()} ms")
    }

    override fun listeners(): List<TestListener> =
        listOf(object: TestListener {
            var testStartedAt = 0L

            override fun beforeTest(testCase: TestCase): Unit {
                log.info("Starting test ${testCase.description}")
                testStartedAt = System.currentTimeMillis()
            }

            override fun afterTest(testCase: TestCase, result: TestResult): Unit {
                val executionTime = System.currentTimeMillis() - testStartedAt
                log.info("Execution of ${testCase.description} took $executionTime ms")
                executionTimes.add(executionTime)
            }
        })
}
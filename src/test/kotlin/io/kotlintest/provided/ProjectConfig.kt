package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.extensions.TestListener
import io.kotlintest.extensions.TopLevelTest
import org.hexworks.cobalt.logging.api.LoggerFactory

class ProjectConfig: AbstractProjectConfig() {
    val log = LoggerFactory.getLogger(ProjectConfig::class)
    private var time = 0L
    val executionTimes = mutableListOf<Long>()
    val heapSizes = mutableListOf<Long>()

    override fun beforeAll() {
        log.info("Starting tests, measuring time")
        logMemoryUsage()
        time = System.currentTimeMillis()
    }

    override fun afterAll() {
        val delta = System.currentTimeMillis() - time
        log.info("Tests complete! Execution took $delta ms")
        log.info("Average test execution time: ${executionTimes.average()} ms")
        logMemoryUsage()
        log.info("Maximum memory usage was ${heapSizes.max()} MB")
    }

    override fun listeners(): List<TestListener> =
        listOf(object: TestListener {
            var testStartedAt = 0L

            override fun beforeSpecClass(spec: Spec, tests: List<TopLevelTest>) {
                log.info("Starting tests ${spec.description().fullName()}")
            }

            override fun beforeTest(testCase: TestCase) {
                Runtime.getRuntime().gc()
                testStartedAt = System.currentTimeMillis()
            }

            override fun afterTest(testCase: TestCase, result: TestResult) {
                val executionTime = System.currentTimeMillis() - testStartedAt
                log.info("Execution of '${testCase.description.names().drop(1).joinToString(" ")}' took $executionTime ms")
                executionTimes.add(executionTime)
                heapSizes.add(heapSize())
            }
        })

    private fun logMemoryUsage() {
        val heapSize = heapSize()
        val maxHeapSize = Runtime.getRuntime().maxMemory() / 1024 / 1024
        log.info("Current memory usage: $heapSize / $maxHeapSize MB")
    }

    private fun heapSize() = Runtime.getRuntime().totalMemory() / 1024 / 1024
}
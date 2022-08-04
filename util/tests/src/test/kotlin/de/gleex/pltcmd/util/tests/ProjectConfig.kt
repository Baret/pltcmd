package de.gleex.pltcmd.util.tests

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import mu.KotlinLogging
import kotlin.reflect.KClass

private val log = KotlinLogging.logger {}

class ProjectConfig: AbstractProjectConfig() {
    private var allStartTime = 0L
    val executionTimes = mutableListOf<Long>()
    val heapSizes = mutableListOf<Long>()

    override val parallelism: Int = 1

    override suspend fun beforeProject() {
        log.info { "Starting tests, measuring time" }
        logMemoryUsage()
        allStartTime = System.currentTimeMillis()
    }

    override suspend fun afterProject() {
        val executionTime = System.currentTimeMillis() - allStartTime
        log.info { "Tests complete! Execution took $executionTime ms" }
        log.info { "Average test execution time: ${executionTimes.average()} ms" }
        logMemoryUsage()
        log.info { "Maximum memory usage was ${heapSizes.maxOrNull()} MB" }
    }

    override fun extensions(): List<Extension> =
        listOf(object: TestListener {
            private var testStartedAt = 0L

            override suspend fun prepareSpec(kclass: KClass<out Spec>) {
                log.info { "Starting tests ${kclass.qualifiedName}" }
            }

            override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
                log.info { "Finished tests ${kclass.qualifiedName}" }
                logMemoryUsage()
            }

            override suspend fun beforeTest(testCase: TestCase) {
                testStartedAt = System.currentTimeMillis()
            }

            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
                if (testCase.type == TestType.Container) {
                    return
                }
                val executionTime = System.currentTimeMillis() - testStartedAt
                val ids = testCase.descriptor.ids()
                val testClassName = ids.firstOrNull()?.value
                val infix = if(testCase.spec is WordSpec) {
                    "should"
                } else {
                    ""
                }
                val testName = ids.drop(1).joinToString(separator = " $infix ") { it.value }
                log.info { "Execution of [$testClassName] '$testName' took $executionTime ms" }
                log.info { " - - - - - - - - - - - - - - - - - -" }
                executionTimes.add(executionTime)
                heapSizes.add(heapSize())
            }
        })

    private fun logMemoryUsage() {
        val heapSize = heapSize()
        val maxHeapSize = Runtime.getRuntime().maxMemory() / 1024 / 1024
        log.info { "Current memory usage: $heapSize / $maxHeapSize MB" }
    }

    private fun heapSize() = Runtime.getRuntime().totalMemory() / 1024 / 1024
}
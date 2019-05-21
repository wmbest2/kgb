package best.william.kgb.test

import java.util.concurrent.TimeUnit
import org.junit.AssumptionViolatedException
import org.junit.rules.Stopwatch
import org.junit.runner.Description

class NanoStopwatch : Stopwatch() {

    private fun logInfo(description: Description, status: String, nanos: Long) {
        val testName = description.methodName
        println("Test $testName $status, spent $nanos nanoseconds")
    }

    override fun succeeded(nanos: Long, description: Description?) {
        logInfo(description!!, "succeeded", nanos)
    }

    override fun failed(nanos: Long, e: Throwable?, description: Description?) {
        logInfo(description!!, "failed", nanos)
    }

    override fun skipped(nanos: Long, e: AssumptionViolatedException?, description: Description?) {
        logInfo(description!!, "skipped", nanos)
    }

    override fun finished(nanos: Long, description: Description?) {
        logInfo(description!!, "finished", nanos)
    }
}
package tinywebscrawler.util

import com.hieuphan.tinywebscrawler.util.ExecutionTimeLogger
import org.specs2.mutable.Specification

class ExecutionTimeLoggerTest extends Specification with ExecutionTimeLogger {

  "exeuctionTimeLogger" should {
    "execute code and return result" in {
      def sumOf(x: Int, y: Int) = x + y
      val result = executeAndLogExecutionTime(sumOf(1, 1))
      result must_== 2
    }

    "log execution time using logger" in {
      // test to be written
      success
    }
  }

}

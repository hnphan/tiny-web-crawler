package tinywebscrawler.util

import com.hieuphan.tinywebscrawler.util.ExecutionTimeUtil
import org.specs2.mutable.Specification

class ExecutionTimeUtilTest extends Specification with ExecutionTimeUtil {

  "exeuctionTimeUtil" should {
    "use executeAndLogExecutionTimeInSeconds to execute code and return result" in {
      def sumOf(x: Int, y: Int) = x + y
      val result = executeAndLogExecutionTimeInSeconds(sumOf(1, 1))
      result must_== 2
    }

    "use executeAndReturnExecutionTimeInSeconds to return a tuple of result and exec time" in {
      def sumOf(x: Int, y: Int) = {
        Thread.sleep(1000)
        x + y
      }
      val (result, execTime) = executeAndReturnExecutionTimeInSeconds(sumOf(1, 1))
      result must_== 2
      execTime must beGreaterThan(1.0)
    }
  }

}

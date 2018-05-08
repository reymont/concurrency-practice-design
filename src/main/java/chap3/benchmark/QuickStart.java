package chap3.benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author Sven Augustus
 * @version 1.0
 * @since JDK 1.8
 */
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 3)
@Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
//@Threads(16)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class QuickStart {

  @Benchmark
  public String testStringAdd() {
    String a = "";
    for (int i = 0; i < 10; i++) {
      a += i;
    }
    return a;
  }

  @Benchmark
  public String testStringBuilderAdd() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 10; i++) {
      sb.append(i);
    }
    return sb.toString();
  }

  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(QuickStart.class.getSimpleName())
        .forks(1)
        .build();
    new Runner(opt).run();
  }

}
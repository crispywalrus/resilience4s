# resilience4s
[![Build Status](https://travis-ci.org/softwaremill/resilience4s.svg?branch=master)](https://travis-ci.org/softwaremill/resilience4s)

This project is just a scala wrapper around [resilience4j](https://github.com/resilience4j/resilience4j) 
which is a fault tolerance library designed for java.

With resilience4s you can easily add any fault-tolerance pattern to `F` of your choice.

Current support includes:
* [cats-effects](#cats-effect)
* [monix.Task](#monix)
* [ZIO](#zio)

Resilience4s provides several core modules which mirrors those in resilience4j:

* [resilience4s-circuitbreaker](#circuitbreaker): Circuit breaking
* [resilience4s-ratelimiter](#ratelimiter): Rate limiting
* [resilience4s-bulkhead](#bulkhead): Bulkheading
* [resilience4s-retry](#retry): Automatic retrying (sync and async)
* [resilience4s-timelimiter](#timelimiter): Timeout handling
* [resilience4s-cache](#cache): Result caching

## integrations

### cats-effect

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "cats" % "0.1.0-SNAPSHOT"
```

```scala
import sttp.resilience4s.cats.implicits._
```

### monix

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "monix" % "0.1.0-SNAPSHOT"
```

```scala
import sttp.resilience4s.monix.implicits._
```

### zio

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "zio" % "0.1.0-SNAPSHOT"
```

```scala
import sttp.resilience4s.zio.implicits._
```

## modules

All examples assume existence of following service:
```scala
import cats.effect.{ContextShift, IO, Timer}

object Service {
    def getUsersIds: IO[List[String]] = IO.pure(List("123" ,"234"))
}
```

### circuitbreaker

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "circuitbreaker" % "0.1.0-SNAPSHOT"
```

```scala
def exampleCircuitbreaker(implicit cs: ContextShift[IO], timer: Timer[IO]) = {
    import sttp.resilience4s.cats.implicits._
    import sttp.resilience4s.circuitbreaker.syntax._
    import io.github.resilience4j.circuitbreaker.CircuitBreaker
    
    val circuitBreaker = CircuitBreaker.ofDefaults("backendName")
    Service.getUsersIds
        .withCircuitBreaker(circuitBreaker)
        .unsafeRunSync()
}
```

### ratelimiter

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "ratelimiter" % "0.1.0-SNAPSHOT"
```

```scala
def exampleRateLimiter(implicit cs: ContextShift[IO], timer: Timer[IO]) = {
    import sttp.resilience4s.cats.implicits._
    import sttp.resilience4s.ratelimiter.syntax._
    import io.github.resilience4j.ratelimiter.{RateLimiterConfig, RateLimiterRegistry}
    import java.time.Duration
    
    val config = RateLimiterConfig.custom()
      .limitRefreshPeriod(Duration.ofMillis(1))
      .limitForPeriod(10)
      .timeoutDuration(Duration.ofMillis(25))
      .build()
    
    // Create registry
    val rateLimiterRegistry = RateLimiterRegistry.of(config)
    
    // Use registry
    val rateLimiter = rateLimiterRegistry
      .rateLimiter("name1")
    
    Service.getUsersIds
        .withRateLimiter(rateLimiter)
        .unsafeRunSync()
}
```

### bulkhead

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "bulkhead" % "0.1.0-SNAPSHOT"
```

```scala
def exampleBulkhead(implicit cs: ContextShift[IO], timer: Timer[IO]) = {
    import sttp.resilience4s.cats.implicits._
    import sttp.resilience4s.bulkhead.syntax._
    import io.github.resilience4j.bulkhead.{BulkheadConfig, Bulkhead}
    import java.time.Duration

    val config = BulkheadConfig.custom()
        .maxConcurrentCalls(150)
        .maxWaitDuration(Duration.ofMillis(25))
        .build()
    
    val bulkhead = Bulkhead.of("backendName", config)

    Service.getUsersIds
        .withBulkhead(bulkhead)
        .unsafeRunSync()
}
```

### retry

```scala
libraryDependencies += "com.softwaremill.sttp.resilience4s" % "retry" % "0.1.0-SNAPSHOT"
```

```scala
def exampleRetry(implicit cs: ContextShift[IO], timer: Timer[IO]) = {
    import sttp.resilience4s.cats.implicits._
    import sttp.resilience4s.retry.syntax._
    import io.github.resilience4j.retry.Retry

    val retry = Retry.ofDefaults("backendName")

    Service.getUsersIds
        .withRetry(retry)
        .unsafeRunSync()
}
```

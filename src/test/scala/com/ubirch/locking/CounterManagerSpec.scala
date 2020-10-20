package com.ubirch.locking

import com.github.sebruck.EmbeddedRedis
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, Matchers}
import redis.embedded.RedisServer

class CounterManagerSpec extends FeatureSpec with EmbeddedRedis
  with BeforeAndAfterAll
  with StrictLogging
  with Matchers {

  var redis: Option[RedisServer] = None
  var counterManager: CounterManagerImp = _

  class CounterManagerImp extends CounterManager {}

  override def beforeAll(): Unit = {
    redis = Some(startRedis(6379))
    counterManager = new CounterManagerImp()
  }

  override def afterAll {
    stopRedis(redis.get)
  }

  feature("basic test") {

    scenario("inc/get/dec counter") {
      val counterName = s"myLock-${UUIDUtil.uuidStr}"
      counterManager.inc(counterName) shouldBe 1L
      counterManager.inc(counterName) shouldBe 2L
      counterManager.inc(counterName) shouldBe 3L

      counterManager.get(counterName) shouldBe 3L

      counterManager.dec(counterName) shouldBe 2L
      counterManager.dec(counterName) shouldBe 1L
      counterManager.dec(counterName) shouldBe 0L

      counterManager.get(counterName) shouldBe 0L
    }

    scenario("inc/reset counter") {
      val counterName = s"myLock-${UUIDUtil.uuidStr}"
      counterManager.inc(counterName) shouldBe 1L
      counterManager.inc(counterName) shouldBe 2L
      counterManager.inc(counterName) shouldBe 3L
      counterManager.inc(counterName) shouldBe 4L
      counterManager.inc(counterName) shouldBe 5L

      counterManager.reset(counterName) shouldBe 0L

      counterManager.get(counterName) shouldBe 0L
    }


    scenario("inc/get/dec counters") {
      val counterNames = List(
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}"
      )

      counterNames.foreach { counterName =>
        counterManager.inc(counterName) shouldBe 1L
        counterManager.inc(counterName) shouldBe 2L
        counterManager.inc(counterName) shouldBe 3L

        counterManager.get(counterName) shouldBe 3L

        counterManager.dec(counterName) shouldBe 2L
        counterManager.dec(counterName) shouldBe 1L
        counterManager.dec(counterName) shouldBe 0L

        counterManager.get(counterName) shouldBe 0L
      }
    }

    scenario("inc/get/dec counters 2") {
      val counterNames = List(
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}",
        s"myLock-${UUIDUtil.uuidStr}"
      )

      counterNames.foreach(counterManager.inc(_) shouldBe 1L)
      counterNames.foreach(counterManager.inc(_) shouldBe 2L)
      counterNames.foreach(counterManager.inc(_) shouldBe 3L)
      counterNames.foreach(counterManager.inc(_) shouldBe 4L)

      counterNames.foreach(counterManager.get(_) shouldBe 4L)

      counterNames.foreach(counterManager.dec(_) shouldBe 3L)
      counterNames.foreach(counterManager.dec(_) shouldBe 2L)
      counterNames.foreach(counterManager.dec(_) shouldBe 1L)
      counterNames.foreach(counterManager.dec(_) shouldBe 0L)

      counterNames.foreach(counterManager.get(_) shouldBe 0L)
    }
  }
}

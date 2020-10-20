package com.ubirch.locking

import com.github.sebruck.EmbeddedRedis
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, Matchers}
import redis.embedded.RedisServer

class StaticLockManagerSpec extends FeatureSpec with EmbeddedRedis
  with BeforeAndAfterAll
  with StrictLogging
  with Matchers {

  var redis: Option[RedisServer] = None
  var lockManager: StaticLockManagerImpl = _

  class StaticLockManagerImpl extends StaticLockManager {
    val lockId = s"myLock-${UUIDUtil.uuidStr}"
  }

  override def beforeAll(): Unit = {
    redis = Some(startRedis(6379))
    lockManager = new StaticLockManagerImpl()
  }

  override def afterAll {
    stopRedis(redis.get)
  }


  feature("basic test") {

    scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = lockManager.redisson.getLock(lockName)
      lockManager.lock shouldBe true
      lockManager.unlock shouldBe true
    }

    scenario("no lock") {
      lockManager.unlock shouldBe false
    }

  }
}

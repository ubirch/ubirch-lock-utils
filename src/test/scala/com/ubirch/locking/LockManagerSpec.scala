package com.ubirch.locking

import com.github.sebruck.EmbeddedRedis
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{BeforeAndAfterAll, FeatureSpec, Matchers}
import redis.embedded.RedisServer

class LockManagerSpec extends FeatureSpec with EmbeddedRedis
  with BeforeAndAfterAll
  with Matchers {

  var redis: Option[RedisServer] = None
  var lockManager: LockManagerImpl = _

  class LockManagerImpl extends LockManager {}

  override def beforeAll(): Unit = {
    redis = Some(startRedis(6379))
    lockManager = new LockManagerImpl()
  }

  override def afterAll {
    stopRedis(redis.get)
  }


  feature("basic test") {

    scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      lockManager.lock(lockName) shouldBe true
      lockManager.unlock(lockName) shouldBe true
    }

    scenario("no lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      lockManager.unlock(lockName) shouldBe false
    }
  }
}

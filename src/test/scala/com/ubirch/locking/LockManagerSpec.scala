package com.ubirch.locking

import com.github.sebruck.EmbeddedRedis
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.BeforeAndAfterAll
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import redis.embedded.RedisServer

class LockManagerSpec extends AnyFeatureSpec with EmbeddedRedis
  with BeforeAndAfterAll
  with Matchers {

  var redis: Option[RedisServer] = None
  var lockManager: LockManagerImpl = _

  class LockManagerImpl extends LockManager {}

  override def beforeAll(): Unit = {
    redis = Some(startRedis(6379))
    lockManager = new LockManagerImpl()
  }

  override def afterAll(): Unit = {
    stopRedis(redis.get)
  }


  Feature("basic test") {

    Scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      lockManager.lock(lockName) shouldBe true
      lockManager.unlock(lockName) shouldBe true
    }

    Scenario("no lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      lockManager.unlock(lockName) shouldBe false
    }
  }
}

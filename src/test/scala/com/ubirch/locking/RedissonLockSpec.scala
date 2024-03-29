package com.ubirch.locking

import com.github.sebruck.EmbeddedRedis
import com.typesafe.scalalogging.StrictLogging
import com.ubirch.locking.config.LockingConfig
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.BeforeAndAfterAll
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import redis.embedded.RedisServer

class RedissonLockSpec extends AnyFeatureSpec with EmbeddedRedis
  with StrictLogging
  with BeforeAndAfterAll
  with Matchers {

  var redis: Option[RedisServer] = None

  override def beforeAll(): Unit = {
    redis = Some(startRedis(6379))
  }

  override def afterAll(): Unit = {
    stopRedis(redis.get)
  }

  Feature("basic test") {

    Scenario("create a lock") {

      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = LockingConfig.redisson.getLock(lockName)
      lock1.tryLock()
      lock1.isLocked shouldBe true
      lock1.unlock()
      lock1.isLocked shouldBe false
    }

    Scenario("no lock") {

      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = LockingConfig.redisson.getLock(lockName)
      lock1.isLocked shouldBe false
    }

    Scenario("read/write lock") {

      val lockName = s"myLock-${UUIDUtil.uuidStr}"

      val lock1 = LockingConfig.redisson.getReadWriteLock(lockName)

      lock1.readLock().isLocked shouldBe false
      lock1.writeLock().isLocked shouldBe false

      lock1.readLock.lock()
      lock1.readLock().isLocked shouldBe true
      lock1.writeLock().isLocked shouldBe false

      lock1.writeLock().tryLock()
      lock1.readLock().isLocked shouldBe true
      lock1.writeLock().isLocked shouldBe false

      lock1.readLock().unlock()
      lock1.readLock().isLocked shouldBe false
      lock1.writeLock().isLocked shouldBe false

      lock1.writeLock().tryLock()
      lock1.readLock().isLocked shouldBe false
      lock1.writeLock().isLocked shouldBe true

      lock1.writeLock().unlock()
      lock1.readLock().isLocked shouldBe false
      lock1.writeLock().isLocked shouldBe false
    }

    Scenario("simple semaphore test") {

      val semaphore = LockingConfig.redisson.getSemaphore("semaphore")
      semaphore.tryAcquire shouldBe false
      semaphore.isExists shouldBe false
      semaphore.release()
      semaphore.tryAcquire shouldBe true
      semaphore.isExists shouldBe true
    }
  }

}

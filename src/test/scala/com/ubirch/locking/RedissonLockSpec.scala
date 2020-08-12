package com.ubirch.locking

import com.typesafe.scalalogging.StrictLogging
import com.ubirch.locking.config.LockingConfig
import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{FeatureSpec, Matchers}

class RedissonLockSpec extends FeatureSpec
  with StrictLogging
  with Matchers {

  private val redisson = LockingConfig.redisson

  feature("basic test") {

    scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = redisson.getLock(lockName)
      lock1.tryLock()
      lock1.isLocked shouldBe true
      lock1.unlock()
      lock1.isLocked shouldBe false
    }

    scenario("no lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      val lock1 = redisson.getLock(lockName)
      lock1.isLocked shouldBe false
    }

    scenario("read/write lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"

      val lock1 = redisson.getReadWriteLock(lockName)

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

    //    scenario("simple semaphore test") {
    //      val semaphore = redisson.getSemaphore("semaphore")
    //      semaphore.tryAcquire shouldBe true
    //      semaphore.isExists  shouldBe true
    //      semaphore.release()
    //      semaphore.isExists  shouldBe false
    //    }

  }
}

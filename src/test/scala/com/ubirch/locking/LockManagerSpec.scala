package com.ubirch.locking

import com.ubirch.util.uuid.UUIDUtil
import org.scalatest.{FeatureSpec, Matchers}

class LockManagerSpec extends FeatureSpec
  with LockManager
  with Matchers {

  feature("basic test") {

    scenario("create a lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      lock(lockName) shouldBe true
      unlock(lockName) shouldBe true
    }

    scenario("no lock") {
      val lockName = s"myLock-${UUIDUtil.uuidStr}"
      unlock(lockName) shouldBe false
    }
  }
}

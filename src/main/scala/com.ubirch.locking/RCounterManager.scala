package com.ubirch.locking

import com.ubirch.locking.config.LockingConfig
import org.redisson.api.RedissonClient

trait RCounterManager {

  val redisson: RedissonClient = LockingConfig.redisson

  def rInc(counterId: String): Long = {
    redisson.getAtomicLong(counterId).incrementAndGet()
  }

  def rDec(counterId: String): Long = {
    redisson.getAtomicLong(counterId).decrementAndGet()
  }

  def rGet(counterId: String): Long = {
    redisson.getAtomicLong(counterId).get()
  }

  def rReset(counterId: String): Long = {
    redisson.getAtomicLong(counterId).set(0)
    rGet(counterId)
  }

}
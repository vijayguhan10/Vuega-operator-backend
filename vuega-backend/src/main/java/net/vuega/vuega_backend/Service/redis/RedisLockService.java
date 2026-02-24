package net.vuega.vuega_backend.Service.redis;

import java.time.Duration;
import java.util.Objects;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Distributed lock service backed by Redis SETNX.
 * Key format: "vuega:seat_lock:{scheduleId}:{seatId}"
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLockService {

    private static final String KEY_PREFIX = "vuega:seat_lock:";

    private final StringRedisTemplate redisTemplate;

    // -------------------------------------------------------------------------
    // Key helpers
    // -------------------------------------------------------------------------

    public String buildKey(Long scheduleId, Long seatId) {
        return KEY_PREFIX + scheduleId + ":" + seatId;
    }

    // -------------------------------------------------------------------------
    // Core operations
    // -------------------------------------------------------------------------

    /**
     * Try to acquire a lock using SET NX EX semantics.
     *
     * @param key        Redis key (use {@link #buildKey})
     * @param value      Caller identity (e.g. "partnerId:42") used for safe release
     * @param ttlSeconds Lock TTL
     * @return true if lock was acquired, false if already held
     */
    public boolean acquireLock(String key, String value, long ttlSeconds) {
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(ttlSeconds));
        boolean result = Boolean.TRUE.equals(acquired);
        log.debug("acquireLock key={} value={} result={}", key, value, result);
        return result;
    }

    /**
     * Release a lock unconditionally. Use {@link #releaseLockIfOwner} when
     * ownership verification matters.
     */
    public void releaseLock(String key) {
        redisTemplate.delete(key);
        log.debug("releaseLock key={}", key);
    }

    /**
     * Release a lock only if the current holder matches {@code expectedValue}.
     *
     * @return true if the lock was released, false if not the owner or key gone
     */
    public boolean releaseLockIfOwner(String key, String expectedValue) {
        String current = redisTemplate.opsForValue().get(key);
        if (Objects.equals(current, expectedValue)) {
            redisTemplate.delete(key);
            log.debug("releaseLockIfOwner key={} released", key);
            return true;
        }
        log.debug("releaseLockIfOwner key={} NOT owner (current={})", key, current);
        return false;
    }

    /**
     * Renew the TTL of a lock, but only if the caller still owns it.
     *
     * @return true if renewed, false if the lock is gone or owned by someone else
     */
    public boolean renewLock(String key, String expectedValue, long ttlSeconds) {
        String current = redisTemplate.opsForValue().get(key);
        if (!Objects.equals(current, expectedValue)) {
            log.debug("renewLock key={} NOT owner (current={})", key, current);
            return false;
        }
        redisTemplate.expire(key, Duration.ofSeconds(ttlSeconds));
        log.debug("renewLock key={} renewed ttl={}s", key, ttlSeconds);
        return true;
    }

    /**
     * Return the current lock holder value, or null if absent.
     */
    public String getLockHolder(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Convenience method: builds the key and acquires the lock.
     */
    public boolean acquireSeatLock(Long scheduleId, Long seatId, Long partnerId, long ttlSeconds) {
        String key = buildKey(scheduleId, seatId);
        String value = ownerValue(partnerId);
        return acquireLock(key, value, ttlSeconds);
    }

    /**
     * Convenience method: release only if caller owns the lock.
     */
    public boolean releaseSeatLock(Long scheduleId, Long seatId, Long partnerId) {
        String key = buildKey(scheduleId, seatId);
        String value = ownerValue(partnerId);
        return releaseLockIfOwner(key, value);
    }

    /**
     * Convenience method: renew TTL only if caller owns the lock.
     */
    public boolean renewSeatLock(Long scheduleId, Long seatId, Long partnerId, long ttlSeconds) {
        String key = buildKey(scheduleId, seatId);
        String value = ownerValue(partnerId);
        return renewLock(key, value, ttlSeconds);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    private String ownerValue(Long partnerId) {
        return "partner:" + partnerId;
    }
}

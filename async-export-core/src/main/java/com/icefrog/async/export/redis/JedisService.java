/***
 * Copyright 2019 icefrog.su
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.icefrog.async.export.redis;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.*;
import redis.clients.jedis.params.GeoRadiusParam;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JRedis 方式操作缓存数据
 */
@Service("jedisService")
public class JedisService {

    @Value("${spring.redis.host}")
    private String ip;

    @Value("${spring.redis.port:6379}")
    private Integer port;

    @Value("${spring.redis.timeout:3000}")
    private Integer timeout;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.pool.max-idle:10}")
    private Integer maxIdle;

    @Value("${spring.redis.pool.min-idle:1}")
    private Integer minIdle;

    @Value("${spring.redis.pool.max-wait:-1}")
    private Long maxWaitMillis;

    /**
     * JedisPool
     */
    private JedisPool jedisPool;

    public JedisService init() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);

        if (StringUtils.isBlank(password)) {
            this.jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout);
        } else {
            this.jedisPool = new JedisPool(jedisPoolConfig, ip, port, timeout, password);
        }

        return this;
    }

    /**
     * Set the string value as value of the key. The string can't be longer than 1073741824 bytes (1
     * GB). <p> Time complexity: O(1)
     *
     * @param key key
     * @param value value
     * @return Status code reply
     */
    public String set(final String key, String value) {
        Jedis jedis = getResource();
        try {
            return jedis.set(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Get the value of the specified key. If the key does not exist null is returned. If the value
     * stored at key is not a string an error is returned because GET can only handle string
     * values.
     * <p> Time complexity: O(1)
     *
     * @return Bulk reply
     */
    public String get(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.get(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Test if the specified key exists. The command returns the number of keys existed Time
     * complexity: O(N)
     *
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were
     * removed 0 if none of the specified key existed
     */
    public Long exists(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.exists(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Test if the specified key exists. The command returns "1" if the key exists, otherwise "0" is
     * returned. Note that even keys set with an empty string as value will return "1". Time
     * complexity: O(1)
     *
     * @return Boolean reply, true if the key exists, otherwise false
     */
    public Boolean exists(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.exists(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove the specified keys. If a given key does not exist no operation is performed for this
     * key. The command returns the number of keys removed. Time complexity: O(1)
     *
     * @return Integer reply, specifically: an integer greater than 0 if one or more keys were
     * removed 0 if none of the specified key existed
     */
    public Long del(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.del(keys);
        } finally {
            returnResource(jedis);
        }
    }

    public Long del(String key) {
        Jedis jedis = getResource();
        try {
            return jedis.del(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the type of the value stored at key in form of a string. The type can be one of
     * "none", "string", "list", "set". "none" is returned if the key does not exist. Time
     * complexity: O(1)
     *
     * @return Status code reply, specifically: "none" if the key does not exist "string" if the key
     * contains a String value "list" if the key contains a List value "set" if the key contains a
     * Set value "zset" if the key contains a Sorted Set value "hash" if the key contains a Hash
     * value
     */
    public String type(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.type(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Atomically renames the key oldkey to newkey. If the source and destination name are the same
     * an error is returned. If newkey already exists it is overwritten. <p> Time complexity: O(1)
     *
     * @return Status code repy
     */
    public String rename(final String oldkey, final String newkey) {
        Jedis jedis = getResource();
        try {
            return jedis.rename(oldkey, newkey);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Rename oldkey into newkey but fails if the destination key newkey already exists. <p> Time
     * complexity: O(1)
     *
     * @return Integer reply, specifically: 1 if the key was renamed 0 if the target key already
     * exist
     */
    public Long renamenx(final String oldkey, final String newkey) {
        Jedis jedis = getResource();
        try {
            return jedis.renamenx(oldkey, newkey);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set a timeout on the specified key. After the timeout the key will be automatically deleted
     * by the server. A key with an associated timeout is said to be volatile in Redis terminology.
     * <p> Voltile keys are stored on disk like the other keys, the timeout is persistent too like
     * all the other aspects of the dataset. Saving a dataset containing expires and stopping the
     * server does not stop the flow of time as Redis stores on disk the time when the key will no
     * longer be available as Unix time, and not the remaining seconds. <p> Since Redis 2.1.3 you
     * can update the value of the timeout of a key already having an expire set. It is also
     * possible to undo the expire at all turning the key into a normal key using the {@link
     * #persist(String) PERSIST} command. <p> Time complexity: O(1)
     *
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt; 2.1.3,
     * Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expire(final String key, final int seconds) {
        Jedis jedis = getResource();
        try {
            return jedis.expire(key, seconds);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * EXPIREAT works exctly like {@link #expire(String, int) EXPIRE} but instead to get the number
     * of seconds representing the Time To Live of the key as a second argument (that is a relative
     * way of specifing the TTL), it takes an absolute one in the form of a UNIX timestamp (Number
     * of seconds elapsed since 1 Gen 1970). <p> EXPIREAT was introduced in order to implement the
     * Append Only File persistence mode so that EXPIRE commands are automatically translated into
     * EXPIREAT commands for the append only file. Of course EXPIREAT can also used by programmers
     * that need a way to simply specify that a given key should expire at a given time in the
     * future. <p> Since Redis 2.1.3 you can update the value of the timeout of a key already having
     * an expire set. It is also possible to undo the expire at all turning the key into a normal
     * key using the {@link #persist(String) PERSIST} command. <p> Time complexity: O(1)
     *
     * @return Integer reply, specifically: 1: the timeout was set. 0: the timeout was not set since
     * the key already has an associated timeout (this may happen only in Redis versions &lt; 2.1.3,
     * Redis &gt;= 2.1.3 will happily update the timeout), or the key does not exist.
     * @see <a href="http://code.google.com/p/redis/wiki/ExpireCommand">ExpireCommand</a>
     */
    public Long expireAt(final String key, final long unixTime) {
        Jedis jedis = getResource();
        try {
            return jedis.expireAt(key, unixTime);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * The TTL command returns the remaining time to live in seconds of a key that has an {@link
     * #expire(String, int) EXPIRE} set. This introspection capability allows a Redis client to
     * check how many seconds a given key will continue to be part of the dataset.
     *
     * @return Integer reply, returns the remaining time to live in seconds of a key that has an
     * EXPIRE. In Redis 2.6 or older, if the Key does not exists or does not have an associated
     * expire, -1 is returned. In Redis 2.8 or newer, if the Key does not have an associated expire,
     * -1 is returned or if the Key does not exists, -2 is returned.
     */
    public Long ttl(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.ttl(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * GETSET is an atomic set this value and return the old value command. Set key to the string
     * value and return the old value stored at key. The string can't be longer than 1073741824
     * bytes (1 GB). <p> Time complexity: O(1)
     *
     * @return Bulk reply
     */
    public String getSet(final String key, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.getSet(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Get the values of all the specified keys. If one or more keys dont exist or is not of type
     * String, a 'nil' value is returned instead of the value of the specified key, but the
     * operation never fails. <p> Time complexity: O(1) for every key
     *
     * @return Multi bulk reply
     */
    public List<String> mget(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.mget(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * SETNX works exactly like {@link #set(String, String) SET} with the only difference that if
     * the key already exists no operation is performed. SETNX actually means "SET if Not eXists".
     * <p> Time complexity: O(1)
     *
     * @return Integer reply, specifically: 1 if the key was set 0 if the key was not set
     */
    public Long setnx(final String key, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.setnx(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * The command is exactly equivalent to the following group of commands: {@link #set(String,
     * String) SET} + {@link #expire(String, int) EXPIRE}. The operation is atomic. <p> Time
     * complexity: O(1)
     *
     * @return Status code reply
     */
    public String setex(final String key, final int seconds, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.setex(key, seconds, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set the the respective keys to the respective values. MSET will replace old values with new
     * values, while {@link #msetnx(String...) MSETNX} will not perform any operation at all even if
     * just a single key already exists. <p> Because of this semantic MSETNX can be used in order to
     * set different keys representing different fields of an unique logic object in a way that
     * ensures that either all the fields or none at all are set. <p> Both MSET and MSETNX are
     * atomic operations. This means that for instance if the keys A and B are modified, another
     * client talking to Redis can either see the changes to both A and B at once, or no
     * modification at all.
     *
     * @return Status code reply Basically +OK as MSET can't fail
     * @see #msetnx(String...)
     */
    public String mset(final String... keysvalues) {
        Jedis jedis = getResource();
        try {
            return jedis.mset(keysvalues);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set the the respective keys to the respective values. {@link #mset(String...) MSET} will
     * replace old values with new values, while MSETNX will not perform any operation at all even
     * if just a single key already exists. <p> Because of this semantic MSETNX can be used in order
     * to set different keys representing different fields of an unique logic object in a way that
     * ensures that either all the fields or none at all are set. <p> Both MSET and MSETNX are
     * atomic operations. This means that for instance if the keys A and B are modified, another
     * client talking to Redis can either see the changes to both A and B at once, or no
     * modification at all.
     *
     * @return Integer reply, specifically: 1 if the all the keys were set 0 if no key was set (at
     * least one key already existed)
     * @see #mset(String...)
     */
    public Long msetnx(final String... keysvalues) {
        Jedis jedis = getResource();
        try {
            return jedis.msetnx(keysvalues);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * IDECRBY work just like {@link #decr(String) INCR} but instead to decrement by 1 the decrement
     * is integer. <p> INCR commands are limited to 64 bit signed integers. <p> Note: this is
     * actually a string operation, that is, in Redis there are not "integer" types. Simply the
     * string stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then
     * converted back as a string. <p> Time complexity: O(1)
     *
     * @return Integer reply, this commands will reply with the new value of key after the
     * increment.
     * @see #incr(String)
     * @see #decr(String)
     * @see #incrBy(String, long)
     */
    public Long decrBy(final String key, final long integer) {
        Jedis jedis = getResource();
        try {
            return jedis.decrBy(key, integer);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Decrement the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the decrement operation. <p>
     * INCR commands are limited to 64 bit signed integers. <p> Note: this is actually a string
     * operation, that is, in Redis there are not "integer" types. Simply the string stored at the
     * key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a
     * string. <p> Time complexity: O(1)
     *
     * @return Integer reply, this commands will reply with the new value of key after the
     * increment.
     * @see #incr(String)
     * @see #incrBy(String, long)
     * @see #decrBy(String, long)
     */
    public Long decr(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.decr(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * INCRBY work just like {@link #incr(String) INCR} but instead to increment by 1 the increment
     * is integer. <p> INCR commands are limited to 64 bit signed integers. <p> Note: this is
     * actually a string operation, that is, in Redis there are not "integer" types. Simply the
     * string stored at the key is parsed as a base 10 64 bit signed integer, incremented, and then
     * converted back as a string. <p> Time complexity: O(1)
     *
     * @return Integer reply, this commands will reply with the new value of key after the
     * increment.
     * @see #incr(String)
     * @see #decr(String)
     * @see #decrBy(String, long)
     */
    public Long incrBy(final String key, final long integer) {
        Jedis jedis = getResource();
        try {
            return jedis.incrBy(key, integer);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * INCRBYFLOAT <p> INCRBYFLOAT commands are limited to double precision floating point values.
     * <p> Note: this is actually a string operation, that is, in Redis there are not "double"
     * types. Simply the string stored at the key is parsed as a base double precision floating
     * point value, incremented, and then converted back as a string. There is no DECRYBYFLOAT but
     * providing a negative value will work as expected. <p> Time complexity: O(1)
     *
     * @return Double reply, this commands will reply with the new value of key after the increment.
     */
    public Double incrByFloat(final String key, final double value) {
        Jedis jedis = getResource();
        try {
            return jedis.incrByFloat(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Increment the number stored at key by one. If the key does not exist or contains a value of a
     * wrong type, set the key to the value of "0" before to perform the increment operation. <p>
     * INCR commands are limited to 64 bit signed integers. <p> Note: this is actually a string
     * operation, that is, in Redis there are not "integer" types. Simply the string stored at the
     * key is parsed as a base 10 64 bit signed integer, incremented, and then converted back as a
     * string. <p> Time complexity: O(1)
     *
     * @return Integer reply, this commands will reply with the new value of key after the
     * increment.
     * @see #incrBy(String, long)
     * @see #decr(String)
     * @see #decrBy(String, long)
     */
    public Long incr(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.incr(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * If the key already exists and is a string, this command appends the provided value at the end
     * of the string. If the key does not exist it is created and set as an empty string, so APPEND
     * will be very similar to SET in this special case. <p> Time complexity: O(1). The amortized
     * time complexity is O(1) assuming the appended value is small and the already present value is
     * of any size, since the dynamic string library used by Redis will double the free space
     * available on every reallocation.
     *
     * @return Integer reply, specifically the total length of the string after the append
     * operation.
     */
    public Long append(final String key, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.append(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return a subset of the string from offset start to offset end (both offsets are inclusive).
     * Negative offsets can be used in order to provide an offset starting from the end of the
     * string. So -1 means the last char, -2 the penultimate and so forth. <p> The function handles
     * out of range requests without raising an error, but just limiting the resulting range to the
     * actual length of the string. <p> Time complexity: O(start+n) (with start being the start
     * index and n the total length of the requested range). Note that the lookup part of this
     * command is O(1) so for small strings this is actually an O(1) command.
     *
     * @return Bulk reply
     */
    public String substr(final String key, final int start, final int end) {
        Jedis jedis = getResource();
        try {
            return jedis.substr(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set the specified hash field to the specified value. <p> If key does not exist, a new key
     * holding a hash is created. <p> <b>Time complexity:</b> O(1)
     *
     * @return If the field already exists, and the HSET just produced an update of the value, 0 is
     * returned, otherwise if a new field is created 1 is returned.
     */
    public Long hset(final String key, final String field, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.hset(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * If key holds a hash, retrieve the value associated to the specified field. <p> If the field
     * is not found or the key does not exist, a special 'nil' value is returned. <p> <b>Time
     * complexity:</b> O(1)
     *
     * @return Bulk reply
     */
    public String hget(final String key, final String field) {
        Jedis jedis = getResource();
        try {
            return jedis.hget(key, field);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set the specified hash field to the specified value if the field not exists. <b>Time
     * complexity:</b> O(1)
     *
     * @return If the field already exists, 0 is returned, otherwise if a new field is created 1 is
     * returned.
     */
    public Long hsetnx(final String key, final String field, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.hsetnx(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set the respective fields to the respective values. HMSET replaces old values with new
     * values.
     * <p> If key does not exist, a new key holding a hash is created. <p> <b>Time complexity:</b>
     * O(N) (with N being the number of fields)
     *
     * @return Return OK or Exception if hash is empty
     */
    public String hmset(final String key, final Map<String, String> hash) {
        Jedis jedis = getResource();
        try {
            return jedis.hmset(key, hash);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Retrieve the values associated to the specified fields. <p> If some of the specified fields
     * do not exist, nil values are returned. Non existing keys are considered like empty hashes.
     * <p>
     * <b>Time complexity:</b> O(N) (with N being the number of fields)
     *
     * @return Multi Bulk Reply specifically a list of all the values associated with the specified
     * fields, in the same order of the request.
     */
    public List<String> hmget(final String key, final String... fields) {
        Jedis jedis = getResource();
        try {
            return jedis.hmget(key, fields);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Increment the number stored at field in the hash at key by value. If key does not exist, a
     * new key holding a hash is created. If field does not exist or holds a string, the value is
     * set to 0 before applying the operation. Since the value argument is signed you can use this
     * command to perform both increments and decrements. <p> The range of values supported by
     * HINCRBY is limited to 64 bit signed integers. <p> <b>Time complexity:</b> O(1)
     *
     * @return Integer reply The new value at field after the increment operation.
     */
    public Long hincrBy(final String key, final String field, final long value) {
        Jedis jedis = getResource();
        try {
            return jedis.hincrBy(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Increment the number stored at field in the hash at key by a double precision floating point
     * value. If key does not exist, a new key holding a hash is created. If field does not exist or
     * holds a string, the value is set to 0 before applying the operation. Since the value argument
     * is signed you can use this command to perform both increments and decrements. <p> The range
     * of values supported by HINCRBYFLOAT is limited to double precision floating point values.
     * <p>
     * <b>Time complexity:</b> O(1)
     *
     * @return Double precision floating point reply The new value at field after the increment
     * operation.
     */
    public Double hincrByFloat(final String key, final String field, final double value) {
        Jedis jedis = getResource();
        try {
            return jedis.hincrByFloat(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Test for existence of a specified field in a hash. <b>Time complexity:</b> O(1)
     *
     * @return Return 1 if the hash stored at key contains the specified field. Return 0 if the key
     * is not found or the field is not present.
     */
    public Boolean hexists(final String key, final String field) {
        Jedis jedis = getResource();
        try {
            return jedis.hexists(key, field);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove the specified field from an hash stored at key. <p> <b>Time complexity:</b> O(1)
     *
     * @return If the field was present in the hash it is deleted and 1 is returned, otherwise 0 is
     * returned and no operation is performed.
     */
    public Long hdel(final String key, final String... fields) {
        Jedis jedis = getResource();
        try {
            return jedis.hdel(key, fields);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the number of items in a hash. <p> <b>Time complexity:</b> O(1)
     *
     * @return The number of entries (fields) contained in the hash stored at key. If the specified
     * key does not exist, 0 is returned assuming an empty hash.
     */
    public Long hlen(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.hlen(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return all the fields in a hash. <p> <b>Time complexity:</b> O(N), where N is the total
     * number of entries
     *
     * @return All the fields names contained into a hash.
     */
    public Set<String> hkeys(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.hkeys(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return all the values in a hash. <p> <b>Time complexity:</b> O(N), where N is the total
     * number of entries
     *
     * @return All the fields values contained into a hash.
     */
    public List<String> hvals(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.hvals(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return all the fields and associated values in a hash. <p> <b>Time complexity:</b> O(N),
     * where N is the total number of entries
     *
     * @return All the fields and values contained into a hash.
     */
    public Map<String, String> hgetAll(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.hgetAll(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the
     * key does not exist an empty list is created just before the append operation. If the key
     * exists but is not a List an error is returned. <p> Time complexity: O(1)
     *
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     */
    public Long rpush(final String key, final String... strings) {
        Jedis jedis = getResource();
        try {
            return jedis.rpush(key, strings);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Add the string value to the head (LPUSH) or tail (RPUSH) of the list stored at key. If the
     * key does not exist an empty list is created just before the append operation. If the key
     * exists but is not a List an error is returned. <p> Time complexity: O(1)
     *
     * @return Integer reply, specifically, the number of elements inside the list after the push
     * operation.
     */
    public Long lpush(final String key, final String... strings) {
        Jedis jedis = getResource();
        try {
            return jedis.lpush(key, strings);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the length of the list stored at the specified key. If the key does not exist zero is
     * returned (the same behaviour as for empty lists). If the value stored at key is not a list an
     * error is returned. <p> Time complexity: O(1)
     *
     * @return The length of the list.
     */
    public Long llen(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.llen(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the specified elements of the list stored at the specified key. Start and end are
     * zero-based indexes. 0 is the first element of the list (the list head), 1 the next element
     * and so on. <p> For example LRANGE foobar 0 2 will return the first three elements of the
     * list. <p> start and end can also be negative numbers indicating offsets from the end of the
     * list. For example -1 is the last element of the list, -2 the penultimate element and so on.
     * <p>
     * <b>Consistency with range functions in various programming languages</b> <p> Note that if
     * you
     * have a list of numbers from 0 to 100, LRANGE 0 10 will return 11 elements, that is, rightmost
     * item is included. This may or may not be consistent with behavior of range-related functions
     * in your programming language of choice (think Ruby's Range.new, Array#slice or Python's
     * range() function). <p> LRANGE behavior is consistent with one of Tcl. <p> <b>Out-of-range
     * indexes</b>
     * <p> Indexes out of range will not produce an error: if start is over the end of the list, or
     * start &gt; end, an empty list is returned. If end is over the end of the list Redis will
     * threat it just like the last element of the list. <p> Time complexity: O(start+n) (with n
     * being the length of the range and start being the start offset)
     *
     * @return Multi bulk reply, specifically a list of elements in the specified range.
     */
    public List<String> lrange(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.lrange(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Trim an existing list so that it will contain only the specified range of elements specified.
     * Start and end are zero-based indexes. 0 is the first element of the list (the list head), 1
     * the next element and so on. <p> For example LTRIM foobar 0 2 will modify the list stored at
     * foobar key so that only the first three elements of the list will remain. <p> start and end
     * can also be negative numbers indicating offsets from the end of the list. For example -1 is
     * the last element of the list, -2 the penultimate element and so on. <p> Indexes out of range
     * will not produce an error: if start is over the end of the list, or start &gt; end, an empty
     * list is left as value. If end over the end of the list Redis will threat it just like the
     * last element of the list. <p> Hint: the obvious use of LTRIM is together with LPUSH/RPUSH.
     * For example: <p> {@code lpush("mylist", "someelement"); ltrim("mylist", 0, 99); * } <p> The
     * above two commands will push elements in the list taking care that the list will not grow
     * without limits. This is very useful when using Redis to baseservice logs for example. It is
     * important to note that when used in this way LTRIM is an O(1) operation because in the
     * average case just one element is removed from the tail of the list. <p> Time complexity: O(n)
     * (with n being len of list - len of range)
     *
     * @return Status code reply
     */
    public String ltrim(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.ltrim(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the specified element of the list stored at the specified key. 0 is the first element,
     * 1 the second and so on. Negative indexes are supported, for example -1 is the last element,
     * -2 the penultimate and so on. <p> If the value stored at key is not of list type an error is
     * returned. If the index is out of range a 'nil' reply is returned. <p> Note that even if the
     * average time complexity is O(n) asking for the first or the last element of the list is
     * O(1).
     * <p> Time complexity: O(n) (with n being the length of the list)
     *
     * @return Bulk reply, specifically the requested element
     */
    public String lindex(final String key, final long index) {
        Jedis jedis = getResource();
        try {
            return jedis.lindex(key, index);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Set a new value as the element at index position of the List at key. <p> Out of range indexes
     * will generate an error. <p> Similarly to other list commands accepting indexes, the index can
     * be negative to access elements starting from the end of the list. So -1 is the last element,
     * -2 is the penultimate, and so forth. <p> <b>Time complexity:</b> <p> O(N) (with N being the
     * length of the list), setting the first or last elements of the list is O(1).
     *
     * @return Status code reply
     * @see #lindex(String, long)
     */
    public String lset(final String key, final long index, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.lset(key, index, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove the first count occurrences of the value element from the list. If count is zero all
     * the elements are removed. If count is negative elements are removed from tail to head,
     * instead to go from head to tail that is the normal behaviour. So for example LREM with count
     * -2 and hello as value to remove against the list (a,b,c,hello,x,hello,hello) will lave the
     * list (a,b,c,hello,x). The number of removed elements is returned as an integer, see below for
     * more information about the returned value. Note that non existing keys are considered like
     * empty lists by LREM, so LREM against non existing keys will always return 0. <p> Time
     * complexity: O(N) (with N being the length of the list)
     *
     * @return Integer Reply, specifically: The number of removed elements if the operation
     * succeeded
     */
    public Long lrem(final String key, final long count, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.lrem(key, count, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" LPOP will return "a" and the list will become
     * "b","c". <p> If the key does not exist or the list is already empty the special value 'nil'
     * is returned.
     *
     * @return Bulk reply
     * @see #rpop(String)
     */
    public String lpop(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.lpop(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Atomically return and remove the first (LPOP) or last (RPOP) element of the list. For example
     * if the list contains the elements "a","b","c" RPOP will return "c" and the list will become
     * "a","b". <p> If the key does not exist or the list is already empty the special value 'nil'
     * is returned.
     *
     * @return Bulk reply
     * @see #lpop(String)
     */
    public String rpop(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.rpop(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Atomically return and remove the last (tail) element of the srckey list, and push the element
     * as the first (head) element of the dstkey list. For example if the source list contains the
     * elements "a","b","c" and the destination list contains the elements "foo","bar" after an
     * RPOPLPUSH command the content of the two lists will be "a","b" and "c","foo","bar". <p> If
     * the key does not exist or the list is already empty the special value 'nil' is returned. If
     * the srckey and dstkey are the same the operation is equivalent to removing the last element
     * from the list and pusing it as first element of the list, so it's a "list rotation" command.
     * <p> Time complexity: O(1)
     *
     * @return Bulk reply
     */
    public String rpoplpush(final String srckey, final String dstkey) {
        Jedis jedis = getResource();
        try {
            return jedis.rpoplpush(srckey, dstkey);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Add the specified member to the set value stored at key. If member is already a member of the
     * set no operation is performed. If key does not exist a new set with the specified member as
     * sole member is created. If the key exists but does not hold a set value an error is
     * returned.
     * <p> Time complexity O(1)
     *
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the set
     */
    public Long sadd(final String key, final String... members) {
        Jedis jedis = getResource();
        try {
            return jedis.sadd(key, members);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return all the members (elements) of the set value stored at key. This is just syntax glue
     * for {@link #sinter(String...) SINTER}. <p> Time complexity O(N)
     *
     * @return Multi bulk reply
     */
    public Set<String> smembers(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.smembers(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove the specified member from the set value stored at key. If member was not a member of
     * the set no operation is performed. If key does not hold a set value an error is returned. <p>
     * Time complexity O(1)
     *
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element
     * was not a member of the set
     */
    public Long srem(final String key, final String... members) {
        Jedis jedis = getResource();
        try {
            return jedis.srem(key, members);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove a random element from a Set returning it as return value. If the Set is empty or the
     * key does not exist, a nil object is returned. <p> The {@link #srandmember(String)} command
     * does a similar work but the returned element is not removed from the Set. <p> Time complexity
     * O(1)
     *
     * @return Bulk reply
     */
    public String spop(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.spop(key);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> spop(final String key, final long count) {
        Jedis jedis = getResource();
        try {
            return jedis.spop(key, count);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Move the specifided member from the set at srckey to the set at dstkey. This operation is
     * atomic, in every given moment the element will appear to be in the source or destination set
     * for accessing clients. <p> If the source set does not exist or does not contain the specified
     * element no operation is performed and zero is returned, otherwise the element is removed from
     * the source set and added to the destination set. On success one is returned, even if the
     * element was already present in the destination set. <p> An error is raised if the source or
     * destination keys contain a non Set value. <p> Time complexity O(1)
     *
     * @return Integer reply, specifically: 1 if the element was moved 0 if the element was not
     * found on the first set and no operation was performed
     */
    public Long smove(final String srckey, final String dstkey, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.smove(srckey, dstkey, member);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the set cardinality (number of elements). If the key does not exist 0 is returned,
     * like for empty sets.
     *
     * @return Integer reply, specifically: the cardinality (number of elements) of the set as an
     * integer.
     */
    public Long scard(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.scard(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return 1 if member is a member of the set stored at key, otherwise 0 is returned. <p> Time
     * complexity O(1)
     *
     * @return Integer reply, specifically: 1 if the element is a member of the set 0 if the element
     * is not a member of the set OR if the key does not exist
     */
    public Boolean sismember(final String key, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.sismember(key, member);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the members of a set resulting from the intersection of all the sets hold at the
     * specified keys. Like in {@link #lrange(String, long, long) LRANGE} the result is sent to the
     * client as a multi-bulk reply (see the protocol specification for more information). If just a
     * single key is specified, then this command produces the same result as {@link
     * #smembers(String) SMEMBERS}. Actually SMEMBERS is just syntax sugar for SINTER. <p> Non
     * existing keys are considered like empty sets, so if one of the keys is missing an empty set
     * is returned (since the intersection with an empty set always is an empty set). <p> Time
     * complexity O(N*M) worst case where N is the cardinality of the smallest set and M the number
     * of sets
     *
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<String> sinter(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sinter(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * This commnad works exactly like {@link #sinter(String...) SINTER} but instead of being
     * returned the resulting set is sotred as dstkey. <p> Time complexity O(N*M) worst case where N
     * is the cardinality of the smallest set and M the number of sets
     *
     * @return Status code reply
     */
    public Long sinterstore(final String dstkey, final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sinterstore(dstkey, keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the members of a set resulting from the union of all the sets hold at the specified
     * keys. Like in {@link #lrange(String, long, long) LRANGE} the result is sent to the client as
     * a multi-bulk reply (see the protocol specification for more information). If just a single
     * key is specified, then this command produces the same result as {@link #smembers(String)
     * SMEMBERS}.
     * <p> Non existing keys are considered like empty sets. <p> Time complexity O(N) where N is
     * the
     * total number of elements in all the provided sets
     *
     * @return Multi bulk reply, specifically the list of common elements.
     */
    public Set<String> sunion(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sunion(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * This command works exactly like {@link #sunion(String...) SUNION} but instead of being
     * returned the resulting set is stored as dstkey. Any existing value in dstkey will be
     * over-written. <p> Time complexity O(N) where N is the total number of elements in all the
     * provided sets
     *
     * @return Status code reply
     */
    public Long sunionstore(final String dstkey, final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sunionstore(dstkey, keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the difference between the Set stored at key1 and all the Sets key2, ..., keyN <p>
     * <b>Example:</b>
     *
     * <pre>
     * key1 = [x, a, b, c]
     * key2 = [c]
     * key3 = [a, d]
     * SDIFF key1,key2,key3 =&gt; [x, b]
     * </pre>
     *
     * Non existing keys are considered like empty sets. <p> <b>Time complexity:</b> <p> O(N) with N
     * being the total number of elements of all the sets
     *
     * @return Return the members of a set resulting from the difference between the first set
     * provided and all the successive sets.
     */
    public Set<String> sdiff(final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sdiff(keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * This command works exactly like {@link #sdiff(String...) SDIFF} but instead of being returned
     * the resulting set is stored in dstkey.
     *
     * @return Status code reply
     */
    public Long sdiffstore(final String dstkey, final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.sdiffstore(dstkey, keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return a random element from a Set, without removing the element. If the Set is empty or the
     * key does not exist, a nil object is returned. <p> The SPOP command does a similar work but
     * the returned element is popped (removed) from the Set. <p> Time complexity O(1)
     *
     * @return Bulk reply
     */
    public String srandmember(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.srandmember(key);
        } finally {
            returnResource(jedis);
        }
    }

    public List<String> srandmember(final String key, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.srandmember(key, count);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Add the specified member having the specifeid score to the sorted set stored at key. If
     * member is already a member of the sorted set the score is updated, and the element reinserted
     * in the right position to ensure sorting. If key does not exist a new sorted set with the
     * specified member as sole member is crated. If the key exists but does not hold a sorted set
     * value an error is returned. <p> The score value can be the string representation of a double
     * precision floating point number. <p> Time complexity O(logger(N)) with N being the number of
     * elements in the sorted set
     *
     * @return Integer reply, specifically: 1 if the new element was added 0 if the element was
     * already a member of the sorted set and the score was updated
     */
    public Long zadd(final String key, final double score, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.zadd(key, score, member);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zadd(final String key, final double score, final String member,
                     final ZAddParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.zadd(key, score, member, params);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zadd(final String key, final Map<String, Double> scoreMembers) {
        Jedis jedis = getResource();
        try {
            return jedis.zadd(key, scoreMembers);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.zadd(key, scoreMembers, params);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrange(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.zrange(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove the specified member from the sorted set value stored at key. If member was not a
     * member of the set no operation is performed. If key does not not hold a set value an error is
     * returned. <p> Time complexity O(logger(N)) with N being the number of elements in the sorted
     * set
     *
     * @return Integer reply, specifically: 1 if the new element was removed 0 if the new element
     * was not a member of the set
     */
    public Long zrem(final String key, final String... members) {
        Jedis jedis = getResource();
        try {
            return jedis.zrem(key, members);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * If member already exists in the sorted set adds the increment to its score and updates the
     * position of the element in the sorted set accordingly. If member does not already exist in
     * the sorted set it is added with increment as score (that is, like if the previous score was
     * virtually zero). If key does not exist a new sorted set with the specified member as sole
     * member is crated. If the key exists but does not hold a sorted set value an error is
     * returned.
     * <p> The score value can be the string representation of a double precision floating point
     * number. It's possible to provide a negative value to perform a decrement. <p> For an
     * introduction to sorted sets check the Introduction to Redis data types page. <p> Time
     * complexity O(logger(N)) with N being the number of elements in the sorted set
     *
     * @return The new score
     */
    public Double zincrby(final String key, final double score, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.zincrby(key, score, member);
        } finally {
            returnResource(jedis);
        }
    }

    public Double zincrby(String key, double score, String member, ZIncrByParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.zincrby(key, score, member, params);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * low to high. <p> When the given member does not exist in the sorted set, the special value
     * 'nil' is returned. The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b> <p> O(logger(N))
     *
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an
     * integer reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrevrank(String, String)
     */
    public Long zrank(final String key, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.zrank(key, member);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the rank (or index) or member in the sorted set at key, with scores being ordered from
     * high to low. <p> When the given member does not exist in the sorted set, the special value
     * 'nil' is returned. The returned rank (or index) of the member is 0-based for both commands.
     * <p>
     * <b>Time complexity:</b> <p> O(logger(N))
     *
     * @return Integer reply or a nil bulk reply, specifically: the rank of the element as an
     * integer reply if the element exists. A nil bulk reply if there is no such element.
     * @see #zrank(String, String)
     */
    public Long zrevrank(final String key, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrank(key, member);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrange(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrange(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrangeWithScores(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeWithScores(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeWithScores(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeWithScores(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the sorted set cardinality (number of elements). If the key does not exist 0 is
     * returned, like for empty sorted sets. <p> Time complexity O(1)
     *
     * @return the cardinality (number of elements) of the set as an integer.
     */
    public Long zcard(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.zcard(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the score of the specified element of the sorted set at key. If the specified element
     * does not exist in the sorted set, or the key does not exist at all, a special 'nil' value is
     * returned. <p> <b>Time complexity:</b> O(1)
     *
     * @return the score
     */
    public Double zscore(final String key, final String member) {
        Jedis jedis = getResource();
        try {
            return jedis.zscore(key, member);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Sort a Set or a List. <p> Sort the elements contained in the List, Set, or Sorted Set value
     * at key. By default sorting is numeric with elements being compared as double precision
     * floating point numbers. This is the simplest form of SORT.
     *
     * @return Assuming the Set/List at key contains a list of numbers, the return value will be the
     * list of numbers ordered from the smallest to the biggest number.
     * @see #sort(String, String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     */
    public List<String> sort(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.sort(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters. <p> <b>examples:</b> <p> Given
     * are the following sets and key/values:
     *
     * <pre>
     * x = [1, 2, 3]
     * y = [a, b, c]
     *
     * k1 = z
     * k2 = y
     * k3 = x
     *
     * w1 = 9
     * w2 = 8
     * w3 = 7
     * </pre>
     *
     * Sort Order:
     *
     * <pre>
     * sort(x) or sort(x, sp.asc())
     * -&gt; [1, 2, 3]
     *
     * sort(x, sp.desc())
     * -&gt; [3, 2, 1]
     *
     * sort(y)
     * -&gt; [c, a, b]
     *
     * sort(y, sp.alpha())
     * -&gt; [a, b, c]
     *
     * sort(y, sp.alpha().desc())
     * -&gt; [c, a, b]
     * </pre>
     *
     * Limit (e.g. for Pagination):
     *
     * <pre>
     * sort(x, sp.limit(0, 2))
     * -&gt; [1, 2]
     *
     * sort(y, sp.alpha().desc().limit(1, 2))
     * -&gt; [b, a]
     * </pre>
     *
     * Sorting by external keys:
     *
     * <pre>
     * sort(x, sb.by(w*))
     * -&gt; [3, 2, 1]
     *
     * sort(x, sb.by(w*).desc())
     * -&gt; [1, 2, 3]
     * </pre>
     *
     * Getting external keys:
     *
     * <pre>
     * sort(x, sp.by(w*).get(k*))
     * -&gt; [x, y, z]
     *
     * sort(x, sp.by(w*).get(#).get(k*))
     * -&gt; [3, x, 2, y, 1, z]
     * </pre>
     *
     * @return a list of sorted elements.
     * @see #sort(String)
     * @see #sort(String, SortingParams, String)
     */
    public List<String> sort(final String key, final SortingParams sortingParameters) {
        Jedis jedis = getResource();
        try {
            return jedis.sort(key, sortingParameters);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists. <p> The following is a description of the exact semantic. We describe BLPOP but the
     * two commands are identical, the only difference is that BLPOP pops the element from the left
     * (head) of the list, and BRPOP pops from the right (tail). <p> <b>Non blocking behavior</b>
     * <p> When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value). <p> Keys are scanned from left to right, so for instance if you issue BLPOP
     * list1 list2 list3 0 against a dataset where list1 does not exist but list2 and list3 contain
     * non empty lists, BLPOP guarantees to return an element from the list stored at list2 (since
     * it is the first non empty list starting from the left). <p> <b>Blocking behavior</b> <p> If
     * none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists. <p> Once new data is
     * present on one of the lists, the client finally returns with the name of the key unblocking
     * it and the popped value. <p> When blocking, if a non-zero timeout is specified, the client
     * will unblock returning a nil special value if the specified amount of seconds passed without
     * a push operation against at least one of the specified keys. <p> The timeout argument is
     * interpreted as an integer value. A timeout of zero means instead to block forever. <p>
     * <b>Multiple clients blocking for the same keys</b> <p> Multiple clients can block for the
     * same key. They are put into a queue, so the first to be served will be the one that started
     * to wait earlier, in a first-blpopping first-served fashion. <p> <b>blocking POP inside a
     * MULTI/EXEC transaction</b> <p> BLPOP and BRPOP can be used with pipelining (sending multiple
     * commands and reading the replies in batch), but it does not make sense to use BLPOP or BRPOP
     * inside a MULTI/EXEC block (a Redis transaction). <p> The behavior of BLPOP inside MULTI/EXEC
     * when the list is empty is to return a multi-bulk nil reply, exactly what happens when the
     * timeout is reached. If you like science fiction, think at it like if inside MULTI/EXEC the
     * time will flow at infinite speed :) <p> Time complexity: O(1)
     *
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value. <p> When a non-zero timeout is specified, and the BLPOP
     * operation timed out, the return value is a nil multi bulk reply. Most client values will
     * return false or nil accordingly to the programming language used.
     * @see #brpop(int, String...)
     */
    public List<String> blpop(final int timeout, final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.blpop(timeout, keys);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Sort a Set or a List accordingly to the specified parameters and baseservice the result at dstkey.
     *
     * @return The number of elements of the list at dstkey.
     * @see #sort(String, SortingParams)
     * @see #sort(String)
     * @see #sort(String, String)
     */
    public Long sort(final String key, final SortingParams sortingParameters, final String dstkey) {
        Jedis jedis = getResource();
        try {
            return jedis.sort(key, sortingParameters, dstkey);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Sort a Set or a List and Store the Result at dstkey. <p> Sort the elements contained in the
     * List, Set, or Sorted Set value at key and baseservice the result at dstkey. By default sorting is
     * numeric with elements being compared as double precision floating point numbers. This is the
     * simplest form of SORT.
     *
     * @return The number of elements of the list at dstkey.
     * @see #sort(String)
     * @see #sort(String, SortingParams)
     * @see #sort(String, SortingParams, String)
     */
    public Long sort(final String key, final String dstkey) {
        Jedis jedis = getResource();
        try {
            return jedis.sort(key, dstkey);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * BLPOP (and BRPOP) is a blocking list pop primitive. You can see this commands as blocking
     * versions of LPOP and RPOP able to block if the specified keys don't exist or contain empty
     * lists. <p> The following is a description of the exact semantic. We describe BLPOP but the
     * two commands are identical, the only difference is that BLPOP pops the element from the left
     * (head) of the list, and BRPOP pops from the right (tail). <p> <b>Non blocking behavior</b>
     * <p> When BLPOP is called, if at least one of the specified keys contain a non empty list, an
     * element is popped from the head of the list and returned to the caller together with the name
     * of the key (BLPOP returns a two elements array, the first element is the key, the second the
     * popped value). <p> Keys are scanned from left to right, so for instance if you issue BLPOP
     * list1 list2 list3 0 against a dataset where list1 does not exist but list2 and list3 contain
     * non empty lists, BLPOP guarantees to return an element from the list stored at list2 (since
     * it is the first non empty list starting from the left). <p> <b>Blocking behavior</b> <p> If
     * none of the specified keys exist or contain non empty lists, BLPOP blocks until some other
     * client performs a LPUSH or an RPUSH operation against one of the lists. <p> Once new data is
     * present on one of the lists, the client finally returns with the name of the key unblocking
     * it and the popped value. <p> When blocking, if a non-zero timeout is specified, the client
     * will unblock returning a nil special value if the specified amount of seconds passed without
     * a push operation against at least one of the specified keys. <p> The timeout argument is
     * interpreted as an integer value. A timeout of zero means instead to block forever. <p>
     * <b>Multiple clients blocking for the same keys</b> <p> Multiple clients can block for the
     * same key. They are put into a queue, so the first to be served will be the one that started
     * to wait earlier, in a first-blpopping first-served fashion. <p> <b>blocking POP inside a
     * MULTI/EXEC transaction</b> <p> BLPOP and BRPOP can be used with pipelining (sending multiple
     * commands and reading the replies in batch), but it does not make sense to use BLPOP or BRPOP
     * inside a MULTI/EXEC block (a Redis transaction). <p> The behavior of BLPOP inside MULTI/EXEC
     * when the list is empty is to return a multi-bulk nil reply, exactly what happens when the
     * timeout is reached. If you like science fiction, think at it like if inside MULTI/EXEC the
     * time will flow at infinite speed :) <p> Time complexity: O(1)
     *
     * @return BLPOP returns a two-elements array via a multi bulk reply in order to return both the
     * unblocking key and the popped value. <p> When a non-zero timeout is specified, and the BLPOP
     * operation timed out, the return value is a nil multi bulk reply. Most client values will
     * return false or nil accordingly to the programming language used.
     * @see #blpop(int, String...)
     */
    public List<String> brpop(final int timeout, final String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.brpop(timeout, keys);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zcount(final String key, final double min, final double max) {
        Jedis jedis = getResource();
        try {
            return jedis.zcount(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zcount(final String key, final String min, final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zcount(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max). <p> The elements having the same score
     * are returned sorted lexicographically as ASCII strings (this follows from a property of Redis
     * sorted sets and does not involve further computation). <p> Using the optional {@link
     * #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a range of
     * the matching elements in an SQL-alike way. Note that if offset is large the commands needs to
     * traverse the list for offset elements and this adds up to the O(M) figure. <p> The {@link
     * #zcount(String, double, double) ZCOUNT} command is similar to {@link #zrangeByScore(String,
     * double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the specified
     * interval, it just returns the number of matching elements. <p> <b>Exclusive intervals and
     * infinity</b> <p> min and max can be -inf and +inf, so that you are not required to know
     * what's the greatest or smallest element in order to take, for instance, elements "up to a
     * given value". <p> Also while the interval is for default closed (inclusive) it's possible to
     * specify open intervals prefixing the score with a "(" character, so for instance: <p> {@code
     * ZRANGEBYSCORE zset (1.3 5} <p> Will return all the values with score &gt; 1.3 and &lt;= 5,
     * while for instance: <p> {@code ZRANGEBYSCORE zset (5 (10} <p> Will return all the values with
     * score &gt; 5 and &lt; 10 (5 and 10 excluded). <p> <b>Time complexity:</b> <p>
     * O(logger(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(logger(N))
     *
     * @param min a double or Double.MIN_VALUE for "-inf"
     * @param max a double or Double.MAX_VALUE for "+inf"
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, String, String)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<String> zrangeByScore(final String key, final double min, final double max) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScore(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max). <p> The elements having the same score
     * are returned sorted lexicographically as ASCII strings (this follows from a property of Redis
     * sorted sets and does not involve further computation). <p> Using the optional {@link
     * #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a range of
     * the matching elements in an SQL-alike way. Note that if offset is large the commands needs to
     * traverse the list for offset elements and this adds up to the O(M) figure. <p> The {@link
     * #zcount(String, double, double) ZCOUNT} command is similar to {@link #zrangeByScore(String,
     * double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the specified
     * interval, it just returns the number of matching elements. <p> <b>Exclusive intervals and
     * infinity</b> <p> min and max can be -inf and +inf, so that you are not required to know
     * what's the greatest or smallest element in order to take, for instance, elements "up to a
     * given value". <p> Also while the interval is for default closed (inclusive) it's possible to
     * specify open intervals prefixing the score with a "(" character, so for instance: <p> {@code
     * ZRANGEBYSCORE zset (1.3 5} <p> Will return all the values with score &gt; 1.3 and &lt;= 5,
     * while for instance: <p> {@code ZRANGEBYSCORE zset (5 (10} <p> Will return all the values with
     * score &gt; 5 and &lt; 10 (5 and 10 excluded). <p> <b>Time complexity:</b> <p>
     * O(logger(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(logger(N))
     *
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<String> zrangeByScore(final String key, final double min, final double max,
                                     final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrangeByScore(final String key, final String min, final String max,
                                     final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScore(key, min, max, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max). <p> The elements having the same score
     * are returned sorted lexicographically as ASCII strings (this follows from a property of Redis
     * sorted sets and does not involve further computation). <p> Using the optional {@link
     * #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a range of
     * the matching elements in an SQL-alike way. Note that if offset is large the commands needs to
     * traverse the list for offset elements and this adds up to the O(M) figure. <p> The {@link
     * #zcount(String, double, double) ZCOUNT} command is similar to {@link #zrangeByScore(String,
     * double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the specified
     * interval, it just returns the number of matching elements. <p> <b>Exclusive intervals and
     * infinity</b> <p> min and max can be -inf and +inf, so that you are not required to know
     * what's the greatest or smallest element in order to take, for instance, elements "up to a
     * given value". <p> Also while the interval is for default closed (inclusive) it's possible to
     * specify open intervals prefixing the score with a "(" character, so for instance: <p> {@code
     * ZRANGEBYSCORE zset (1.3 5} <p> Will return all the values with score &gt; 1.3 and &lt;= 5,
     * while for instance: <p> {@code ZRANGEBYSCORE zset (5 (10} <p> Will return all the values with
     * score &gt; 5 and &lt; 10 (5 and 10 excluded). <p> <b>Time complexity:</b> <p>
     * O(logger(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(logger(N))
     *
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min,
                                              final double max) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min,
                                              final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Return the all the elements in the sorted set at key with a score between min and max
     * (including elements with score equal to min or max). <p> The elements having the same score
     * are returned sorted lexicographically as ASCII strings (this follows from a property of Redis
     * sorted sets and does not involve further computation). <p> Using the optional {@link
     * #zrangeByScore(String, double, double, int, int) LIMIT} it's possible to get only a range of
     * the matching elements in an SQL-alike way. Note that if offset is large the commands needs to
     * traverse the list for offset elements and this adds up to the O(M) figure. <p> The {@link
     * #zcount(String, double, double) ZCOUNT} command is similar to {@link #zrangeByScore(String,
     * double, double) ZRANGEBYSCORE} but instead of returning the actual elements in the specified
     * interval, it just returns the number of matching elements. <p> <b>Exclusive intervals and
     * infinity</b> <p> min and max can be -inf and +inf, so that you are not required to know
     * what's the greatest or smallest element in order to take, for instance, elements "up to a
     * given value". <p> Also while the interval is for default closed (inclusive) it's possible to
     * specify open intervals prefixing the score with a "(" character, so for instance: <p> {@code
     * ZRANGEBYSCORE zset (1.3 5} <p> Will return all the values with score &gt; 1.3 and &lt;= 5,
     * while for instance: <p> {@code ZRANGEBYSCORE zset (5 (10} <p> Will return all the values with
     * score &gt; 5 and &lt; 10 (5 and 10 excluded). <p> <b>Time complexity:</b> <p>
     * O(logger(N))+O(M) with N being the number of elements in the sorted set and M the number of
     * elements returned by the command, so if M is constant (for instance you always ask for the
     * first ten elements with LIMIT) you can consider it O(logger(N))
     *
     * @return Multi bulk reply specifically a list of elements in the specified score range.
     * @see #zrangeByScore(String, double, double)
     * @see #zrangeByScore(String, double, double, int, int)
     * @see #zrangeByScoreWithScores(String, double, double)
     * @see #zrangeByScoreWithScores(String, double, double, int, int)
     * @see #zcount(String, double, double)
     */
    public Set<Tuple> zrangeByScoreWithScores(final String key, final double min, final double max,
                                              final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrangeByScoreWithScores(final String key, final String min, final String max,
                                              final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByScoreWithScores(key, min, max, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScore(key, max, min);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(final String key, final double max, final double min,
                                        final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max,
                                                 final double min) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max,
                                                 final double min, final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max,
                                                 final String min, final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByScore(final String key, final String max, final String min,
                                        final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScore(key, max, min, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<Tuple> zrevrangeByScoreWithScores(final String key, final String max,
                                                 final String min) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByScoreWithScores(key, max, min);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove all elements in the sorted set at key with rank between start and end. Start and end
     * are 0-based with rank 0 being the element with the lowest score. Both start and end can be
     * negative numbers, where they indicate offsets starting at the element with the highest rank.
     * For example: -1 is the element with the highest score, -2 the element with the second highest
     * score and so forth. <p> <b>Time complexity:</b> O(logger(N))+O(M) with N being the number of
     * elements in the sorted set and M the number of elements removed by the operation
     */
    public Long zremrangeByRank(final String key, final long start, final long end) {
        Jedis jedis = getResource();
        try {
            return jedis.zremrangeByRank(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Remove all the elements in the sorted set at key with a score between min and max (including
     * elements with score equal to min or max). <p> <b>Time complexity:</b> <p> O(logger(N))+O(M)
     * with N being the number of elements in the sorted set and M the number of elements removed by
     * the operation
     *
     * @return Integer reply, specifically the number of elements removed.
     */
    public Long zremrangeByScore(final String key, final double start, final double end) {
        Jedis jedis = getResource();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zremrangeByScore(final String key, final String start, final String end) {
        Jedis jedis = getResource();
        try {
            return jedis.zremrangeByScore(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it
     * at dstkey. It is mandatory to provide the number of input keys N, before passing the input
     * keys and the other (optional) arguments. <p> As the terms imply, the {@link
     * #zinterstore(String, String...) ZINTERSTORE} command requires an element to be present in
     * each of the given inputs to be inserted in the result. The {@link #zunionstore(String,
     * String...) ZUNIONSTORE} command inserts all elements across all inputs. <p> Using the WEIGHTS
     * option, it is possible to add weight to each input sorted set. This means that the score of
     * each element in the sorted set is first multiplied by this weight before being passed to the
     * aggregation. When this option is not given, all weights default to 1. <p> With the AGGREGATE
     * option, it's possible to specify how the results of the union or intersection are aggregated.
     * This option defaults to SUM, where the score of an element is summed across the inputs where
     * it exists. When this option is set to be either MIN or MAX, the resulting set will contain
     * the minimum or maximum score of an element across the inputs where it exists. <p> <b>Time
     * complexity:</b> O(N) + O(M logger(M)) with N being the sum of the sizes of the input sorted
     * sets, and M being the number of elements in the resulting sorted set
     *
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zunionstore(final String dstkey, final String... sets) {
        Jedis jedis = getResource();
        try {
            return jedis.zunionstore(dstkey, sets);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it
     * at dstkey. It is mandatory to provide the number of input keys N, before passing the input
     * keys and the other (optional) arguments. <p> As the terms imply, the {@link
     * #zinterstore(String, String...) ZINTERSTORE} command requires an element to be present in
     * each of the given inputs to be inserted in the result. The {@link #zunionstore(String,
     * String...) ZUNIONSTORE} command inserts all elements across all inputs. <p> Using the WEIGHTS
     * option, it is possible to add weight to each input sorted set. This means that the score of
     * each element in the sorted set is first multiplied by this weight before being passed to the
     * aggregation. When this option is not given, all weights default to 1. <p> With the AGGREGATE
     * option, it's possible to specify how the results of the union or intersection are aggregated.
     * This option defaults to SUM, where the score of an element is summed across the inputs where
     * it exists. When this option is set to be either MIN or MAX, the resulting set will contain
     * the minimum or maximum score of an element across the inputs where it exists. <p> <b>Time
     * complexity:</b> O(N) + O(M logger(M)) with N being the sum of the sizes of the input sorted
     * sets, and M being the number of elements in the resulting sorted set
     *
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zunionstore(final String dstkey, final ZParams params, final String... sets) {
        Jedis jedis = getResource();
        try {
            return jedis.zunionstore(dstkey, params, sets);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it
     * at dstkey. It is mandatory to provide the number of input keys N, before passing the input
     * keys and the other (optional) arguments. <p> As the terms imply, the {@link
     * #zinterstore(String, String...) ZINTERSTORE} command requires an element to be present in
     * each of the given inputs to be inserted in the result. The {@link #zunionstore(String,
     * String...) ZUNIONSTORE} command inserts all elements across all inputs. <p> Using the WEIGHTS
     * option, it is possible to add weight to each input sorted set. This means that the score of
     * each element in the sorted set is first multiplied by this weight before being passed to the
     * aggregation. When this option is not given, all weights default to 1. <p> With the AGGREGATE
     * option, it's possible to specify how the results of the union or intersection are aggregated.
     * This option defaults to SUM, where the score of an element is summed across the inputs where
     * it exists. When this option is set to be either MIN or MAX, the resulting set will contain
     * the minimum or maximum score of an element across the inputs where it exists. <p> <b>Time
     * complexity:</b> O(N) + O(M logger(M)) with N being the sum of the sizes of the input sorted
     * sets, and M being the number of elements in the resulting sorted set
     *
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zinterstore(final String dstkey, final String... sets) {
        Jedis jedis = getResource();
        try {
            return jedis.zinterstore(dstkey, sets);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Creates a union or intersection of N sorted sets given by keys k1 through kN, and stores it
     * at dstkey. It is mandatory to provide the number of input keys N, before passing the input
     * keys and the other (optional) arguments. <p> As the terms imply, the {@link
     * #zinterstore(String, String...) ZINTERSTORE} command requires an element to be present in
     * each of the given inputs to be inserted in the result. The {@link #zunionstore(String,
     * String...) ZUNIONSTORE} command inserts all elements across all inputs. <p> Using the WEIGHTS
     * option, it is possible to add weight to each input sorted set. This means that the score of
     * each element in the sorted set is first multiplied by this weight before being passed to the
     * aggregation. When this option is not given, all weights default to 1. <p> With the AGGREGATE
     * option, it's possible to specify how the results of the union or intersection are aggregated.
     * This option defaults to SUM, where the score of an element is summed across the inputs where
     * it exists. When this option is set to be either MIN or MAX, the resulting set will contain
     * the minimum or maximum score of an element across the inputs where it exists. <p> <b>Time
     * complexity:</b> O(N) + O(M logger(M)) with N being the sum of the sizes of the input sorted
     * sets, and M being the number of elements in the resulting sorted set
     *
     * @return Integer reply, specifically the number of elements in the sorted set at dstkey
     * @see #zunionstore(String, String...)
     * @see #zunionstore(String, ZParams, String...)
     * @see #zinterstore(String, String...)
     * @see #zinterstore(String, ZParams, String...)
     */
    public Long zinterstore(final String dstkey, final ZParams params, final String... sets) {
        Jedis jedis = getResource();
        try {
            return jedis.zinterstore(dstkey, params, sets);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zlexcount(final String key, final String min, final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zlexcount(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByLex(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrangeByLex(final String key, final String min, final String max,
                                   final int offset, final int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrangeByLex(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByLex(String key, String max, String min) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByLex(key, max, min);
        } finally {
            returnResource(jedis);
        }
    }

    public Set<String> zrevrangeByLex(String key, String max, String min, int offset, int count) {
        Jedis jedis = getResource();
        try {
            return jedis.zrevrangeByLex(key, max, min, offset, count);
        } finally {
            returnResource(jedis);
        }
    }

    public Long zremrangeByLex(final String key, final String min, final String max) {
        Jedis jedis = getResource();
        try {
            return jedis.zremrangeByLex(key, min, max);
        } finally {
            returnResource(jedis);
        }
    }

    public Long strlen(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.strlen(key);
        } finally {
            returnResource(jedis);
        }
    }

    public Long lpushx(final String key, final String... string) {
        Jedis jedis = getResource();
        try {
            return jedis.lpushx(key, string);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Undo a {@link #expire(String, int) expire} at turning the expire key into a normal key. <p>
     * Time complexity: O(1)
     *
     * @return Integer reply, specifically: 1: the key is now persist. 0: the key is not persist
     * (only happens when key not set).
     */
    public Long persist(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.persist(key);
        } finally {
            returnResource(jedis);
        }
    }

    public Long rpushx(final String key, final String... string) {
        Jedis jedis = getResource();
        try {
            return jedis.rpushx(key, string);
        } finally {
            returnResource(jedis);
        }
    }

    public String echo(final String string) {
        Jedis jedis = getResource();
        try {
            return jedis.echo(string);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Pop a value from a list, push it to another list and return it; or block until one is
     * available
     *
     * @return the element
     */
    public String brpoplpush(String source, String destination, int timeout) {
        Jedis jedis = getResource();
        try {
            return jedis.brpoplpush(source, destination, timeout);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Sets or clears the bit at offset in the string value stored at key
     */
    public Boolean setbit(String key, long offset, boolean value) {
        Jedis jedis = getResource();
        try {
            return jedis.setbit(key, offset, value);
        } finally {
            returnResource(jedis);
        }
    }

    public Boolean setbit(String key, long offset, String value) {
        Jedis jedis = getResource();
        try {
            return jedis.setbit(key, offset, value);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * Returns the bit value at offset in the string value stored at key
     */
    public Boolean getbit(String key, long offset) {
        Jedis jedis = getResource();
        try {
            return jedis.getbit(key, offset);
        } finally {
            returnResource(jedis);
        }
    }

    public Long setrange(String key, long offset, String value) {
        Jedis jedis = getResource();
        try {
            return jedis.setrange(key, offset, value);
        } finally {
            returnResource(jedis);
        }
    }

    public String getrange(String key, long startOffset, long endOffset) {
        Jedis jedis = getResource();
        try {
            return jedis.getrange(key, startOffset, endOffset);
        } finally {
            returnResource(jedis);
        }
    }

    public Long bitpos(final String key, final boolean value) {
        Jedis jedis = getResource();
        try {
            return jedis.bitpos(key, value);
        } finally {
            returnResource(jedis);
        }
    }

    public Long bitpos(final String key, final boolean value, final BitPosParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.bitpos(key, value, params);
        } finally {
            returnResource(jedis);
        }
    }

    public Object eval(String script, int keyCount, String... params) {
        Jedis jedis = getResource();
        try {
            return jedis.eval(script, keyCount, params);
        } finally {
            returnResource(jedis);
        }
    }

    public void subscribe(final JedisPubSub jedisPubSub, final String... channels) {
        Jedis jedis = getResource();
        try {
            jedis.subscribe(jedisPubSub, channels);
        } finally {
            returnResource(jedis);
        }
    }

    public Long publish(final String channel, final String message) {
        Jedis jedis = getResource();
        try {
            return jedis.publish(channel, message);
        } finally {
            returnResource(jedis);
        }
    }

    public void psubscribe(final JedisPubSub jedisPubSub, final String... patterns) {
        Jedis jedis = getResource();
        try {
            jedis.psubscribe(jedisPubSub, patterns);
        } finally {
            returnResource(jedis);
        }
    }

    public Object eval(String script, List<String> keys, List<String> args) {
        Jedis jedis = getResource();
        try {
            return jedis.eval(script, keys, args);
        } finally {
            returnResource(jedis);
        }
    }

    public Object eval(String script) {
        return eval(script, 0);
    }

    public Object evalsha(String script) {
        return evalsha(script, 0);
    }

    public Object evalsha(String sha1, List<String> keys, List<String> args) {
        Jedis jedis = getResource();
        try {
            return jedis.evalsha(sha1, keys, args);
        } finally {
            returnResource(jedis);
        }
    }

    public Object evalsha(String sha1, int keyCount, String... params) {
        Jedis jedis = getResource();
        try {
            return jedis.evalsha(sha1, keyCount, params);
        } finally {
            returnResource(jedis);
        }
    }

    public Boolean scriptExists(String sha1) {
        Jedis jedis = getResource();
        try {
            return jedis.scriptExists(sha1);
        } finally {
            returnResource(jedis);
        }
    }

    public List<Boolean> scriptExists(String... sha1) {
        Jedis jedis = getResource();
        try {
            return jedis.scriptExists(sha1);
        } finally {
            returnResource(jedis);
        }
    }

    public String scriptLoad(String script) {
        Jedis jedis = getResource();
        try {
            return jedis.scriptLoad(script);
        } finally {
            returnResource(jedis);
        }
    }

    public Long bitcount(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.bitcount(key);
        } finally {
            returnResource(jedis);
        }
    }

    public Long bitcount(final String key, long start, long end) {
        Jedis jedis = getResource();
        try {
            return jedis.bitcount(key, start, end);
        } finally {
            returnResource(jedis);
        }
    }

    public Long bitop(BitOP op, final String destKey, String... srcKeys) {
        Jedis jedis = getResource();
        try {
            return jedis.bitop(op, destKey, srcKeys);
        } finally {
            returnResource(jedis);
        }
    }

    public Long pexpire(final String key, final long milliseconds) {
        Jedis jedis = getResource();
        try {
            return jedis.pexpire(key, milliseconds);
        } finally {
            returnResource(jedis);
        }
    }

    public Long pexpireAt(final String key, final long millisecondsTimestamp) {
        Jedis jedis = getResource();
        try {
            return jedis.pexpireAt(key, millisecondsTimestamp);
        } finally {
            returnResource(jedis);
        }
    }

    public Long pttl(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.pttl(key);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * PSETEX works exactly like {@link #setex(String, int, String)} with the sole difference that
     * the expire time is specified in milliseconds instead of seconds. Time complexity: O(1)
     *
     * @return Status code reply
     */
    public String psetex(final String key, final long milliseconds, final String value) {
        Jedis jedis = getResource();
        try {
            return jedis.psetex(key, milliseconds, value);
        } finally {
            returnResource(jedis);
        }
    }

    public ScanResult<String> scan(final String cursor) {
        return scan(cursor, new ScanParams());
    }

    public ScanResult<String> scan(final String cursor, final ScanParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.scan(cursor, params);
        } finally {
            returnResource(jedis);
        }
    }

    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor) {
        return hscan(key, cursor, new ScanParams());
    }

    public ScanResult<Map.Entry<String, String>> hscan(final String key, final String cursor,
                                                       final ScanParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.hscan(key, cursor, params);
        } finally {
            returnResource(jedis);
        }
    }

    public ScanResult<String> sscan(final String key, final String cursor) {
        return sscan(key, cursor, new ScanParams());
    }

    public ScanResult<String> sscan(final String key, final String cursor,
                                    final ScanParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.sscan(key, cursor, params);
        } finally {
            returnResource(jedis);
        }
    }

    public ScanResult<Tuple> zscan(final String key, final String cursor) {
        return zscan(key, cursor, new ScanParams());
    }

    public ScanResult<Tuple> zscan(final String key, final String cursor, final ScanParams params) {
        Jedis jedis = getResource();
        try {
            return jedis.zscan(key, cursor, params);
        } finally {
            returnResource(jedis);
        }
    }

    public Long pfadd(final String key, final String... elements) {
        Jedis jedis = getResource();
        try {
            return jedis.pfadd(key, elements);
        } finally {
            returnResource(jedis);
        }
    }

    public long pfcount(final String key) {
        Jedis jedis = getResource();
        try {
            return jedis.pfcount(key);
        } finally {
            returnResource(jedis);
        }
    }

    public long pfcount(String... keys) {
        Jedis jedis = getResource();
        try {
            return jedis.pfcount(keys);
        } finally {
            returnResource(jedis);
        }
    }

    public String pfmerge(final String destkey, final String... sourcekeys) {
        Jedis jedis = getResource();
        try {
            return jedis.pfmerge(destkey, sourcekeys);
        } finally {
            returnResource(jedis);
        }
    }

    public List<String> blpop(int timeout, String key) {
        Jedis jedis = getResource();
        try {
            return jedis.blpop(timeout, key);
        } finally {
            returnResource(jedis);
        }
    }

    public List<String> brpop(int timeout, String key) {
        Jedis jedis = getResource();
        try {
            return jedis.brpop(timeout, key);
        } finally {
            returnResource(jedis);
        }
    }

    public Long geoadd(String key, double longitude, double latitude, String member) {
        Jedis jedis = getResource();
        try {
            return jedis.geoadd(key, longitude, latitude, member);
        } finally {
            returnResource(jedis);
        }
    }

    public Long geoadd(String key, Map<String, GeoCoordinate> memberCoordinateMap) {
        Jedis jedis = getResource();
        try {
            return jedis.geoadd(key, memberCoordinateMap);
        } finally {
            returnResource(jedis);
        }
    }

    public Double geodist(String key, String member1, String member2) {
        Jedis jedis = getResource();
        try {
            return jedis.geodist(key, member1, member2);
        } finally {
            returnResource(jedis);
        }
    }

    public Double geodist(String key, String member1, String member2, GeoUnit unit) {
        Jedis jedis = getResource();
        try {
            return jedis.geodist(key, member1, member2);
        } finally {
            returnResource(jedis);
        }
    }

    public List<String> geohash(String key, String... members) {
        Jedis jedis = getResource();
        try {
            return jedis.geohash(key, members);
        } finally {
            returnResource(jedis);
        }
    }

    public List<GeoCoordinate> geopos(String key, String... members) {
        Jedis jedis = getResource();
        try {
            return jedis.geopos(key, members);
        } finally {
            returnResource(jedis);
        }
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
                                             double radius, GeoUnit unit) {
        Jedis jedis = getResource();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit);
        } finally {
            returnResource(jedis);
        }
    }

    public List<GeoRadiusResponse> georadius(String key, double longitude, double latitude,
                                             double radius, GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis = getResource();
        try {
            return jedis.georadius(key, longitude, latitude, radius, unit, param);
        } finally {
            returnResource(jedis);
        }
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
                                                     GeoUnit unit) {
        Jedis jedis = getResource();
        try {
            return jedis.georadiusByMember(key, member, radius, unit);
        } finally {
            returnResource(jedis);
        }
    }

    public List<GeoRadiusResponse> georadiusByMember(String key, String member, double radius,
                                                     GeoUnit unit, GeoRadiusParam param) {
        Jedis jedis = getResource();
        try {
            return jedis.georadiusByMember(key, member, radius, unit, param);
        } finally {
            returnResource(jedis);
        }
    }

    public List<Long> bitfield(String key, String... arguments) {
        Jedis jedis = getResource();
        try {
            return jedis.bitfield(key, arguments);
        } finally {
            returnResource(jedis);
        }
    }

    /**
     * 获取Jedis对象
     *
     * @return Jedis
     */
    private Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * 释放jedis资源
     */
    public void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
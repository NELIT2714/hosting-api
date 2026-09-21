local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

local bucket = redis.call("HMGET", key, "tokens", "ts")
local tokens = tonumber(bucket[1])
local last_ts = tonumber(bucket[2])

if tokens == nil then
	tokens = capacity
	last_ts = now
end

local elapsed = math.max(0, now - last_ts)
tokens = math.min(capacity, tokens + elapsed * refill_rate)

local allowed = 0
if tokens >= requested then
	tokens = tokens - requested
	allowed = 1
end

tokens = tonumber(string.format("%.4f", tokens))

local wait_seconds = 0
if allowed == 0 then
	wait_seconds = math.ceil((requested - tokens) / refill_rate)
end

redis.call("HMSET", key, "tokens", tokens, "ts", now)
redis.call("EXPIRE", key, math.ceil(capacity / refill_rate) * 2)

return {allowed, tokens, wait_seconds}
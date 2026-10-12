package com.corely.corely_backend.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final int LOGIN_LIMIT = 10; // 10 req
    private static final int REGISTER_LIMIT = 5; // 5 req
    private static final int REFRESH_LIMIT = 20; // 20 req
    private static final long WINDOW_MS = 60_000; // 1 minute

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        String key = request.getRemoteAddr() + ":" + getBucket(path);

        int limit = getLimit(path);
        if (limit <= 0)
            return true;

        Bucket bucket = buckets.compute(key, (k, existing) -> {
            if (existing == null || System.currentTimeMillis() - existing.windowStart > WINDOW_MS) {
                return new Bucket(limit);
            }
            return existing;
        });

        int count = bucket.counter.getAndIncrement();
        response.setHeader("X-RateLimit-Limit", String.valueOf(limit));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, limit - count - 1)));

        if (count >= limit) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            try {
                response.getWriter().write("{\"error\":\"Too many requests. Please try again later.\",\"code\":429}");
            } catch (Exception ignored) {
            }
            return false;
        }
        return true;
    }

    private String getBucket(String path) {
        if (path.contains("/auth/login"))
            return "login";
        if (path.contains("/auth/register"))
            return "register";
        if (path.contains("/auth/refresh"))
            return "refresh";
        if (path.startsWith("/api/auth/"))
            return "auth";
        return "";
    }

    private int getLimit(String path) {
        if (path.contains("/auth/login"))
            return LOGIN_LIMIT;
        if (path.contains("/auth/register"))
            return REGISTER_LIMIT;
        if (path.contains("/auth/refresh"))
            return REFRESH_LIMIT;
        if (path.startsWith("/api/auth/"))
            return LOGIN_LIMIT;
        return 0;
    }

    private static class Bucket {
        final AtomicInteger counter = new AtomicInteger(0);
        final long windowStart = System.currentTimeMillis();

        Bucket(int limit) {
        }
    }
}

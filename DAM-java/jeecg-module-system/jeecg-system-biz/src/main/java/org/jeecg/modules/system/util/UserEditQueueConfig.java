package org.jeecg.modules.system.util;

import lombok.Data;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.*;

/**
 * 队列配置类：核心是为用户编辑请求创建单线程的线程池（保证串行执行）
 */
@Configuration
@EnableAsync
public class UserEditQueueConfig {

    /**
     * 按用户ID分片的队列（避免不同用户请求互相阻塞）
     * key: 用户ID，value: 该用户的编辑请求队列
     */
    private final ConcurrentHashMap<String, BlockingQueue<UserEditRequest>> userQueueMap = new ConcurrentHashMap<>();

    /**
     * 用户编辑请求实体
     */
    @Data
    public static class UserEditRequest {
        private SysUser user;
        private String roles;
        private String departs;
        // 可扩展：添加请求ID、创建时间等
    }

    /**
     * 获取指定用户的队列（不存在则创建）
     */
    public BlockingQueue<UserEditRequest> getUserQueue(String userId) {
        return userQueueMap.computeIfAbsent(userId, k -> new LinkedBlockingQueue<>(100)); // 限制队列长度，避免OOM
    }

    /**
     * 单线程执行器（每个用户一个线程，或全局单线程，根据并发量选择）
     * 这里选择全局单线程（简单版），高并发场景可改为按用户分片的线程池
     */
    @Bean("userEditExecutor")
    public Executor userEditExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, // 核心线程数：1（串行执行）
                1, // 最大线程数
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactory() {
                    private int count = 0;

                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "user-edit-thread-" + (++count));
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy() // 队列满时，由调用线程执行（避免请求丢失）
        );
        // 优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> executor.shutdown()));
        return executor;
    }
}

package com.edu.common.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 *
 * 提供测试环境配置和通用测试工具方法
 *
 * 使用方式：
 * <pre>
 * {@code
 * @SpringBootTest
 * class MyServiceTest extends BaseTest {
 *     @Autowired
 *     private MyService myService;
 *
 *     @Test
 *     void testMethod() {
 *         // 测试代码
 *     }
 * }
 * }
 * </pre>
 *
 * @author edu-system
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@Transactional
public abstract class BaseTest {

    /**
     * 生成随机字符串
     *
     * @param length 长度
     * @return 随机字符串
     */
    protected String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }

    /**
     * 生成随机整数
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     * @return 随机整数
     */
    protected int randomInt(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }

    /**
     * 生成随机长整数
     *
     * @param min 最小值（包含）
     * @param max 最大值（不包含）
     * @return 随机长整数
     */
    protected long randomLong(long min, long max) {
        return min + (long) (Math.random() * (max - min));
    }

    /**
     * 生成随机布尔值
     *
     * @return 随机布尔值
     */
    protected boolean randomBoolean() {
        return Math.random() < 0.5;
    }

    /**
     * 等待指定毫秒数
     *
     * @param millis 毫秒数
     */
    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 打印测试信息
     *
     * @param message 消息
     */
    protected void log(String message) {
        System.out.println("[TEST] " + message);
    }

    /**
     * 打印测试信息（带格式化）
     *
     * @param format 格式
     * @param args 参数
     */
    protected void log(String format, Object... args) {
        System.out.printf("[TEST] " + format + "%n", args);
    }
}

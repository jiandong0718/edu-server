package com.edu.common.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BaseTest示例测试
 *
 * 演示如何使用BaseTest基类
 *
 * @author edu-system
 * @since 1.0.0
 */
@DisplayName("BaseTest示例测试")
class BaseTestExample extends BaseTest {

    @Test
    @DisplayName("测试随机字符串生成")
    void testRandomString() {
        String str = randomString(10);
        assertThat(str).hasSize(10);
        log("生成的随机字符串: %s", str);
    }

    @Test
    @DisplayName("测试随机整数生成")
    void testRandomInt() {
        int num = randomInt(1, 100);
        assertThat(num).isBetween(1, 99);
        log("生成的随机整数: %d", num);
    }

    @Test
    @DisplayName("测试随机长整数生成")
    void testRandomLong() {
        long num = randomLong(1L, 1000L);
        assertThat(num).isBetween(1L, 999L);
        log("生成的随机长整数: %d", num);
    }

    @Test
    @DisplayName("测试随机布尔值生成")
    void testRandomBoolean() {
        boolean bool = randomBoolean();
        assertThat(bool).isIn(true, false);
        log("生成的随机布尔值: %b", bool);
    }
}

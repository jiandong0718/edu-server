package com.edu.notification.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 通知预览VO
 */
@Data
public class NotificationPreviewVO {

    /**
     * 接收人列表
     */
    private List<ReceiverInfo> receivers;

    /**
     * 接收人总数
     */
    private Integer totalCount;

    /**
     * 接收人信息
     */
    @Data
    public static class ReceiverInfo {
        /**
         * 用户ID
         */
        private Long userId;

        /**
         * 用户姓名
         */
        private String userName;

        /**
         * 手机号
         */
        private String phone;

        /**
         * 用户类型
         */
        private String userType;

        /**
         * 校区名称
         */
        private String campusName;
    }
}

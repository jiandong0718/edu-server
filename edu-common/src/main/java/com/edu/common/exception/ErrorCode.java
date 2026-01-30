package com.edu.common.exception;

/**
 * 错误码常量
 */
public interface ErrorCode {

    // ========== 通用错误 ==========
    int SUCCESS = 200;
    int BAD_REQUEST = 400;
    int UNAUTHORIZED = 401;
    int FORBIDDEN = 403;
    int NOT_FOUND = 404;
    int INTERNAL_ERROR = 500;

    // ========== 认证相关 1001-1099 ==========
    int USER_NOT_FOUND = 1001;
    int PASSWORD_ERROR = 1002;
    int ACCOUNT_LOCKED = 1003;
    int ACCOUNT_DISABLED = 1004;
    int TOKEN_EXPIRED = 1005;
    int TOKEN_INVALID = 1006;
    int LOGIN_REQUIRED = 1007;
    int PASSWORD_CHANGE_REQUIRED = 1008;

    // ========== 权限相关 1101-1199 ==========
    int NO_PERMISSION = 1101;
    int ROLE_NOT_FOUND = 1102;

    // ========== 参数校验 1201-1299 ==========
    int PARAM_ERROR = 1201;
    int PARAM_MISSING = 1202;

    // ========== 业务错误 2001-2999 ==========
    int DATA_NOT_FOUND = 2001;
    int DATA_ALREADY_EXISTS = 2002;
    int DATA_STATUS_ERROR = 2003;
    int OPERATION_NOT_ALLOWED = 2004;
}

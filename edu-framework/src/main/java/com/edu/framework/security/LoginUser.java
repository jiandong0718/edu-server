package com.edu.framework.security;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 登录用户信息
 */
@Data
public class LoginUser implements Serializable {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginUser.class);

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 校区ID
     */
    private Long campusId;

    /**
     * 校区名称
     */
    private String campusName;

    /**
     * 角色编码列表
     */
    private List<String> roles;

    /**
     * 权限标识列表
     */
    private List<String> permissions;

    /**
     * 登录时间
     */
    private Long loginTime;

    /**
     * 过期时间
     */
    private Long expireTime;

    /**
     * 登录IP
     */
    private String loginIp;

    /**
     * Token
     */
    private String token;

    // Getter methods (Lombok @Data not working with Java 23)
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRealName() {
        return realName;
    }

    public String getAvatar() {
        return avatar;
    }

    public Long getCampusId() {
        return campusId;
    }

    public String getCampusName() {
        return campusName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public Long getLoginTime() {
        return loginTime;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public String getToken() {
        return token;
    }

    // Setter methods
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCampusId(Long campusId) {
        this.campusId = campusId;
    }

    public void setCampusName(String campusName) {
        this.campusName = campusName;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public void setLoginTime(Long loginTime) {
        this.loginTime = loginTime;
    }

    public void setExpireTime(Long expireTime) {
        this.expireTime = expireTime;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

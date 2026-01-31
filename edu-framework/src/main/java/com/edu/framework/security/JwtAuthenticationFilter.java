package com.edu.framework.security;

import com.edu.framework.mybatis.CampusContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JWT 认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtTokenUtil jwtTokenUtil;

    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * 白名单路径
     */
    private static final List<String> WHITE_LIST = Arrays.asList(
            "/auth/login",
            "/auth/captcha",
            "/doc.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
            "/favicon.ico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // 白名单放行
        if (isWhiteListed(requestUri)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 获取 Token
        String token = jwtTokenUtil.getToken(request);
        if (!StringUtils.hasText(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录或登录已过期\"}");
            return;
        }

        // 验证 Token
        LoginUser loginUser = jwtTokenUtil.getLoginUser(token);
        if (loginUser == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\"}");
            return;
        }

        // 设置上下文
        SecurityContextHolder.setLoginUser(loginUser);
        CampusContextHolder.setCampusId(loginUser.getCampusId());

        try {
            // 刷新 Token 有效期（可选，根据需要开启）
            // jwtTokenUtil.refreshToken(loginUser);

            filterChain.doFilter(request, response);
        } finally {
            // 清除上下文
            SecurityContextHolder.clear();
            CampusContextHolder.clear();
        }
    }

    private boolean isWhiteListed(String requestUri) {
        return WHITE_LIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestUri));
    }
}

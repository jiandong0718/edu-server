# 构建阶段
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# 复制 pom 文件
COPY pom.xml .
COPY edu-common/pom.xml edu-common/
COPY edu-framework/pom.xml edu-framework/
COPY edu-system/pom.xml edu-system/
COPY edu-student/pom.xml edu-student/
COPY edu-teaching/pom.xml edu-teaching/
COPY edu-finance/pom.xml edu-finance/
COPY edu-marketing/pom.xml edu-marketing/
COPY edu-notification/pom.xml edu-notification/
COPY edu-admin/pom.xml edu-admin/

# 下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY edu-common/src edu-common/src
COPY edu-framework/src edu-framework/src
COPY edu-system/src edu-system/src
COPY edu-student/src edu-student/src
COPY edu-teaching/src edu-teaching/src
COPY edu-finance/src edu-finance/src
COPY edu-marketing/src edu-marketing/src
COPY edu-notification/src edu-notification/src
COPY edu-admin/src edu-admin/src

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="edu-admin"
LABEL description="教育机构学生管理系统后端服务"

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建非 root 用户
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# 创建必要的目录
RUN mkdir -p /data/files /app/logs && \
    chown -R appuser:appgroup /data /app

# 从构建阶段复制 jar 文件
COPY --from=builder /app/edu-admin/target/edu-admin-*.jar app.jar

# 切换到非 root 用户
USER appuser

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

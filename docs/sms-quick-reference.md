# 短信服务快速参考

## 快速开始

### 1. 配置短信服务

在 `application-dev.yml` 中添加配置：

```yaml
sms:
  provider: aliyun  # 或 tencent
  mock-enabled: true  # 开发环境使用Mock模式
  max-retry-count: 3
```

### 2. 发送单条短信

```java
@Autowired
private SmsService smsService;

SmsSendDTO dto = new SmsSendDTO();
dto.setPhone("13800138000");
dto.setContent("您的验证码是123456，5分钟内有效。");

SmsSendResultVO result = smsService.sendSms(dto);
```

### 3. 批量发送短信

```java
SmsBatchSendDTO dto = new SmsBatchSendDTO();
dto.setPhones(Arrays.asList("13800138000", "13800138001"));
dto.setContent("尊敬的家长，您的孩子今天表现优秀！");

SmsBatchSendResultVO result = smsService.sendBatchSms(dto);
```

### 4. 发送模板短信

```java
SmsTemplateSendDTO dto = new SmsTemplateSendDTO();
dto.setPhone("13800138000");
dto.setTemplateCode("VERIFY_CODE");

Map<String, String> params = new HashMap<>();
params.put("code", "123456");
params.put("time", "5");
dto.setParams(params);

SmsSendResultVO result = smsService.sendTemplateSms(dto);
```

## API接口

### 发送单条短信

```bash
POST /notification/sms/send
Content-Type: application/json

{
  "phone": "13800138000",
  "content": "您的验证码是123456，5分钟内有效。"
}
```

### 批量发送短信

```bash
POST /notification/sms/send-batch
Content-Type: application/json

{
  "phones": ["13800138000", "13800138001"],
  "content": "尊敬的家长，您的孩子今天表现优秀！"
}
```

### 发送模板短信

```bash
POST /notification/sms/send-template
Content-Type: application/json

{
  "phone": "13800138000",
  "templateCode": "VERIFY_CODE",
  "params": {
    "code": "123456",
    "time": "5"
  }
}
```

### 查询发送状态

```bash
GET /notification/sms/status/{id}
```

### 重试发送

```bash
POST /notification/sms/retry/{id}
```

## 核心类说明

| 类名 | 说明 | 位置 |
|------|------|------|
| SmsService | 短信服务接口 | service/SmsService.java |
| SmsServiceImpl | 短信服务实现 | service/impl/SmsServiceImpl.java |
| SmsSender | 短信发送器接口 | service/sms/SmsSender.java |
| AliyunSmsSenderImpl | 阿里云发送器 | service/sms/impl/AliyunSmsSenderImpl.java |
| TencentSmsSenderImpl | 腾讯云发送器 | service/sms/impl/TencentSmsSenderImpl.java |
| SmsSenderFactory | 发送器工厂 | service/sms/SmsSenderFactory.java |
| SmsProperties | 配置属性 | config/SmsProperties.java |
| SmsController | REST控制器 | controller/SmsController.java |

## 配置参数

| 参数 | 说明 | 默认值 |
|------|------|--------|
| sms.provider | 服务提供商（aliyun/tencent） | aliyun |
| sms.mock-enabled | 是否启用Mock模式 | true |
| sms.max-retry-count | 最大重试次数 | 3 |
| sms.aliyun.access-key-id | 阿里云AccessKey ID | - |
| sms.aliyun.access-key-secret | 阿里云AccessKey Secret | - |
| sms.aliyun.sign-name | 阿里云短信签名 | 教育管理系统 |
| sms.tencent.secret-id | 腾讯云Secret ID | - |
| sms.tencent.secret-key | 腾讯云Secret Key | - |
| sms.tencent.sdk-app-id | 腾讯云SDK App ID | - |
| sms.tencent.sign-name | 腾讯云短信签名 | 教育管理系统 |

## 常用模板

| 模板编码 | 说明 | 参数 |
|----------|------|------|
| VERIFY_CODE | 验证码 | code: 验证码<br>time: 有效时间 |
| NOTIFY | 通知 | content: 通知内容 |

## 发送状态

| 状态 | 说明 |
|------|------|
| pending | 待发送 |
| sending | 发送中 |
| success | 发送成功 |
| failed | 发送失败 |

## 切换服务提供商

只需修改配置：

```yaml
# 使用阿里云
sms:
  provider: aliyun

# 使用腾讯云
sms:
  provider: tencent
```

## Mock模式

开发环境建议启用Mock模式：

```yaml
sms:
  mock-enabled: true
```

Mock模式特点：
- 不实际发送短信
- 生成模拟的第三方消息ID
- 模拟发送成功
- 记录日志

## 真实SDK集成

### 阿里云

1. 添加依赖：
```xml
<dependency>
    <groupId>com.aliyun</groupId>
    <artifactId>dysmsapi20170525</artifactId>
    <version>2.0.24</version>
</dependency>
```

2. 配置密钥：
```yaml
sms:
  mock-enabled: false
  aliyun:
    access-key-id: your-access-key-id
    access-key-secret: your-access-key-secret
```

3. 取消注释 `AliyunSmsSenderImpl.java` 中的真实SDK代码

### 腾讯云

1. 添加依赖：
```xml
<dependency>
    <groupId>com.tencentcloudapi</groupId>
    <artifactId>tencentcloud-sdk-java-sms</artifactId>
    <version>3.1.880</version>
</dependency>
```

2. 配置密钥：
```yaml
sms:
  mock-enabled: false
  tencent:
    secret-id: your-secret-id
    secret-key: your-secret-key
    sdk-app-id: your-sdk-app-id
```

3. 取消注释 `TencentSmsSenderImpl.java` 中的真实SDK代码

## 注意事项

1. **密钥安全**: 使用环境变量，不要直接写在配置文件中
2. **签名申请**: 需要在平台控制台申请短信签名
3. **模板申请**: 模板短信需要申请模板并审核通过
4. **费用控制**: 短信发送会产生费用，建议设置预警
5. **发送限制**: 注意平台的发送频率限制
6. **内容规范**: 短信内容需要符合平台规范

## 常见问题

### Q: 如何切换服务提供商？
A: 修改 `sms.provider` 配置为 `aliyun` 或 `tencent`

### Q: Mock模式是什么？
A: Mock模式用于开发测试，不实际发送短信，只记录日志

### Q: 如何查看发送记录？
A: 发送记录保存在 `sys_notification_log` 表中

### Q: 发送失败会重试吗？
A: 会自动重试，最多重试3次（可配置）

### Q: 如何批量发送？
A: 使用 `/notification/sms/send-batch` 接口，最多支持1000个手机号

### Q: 如何使用模板短信？
A: 使用 `/notification/sms/send-template` 接口，传入模板编码和参数

## 相关文档

- [配置文档](sms-config.md)
- [API文档](sms-api.md)
- [实现总结](task-21.3-summary.md)

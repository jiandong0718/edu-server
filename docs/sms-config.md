# 短信服务配置说明

## 配置文件位置
`edu-admin/src/main/resources/application-dev.yml`

## 配置示例

```yaml
# 短信配置
sms:
  # 短信服务提供商：aliyun-阿里云，tencent-腾讯云
  provider: aliyun
  # 是否启用Mock模式（用于测试，不实际发送短信）
  mock-enabled: true
  # 最大重试次数
  max-retry-count: 3
  # 阿里云短信配置
  aliyun:
    access-key-id: ${ALIYUN_SMS_ACCESS_KEY_ID:your-access-key-id}
    access-key-secret: ${ALIYUN_SMS_ACCESS_KEY_SECRET:your-access-key-secret}
    sign-name: 教育管理系统
    region-id: cn-hangzhou
    endpoint: dysmsapi.aliyuncs.com
  # 腾讯云短信配置
  tencent:
    secret-id: ${TENCENT_SMS_SECRET_ID:your-secret-id}
    secret-key: ${TENCENT_SMS_SECRET_KEY:your-secret-key}
    sdk-app-id: ${TENCENT_SMS_SDK_APP_ID:your-sdk-app-id}
    sign-name: 教育管理系统
    region: ap-guangzhou
```

## 环境变量配置

在 `.env` 文件中配置敏感信息：

```bash
# 阿里云短信配置
ALIYUN_SMS_ACCESS_KEY_ID=your-access-key-id
ALIYUN_SMS_ACCESS_KEY_SECRET=your-access-key-secret

# 腾讯云短信配置
TENCENT_SMS_SECRET_ID=your-secret-id
TENCENT_SMS_SECRET_KEY=your-secret-key
TENCENT_SMS_SDK_APP_ID=your-sdk-app-id
```

## 配置说明

### 基础配置

- `provider`: 短信服务提供商
  - `aliyun`: 阿里云短信服务
  - `tencent`: 腾讯云短信服务

- `mock-enabled`: 是否启用Mock模式
  - `true`: 启用Mock模式，不实际发送短信，用于开发测试
  - `false`: 关闭Mock模式，使用真实的短信服务

- `max-retry-count`: 发送失败时的最大重试次数，默认3次

### 阿里云配置

- `access-key-id`: 阿里云AccessKey ID
- `access-key-secret`: 阿里云AccessKey Secret
- `sign-name`: 短信签名，需要在阿里云控制台申请
- `region-id`: 区域ID，默认cn-hangzhou
- `endpoint`: API端点，默认dysmsapi.aliyuncs.com

### 腾讯云配置

- `secret-id`: 腾讯云Secret ID
- `secret-key`: 腾讯云Secret Key
- `sdk-app-id`: 短信应用ID，在腾讯云控制台获取
- `sign-name`: 短信签名，需要在腾讯云控制台申请
- `region`: 区域，默认ap-guangzhou

## 切换服务提供商

只需修改 `provider` 配置即可：

```yaml
# 使用阿里云
sms:
  provider: aliyun

# 使用腾讯云
sms:
  provider: tencent
```

## 开发环境配置

开发环境建议启用Mock模式：

```yaml
sms:
  mock-enabled: true
```

## 生产环境配置

生产环境需要关闭Mock模式，并配置真实的密钥：

```yaml
sms:
  provider: aliyun
  mock-enabled: false
  aliyun:
    access-key-id: ${ALIYUN_SMS_ACCESS_KEY_ID}
    access-key-secret: ${ALIYUN_SMS_ACCESS_KEY_SECRET}
```

## 注意事项

1. **密钥安全**: 不要将密钥直接写在配置文件中，使用环境变量
2. **签名申请**: 短信签名需要在对应平台控制台申请并审核通过
3. **模板申请**: 模板短信需要在对应平台控制台申请模板并审核通过
4. **费用控制**: 短信发送会产生费用，建议设置费用预警
5. **发送限制**: 注意平台的发送频率限制，避免被限流

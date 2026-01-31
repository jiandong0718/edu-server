# 合同PDF生成功能实现总结

## 任务概述
实现合同PDF生成功能，使用iText 7库直接生成标准格式的合同PDF文件，支持中文字体、表格布局、文件存储等功能。

## 实现内容

### 1. 服务接口与实现

#### 1.1 ContractPdfGeneratorService 接口
**文件路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/src/main/java/com/edu/finance/service/ContractPdfGeneratorService.java`

**核心方法**:
- `generateAndSavePdf(Long contractId)`: 生成PDF并保存到文件系统，返回文件URL
- `generatePdfToStream(Long contractId, OutputStream outputStream)`: 生成PDF到输出流
- `downloadPdf(Long contractId)`: 下载PDF，返回字节数组
- `canGeneratePdf(Long contractId)`: 检查合同是否可以生成PDF

#### 1.2 ContractPdfGeneratorServiceImpl 实现类
**文件路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/src/main/java/com/edu/finance/service/impl/ContractPdfGeneratorServiceImpl.java`

**技术特点**:
- 使用iText 7直接生成PDF（非HTML转换方式）
- 支持中文字体（使用iText内置的STSong-Light字体）
- 专业的PDF布局和样式设计
- 双重存储：本地文件系统 + 文件服务（OSS）

**PDF内容结构**:
1. **标题**: 教育培训服务合同（20pt，加粗，居中）
2. **合同基本信息**: 合同编号、签订日期
3. **甲方信息**: 培训机构名称、联系人、电话、地址
4. **乙方信息**: 学员姓名、监护人、电话、身份证号、地址
5. **课程明细表格**:
   - 表头：课程名称、课时数、单价、金额
   - 灰色背景表头，黑色边框
   - 数据居中对齐
6. **费用汇总**:
   - 合同总金额
   - 优惠金额
   - 实付金额（红色加粗显示）
   - 已付金额
   - 待付金额
   - 付款方式
7. **合同期限**: 生效日期、到期日期
8. **合同条款**: 详细的7条合同条款
9. **备注**: 可选的备注信息
10. **签字栏**: 甲方盖章、乙方签字、日期

**业务逻辑**:
- 只有状态为"signed"（已签署）或"completed"（已完成）的合同才能生成PDF
- 生成的PDF同时保存到本地文件系统和文件服务
- 本地路径：`/data/files/contract/pdf/{contractNo}_{timestamp}.pdf`
- 文件服务路径：`contract/pdf/{contractNo}_{timestamp}.pdf`

### 2. Controller API接口

**文件路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/src/main/java/com/edu/finance/controller/ContractController.java`

#### 2.1 生成合同PDF
```
POST /finance/contract/{id}/generate-pdf
```
- **功能**: 生成合同PDF并保存到文件系统
- **参数**: 合同ID（路径参数）
- **返回**: 文件URL（String）
- **业务规则**: 只有已签署的合同才能生成

#### 2.2 下载合同PDF
```
GET /finance/contract/{id}/download-pdf
```
- **功能**: 下载合同PDF文件
- **参数**: 合同ID（路径参数）
- **返回**: PDF文件字节流
- **响应头**:
  - Content-Type: application/pdf
  - Content-Disposition: attachment; filename="contract_{contractNo}.pdf"
- **特点**: 浏览器会提示下载文件

#### 2.3 预览合同PDF
```
GET /finance/contract/{id}/preview-pdf
```
- **功能**: 在线预览合同PDF
- **参数**: 合同ID（路径参数）
- **返回**: PDF文件字节流
- **响应头**:
  - Content-Type: application/pdf
  - Content-Disposition: inline; filename="contract.pdf"
- **特点**: 浏览器会尝试在线打开PDF

### 3. 依赖配置

**文件路径**: `/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/pom.xml`

**新增依赖**:
```xml
<!-- iText Asian font support for Chinese -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>font-asian</artifactId>
    <version>7.2.5</version>
</dependency>
```

**已有依赖**:
- itext7-core: 7.2.5
- html2pdf: 4.0.5

## 技术实现要点

### 1. 中文字体支持
- 使用iText内置的STSong-Light字体（宋体）
- 字体编码：UniGB-UCS2-H
- 支持中文显示和PDF嵌入

### 2. PDF布局设计
- **页面大小**: A4
- **页边距**: 上下左右各50pt
- **字体大小**:
  - 标题：20pt
  - 章节标题：14pt
  - 正文：12pt
  - 表格数据：10-11pt
- **颜色方案**:
  - 表头背景：RGB(240, 240, 240)
  - 实付金额：RGB(208, 0, 0) 红色
  - 边框：黑色

### 3. 表格设计
- 使用iText Table组件
- 4列布局：课程名称(3)、课时数(1.5)、单价(1.5)、金额(2)
- 表头灰色背景，加粗字体
- 数据单元格居中对齐
- 黑色实线边框

### 4. 文件存储策略
- **双重存储**:
  1. 本地文件系统：`/data/files/contract/pdf/`
  2. 文件服务（可能是OSS）：通过FileService接口
- **文件命名**: `contract_{contractNo}_{timestamp}.pdf`
- **自动创建目录**: 使用Hutool的FileUtil.mkdir()

### 5. 异常处理
- 合同不存在：抛出BusinessException
- 合同明细不存在：抛出BusinessException
- 学员信息不存在：抛出BusinessException
- 合同状态不符合：抛出BusinessException
- PDF生成失败：捕获异常并记录日志，抛出BusinessException

## 与现有ContractPdfService的区别

### ContractPdfService（已有）
- 使用HTML2PDF方式生成
- 先生成HTML，再转换为PDF
- 适合复杂的HTML布局
- 文件路径：`ContractPdfService.java`

### ContractPdfGeneratorService（新增）
- 使用iText 7直接生成PDF
- 更精确的布局控制
- 更好的性能
- 更专业的PDF格式
- 文件路径：`ContractPdfGeneratorService.java`

**两者可以共存**，提供不同的PDF生成方式供选择。

## 使用示例

### 1. 生成并保存PDF
```java
// 调用服务
String fileUrl = contractPdfGeneratorService.generateAndSavePdf(contractId);
// 返回: http://xxx.com/files/contract/pdf/contract_CT20260131001_1738310400000.pdf
```

### 2. 下载PDF
```bash
curl -X GET "http://localhost:8080/finance/contract/1/download-pdf" \
  -H "Authorization: Bearer {token}" \
  --output contract.pdf
```

### 3. 在线预览PDF
```bash
# 浏览器访问
http://localhost:8080/finance/contract/1/preview-pdf
```

## 配置说明

### application.yml配置
```yaml
file:
  upload:
    path: /data/files  # 文件上传根路径
```

### 目录结构
```
/data/files/
└── contract/
    └── pdf/
        ├── contract_CT20260131001_1738310400000.pdf
        ├── contract_CT20260131002_1738310500000.pdf
        └── ...
```

## 测试建议

### 1. 单元测试
- 测试合同状态校验
- 测试PDF生成逻辑
- 测试文件保存逻辑
- 测试异常处理

### 2. 集成测试
- 测试完整的PDF生成流程
- 测试文件下载功能
- 测试在线预览功能
- 测试中文字体显示

### 3. 性能测试
- 测试大量合同明细的PDF生成
- 测试并发生成PDF
- 测试文件存储性能

## 注意事项

1. **字体文件**: iText 7的font-asian依赖包含了中文字体，无需额外配置
2. **文件权限**: 确保应用有权限写入`/data/files/contract/pdf/`目录
3. **磁盘空间**: 注意监控PDF文件占用的磁盘空间
4. **并发控制**: 大量并发生成PDF时注意内存使用
5. **文件清理**: 建议定期清理旧的PDF文件
6. **安全性**: 下载和预览接口需要权限控制

## 扩展功能建议

1. **水印功能**: 添加"已作废"、"副本"等水印
2. **电子签名**: 集成电子签名功能
3. **PDF加密**: 添加PDF密码保护
4. **批量生成**: 支持批量生成多个合同PDF
5. **模板定制**: 支持自定义PDF模板
6. **版本管理**: 记录PDF生成历史和版本
7. **异步生成**: 对于大量合同，使用异步任务生成
8. **缓存机制**: 缓存已生成的PDF，避免重复生成

## 总结

本次实现完成了合同PDF生成的核心功能，使用iText 7直接生成专业格式的PDF文档，支持中文字体、表格布局、文件存储等功能。提供了三个API接口：生成PDF、下载PDF、预览PDF，满足了不同的业务场景需求。

实现的代码结构清晰，异常处理完善，日志记录详细，便于后续维护和扩展。与现有的ContractPdfService形成互补，为系统提供了更灵活的PDF生成方案。

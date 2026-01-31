# 收据打印功能快速参考

## API接口速查

### 1. 获取收据详情
```http
GET /finance/payment/receipt/{paymentId}
```

### 2. 生成收据PDF
```http
POST /finance/payment/receipt/generate?paymentId={paymentId}
```

### 3. 下载收据PDF
```http
GET /finance/payment/receipt/download/{paymentId}
```

### 4. 预览收据
```http
GET /finance/payment/receipt/preview/{paymentId}
```

### 5. 批量生成收据PDF
```http
POST /finance/payment/receipt/batch/generate
Content-Type: application/json

[1, 2, 3, 4, 5]
```

### 6. 批量下载收据PDF
```http
POST /finance/payment/receipt/batch/download
Content-Type: application/json

[1, 2, 3, 4, 5]
```

---

## 前端使用

### 导入API
```typescript
import {
  getReceiptDetail,
  generateReceiptPdf,
  downloadReceiptPdf,
  previewReceipt,
  generateBatchReceiptPdf,
  downloadBatchReceiptPdf,
} from '@/api/payment';
```

### 导入组件
```typescript
import ReceiptPrint from '@/components/ReceiptPrint';
```

### 使用示例
```tsx
const [receiptVisible, setReceiptVisible] = useState(false);
const [currentPaymentId, setCurrentPaymentId] = useState<number | null>(null);

// 打开打印预览
const handlePrint = (paymentId: number) => {
  setCurrentPaymentId(paymentId);
  setReceiptVisible(true);
};

// 渲染组件
<ReceiptPrint
  visible={receiptVisible}
  paymentId={currentPaymentId}
  onClose={() => {
    setReceiptVisible(false);
    setCurrentPaymentId(null);
  }}
/>
```

---

## 收据编号规则

**格式**：`SJ + yyyyMMdd + 6位序号`

**示例**：
- `SJ202601310000001` - 2026年1月31日第1号
- `SJ202601310000002` - 2026年1月31日第2号
- `SJ202602010000001` - 2026年2月1日第1号（新的一天重新开始）

---

## 金额大写转换

| 小写金额 | 大写金额 |
|---------|---------|
| 0.00 | 零元整 |
| 100.00 | 壹佰元整 |
| 1234.56 | 壹仟贰佰叁拾肆元伍角陆分 |
| 3000.00 | 叁仟元整 |
| 10000.00 | 壹万元整 |
| 50000.00 | 伍万元整 |

---

## 支付方式映射

| 代码 | 显示名称 |
|-----|---------|
| wechat | 微信支付 |
| alipay | 支付宝 |
| unionpay | 银联支付 |
| cash | 现金 |
| pos | POS机 |
| bank_transfer | 银行转账 |

---

## 打印设置

### 纸张大小
- **A4**：210mm × 297mm（标准）
- **A5**：148mm × 210mm（节省纸张）

### 打印方向
- **纵向**（Portrait）：默认
- **横向**（Landscape）：宽幅内容

### 打印份数
- 范围：1-10份
- 默认：1份

---

## 常见问题

### Q1: 收据编号重复怎么办？
A: 收据编号由系统自动生成，每天从1开始递增，不会重复。

### Q2: 如何修改机构信息？
A: 目前机构信息使用默认值，可以在 `ReceiptServiceImpl` 中修改：
```java
institutionInfo.setName("您的机构名称");
institutionInfo.setAddress("您的机构地址");
institutionInfo.setPhone("您的联系电话");
```

### Q3: 打印时中文显示乱码？
A: 确保已正确配置iText的中文字体支持：
```java
DefaultFontProvider fontProvider = new DefaultFontProvider(true, true, true);
converterProperties.setFontProvider(fontProvider);
```

### Q4: 如何批量打印？
A: 在收款流水页面勾选多条记录，点击"批量打印"按钮即可。

### Q5: 只有已支付的记录可以打印吗？
A: 是的，只有状态为"已支付"的收款记录才能打印收据。

---

## 文件位置

### 后端
- Controller: `edu-finance/src/main/java/com/edu/finance/controller/ReceiptController.java`
- Service: `edu-finance/src/main/java/com/edu/finance/service/ReceiptService.java`
- ServiceImpl: `edu-finance/src/main/java/com/edu/finance/service/impl/ReceiptServiceImpl.java`
- DTO: `edu-finance/src/main/java/com/edu/finance/domain/dto/ReceiptDTO.java`

### 前端
- API: `edu-web/src/api/payment.ts`
- Component: `edu-web/src/components/ReceiptPrint/index.tsx`
- Styles: `edu-web/src/components/ReceiptPrint/index.less`
- Page: `edu-web/src/pages/finance/payment/index.tsx`

---

## 测试命令

```bash
# 测试获取收据详情
curl http://localhost:8080/finance/payment/receipt/1

# 测试生成PDF
curl -X POST "http://localhost:8080/finance/payment/receipt/generate?paymentId=1"

# 测试下载PDF
curl http://localhost:8080/finance/payment/receipt/download/1 -o receipt.pdf

# 测试批量生成
curl -X POST http://localhost:8080/finance/payment/receipt/batch/generate \
  -H "Content-Type: application/json" \
  -d "[1,2,3]"
```

---

## 依赖版本

```xml
<!-- iText PDF -->
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>itext7-core</artifactId>
    <version>7.2.5</version>
</dependency>
<dependency>
    <groupId>com.itextpdf</groupId>
    <artifactId>html2pdf</artifactId>
    <version>4.0.5</version>
</dependency>
```

---

**最后更新**：2026-01-31

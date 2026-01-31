# 合同PDF生成API使用指南

## API接口列表

### 1. 生成合同PDF

**接口地址**: `POST /finance/contract/{id}/generate-pdf`

**功能说明**: 生成合同PDF并保存到文件系统，返回文件访问URL

**请求参数**:
- `id` (路径参数): 合同ID

**请求示例**:
```bash
curl -X POST "http://localhost:8080/finance/contract/1/generate-pdf" \
  -H "Authorization: Bearer {your_token}" \
  -H "Content-Type: application/json"
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": "http://xxx.com/files/contract/pdf/contract_CT20260131001_1738310400000.pdf"
}
```

**业务规则**:
- 只有状态为"signed"（已签署）或"completed"（已完成）的合同才能生成PDF
- 如果合同状态不符合，返回错误提示

**错误响应**:
```json
{
  "code": 500,
  "msg": "只有已签署的合同才能生成PDF",
  "data": null
}
```

---

### 2. 下载合同PDF

**接口地址**: `GET /finance/contract/{id}/download-pdf`

**功能说明**: 下载合同PDF文件到本地

**请求参数**:
- `id` (路径参数): 合同ID

**请求示例**:
```bash
curl -X GET "http://localhost:8080/finance/contract/1/download-pdf" \
  -H "Authorization: Bearer {your_token}" \
  --output contract.pdf
```

**响应说明**:
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="contract_CT20260131001.pdf"`
- 浏览器会提示下载文件

**前端使用示例** (JavaScript):
```javascript
// 使用fetch下载PDF
async function downloadContractPdf(contractId) {
  const response = await fetch(`/finance/contract/${contractId}/download-pdf`, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  });

  const blob = await response.blob();
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = `contract_${contractId}.pdf`;
  document.body.appendChild(a);
  a.click();
  window.URL.revokeObjectURL(url);
  document.body.removeChild(a);
}
```

**前端使用示例** (Axios):
```javascript
import axios from 'axios';

// 下载PDF
const downloadPdf = async (contractId) => {
  try {
    const response = await axios.get(
      `/finance/contract/${contractId}/download-pdf`,
      {
        responseType: 'blob',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `contract_${contractId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error('下载失败:', error);
  }
};
```

---

### 3. 预览合同PDF

**接口地址**: `GET /finance/contract/{id}/preview-pdf`

**功能说明**: 在浏览器中在线预览合同PDF

**请求参数**:
- `id` (路径参数): 合同ID

**请求示例**:
```bash
# 直接在浏览器中访问
http://localhost:8080/finance/contract/1/preview-pdf
```

**响应说明**:
- Content-Type: `application/pdf`
- Content-Disposition: `inline; filename="contract.pdf"`
- 浏览器会尝试在线打开PDF（如果浏览器支持）

**前端使用示例** (在新窗口打开):
```javascript
// 在新窗口中预览PDF
function previewContractPdf(contractId) {
  const url = `/finance/contract/${contractId}/preview-pdf`;
  window.open(url, '_blank');
}
```

**前端使用示例** (嵌入iframe):
```html
<!-- 在页面中嵌入PDF预览 -->
<iframe
  src="/finance/contract/1/preview-pdf"
  width="100%"
  height="800px"
  style="border: none;">
</iframe>
```

**前端使用示例** (React组件):
```jsx
import React from 'react';

const ContractPdfPreview = ({ contractId }) => {
  const pdfUrl = `/finance/contract/${contractId}/preview-pdf`;

  return (
    <div style={{ width: '100%', height: '800px' }}>
      <iframe
        src={pdfUrl}
        width="100%"
        height="100%"
        style={{ border: 'none' }}
        title="合同预览"
      />
    </div>
  );
};

export default ContractPdfPreview;
```

---

## 完整使用流程

### 场景1: 生成并下载合同PDF

```javascript
// 1. 先生成PDF
const generatePdf = async (contractId) => {
  const response = await axios.post(`/finance/contract/${contractId}/generate-pdf`);
  const fileUrl = response.data.data;
  console.log('PDF已生成:', fileUrl);
  return fileUrl;
};

// 2. 然后下载PDF
const downloadPdf = async (contractId) => {
  const response = await axios.get(
    `/finance/contract/${contractId}/download-pdf`,
    { responseType: 'blob' }
  );

  const url = window.URL.createObjectURL(new Blob([response.data]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', `contract_${contractId}.pdf`);
  document.body.appendChild(link);
  link.click();
  link.remove();
};

// 完整流程
const handleGenerateAndDownload = async (contractId) => {
  try {
    // 生成PDF
    await generatePdf(contractId);
    // 下载PDF
    await downloadPdf(contractId);
  } catch (error) {
    console.error('操作失败:', error);
  }
};
```

### 场景2: 直接预览合同PDF

```javascript
// 直接预览，无需先生成
const previewPdf = (contractId) => {
  const url = `/finance/contract/${contractId}/preview-pdf`;
  window.open(url, '_blank', 'width=1000,height=800');
};
```

### 场景3: 在Modal中预览PDF

```jsx
import React, { useState } from 'react';
import { Modal, Button } from 'antd';

const ContractPdfModal = ({ contractId }) => {
  const [visible, setVisible] = useState(false);

  return (
    <>
      <Button onClick={() => setVisible(true)}>
        预览合同
      </Button>

      <Modal
        title="合同预览"
        visible={visible}
        onCancel={() => setVisible(false)}
        width={1000}
        footer={null}
      >
        <iframe
          src={`/finance/contract/${contractId}/preview-pdf`}
          width="100%"
          height="700px"
          style={{ border: 'none' }}
        />
      </Modal>
    </>
  );
};
```

---

## 错误处理

### 常见错误码

| 错误码 | 错误信息 | 说明 |
|--------|---------|------|
| 500 | 只有已签署的合同才能生成PDF | 合同状态不符合要求 |
| 500 | 合同不存在 | 合同ID无效 |
| 500 | 合同明细不存在 | 合同没有课程明细 |
| 500 | 学员信息不存在 | 学员数据缺失 |
| 500 | 生成PDF失败: xxx | PDF生成过程出错 |

### 错误处理示例

```javascript
const handlePdfOperation = async (contractId) => {
  try {
    const response = await axios.post(
      `/finance/contract/${contractId}/generate-pdf`
    );

    if (response.data.code === 200) {
      message.success('PDF生成成功');
      return response.data.data;
    } else {
      message.error(response.data.msg);
    }
  } catch (error) {
    if (error.response) {
      // 服务器返回错误
      message.error(error.response.data.msg || '操作失败');
    } else if (error.request) {
      // 请求发送失败
      message.error('网络错误，请稍后重试');
    } else {
      // 其他错误
      message.error('操作失败');
    }
  }
};
```

---

## 权限说明

所有PDF相关接口都需要用户登录并具有相应权限：

- **生成PDF**: 需要合同管理权限
- **下载PDF**: 需要合同查看权限
- **预览PDF**: 需要合同查看权限

请确保在请求头中携带有效的JWT Token：
```
Authorization: Bearer {your_jwt_token}
```

---

## 性能优化建议

1. **缓存机制**: 对于已生成的PDF，可以缓存文件URL，避免重复生成
2. **异步生成**: 对于大量合同，建议使用异步任务队列
3. **CDN加速**: 将生成的PDF文件上传到CDN，加快访问速度
4. **懒加载**: 在列表页面不要自动生成PDF，只在用户点击时生成

---

## 注意事项

1. PDF生成是CPU密集型操作，避免短时间内大量并发请求
2. 生成的PDF文件会占用存储空间，建议定期清理旧文件
3. 预览功能依赖浏览器的PDF支持，部分浏览器可能需要安装插件
4. 下载功能在移动端可能表现不同，建议测试各平台兼容性
5. 文件名包含中文时，已使用URLEncoder进行编码处理

---

## 测试建议

### 功能测试
- [ ] 测试生成PDF功能
- [ ] 测试下载PDF功能
- [ ] 测试预览PDF功能
- [ ] 测试中文字体显示
- [ ] 测试表格布局
- [ ] 测试不同合同状态的处理

### 兼容性测试
- [ ] Chrome浏览器
- [ ] Firefox浏览器
- [ ] Safari浏览器
- [ ] Edge浏览器
- [ ] 移动端浏览器

### 性能测试
- [ ] 单个PDF生成时间
- [ ] 并发生成PDF
- [ ] 大量明细的合同PDF生成
- [ ] 文件下载速度

---

## 联系支持

如有问题，请联系技术支持团队。

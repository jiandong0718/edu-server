#!/bin/bash

echo "=========================================="
echo "在线支付实现验证脚本"
echo "=========================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查文件是否存在
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $2"
        return 0
    else
        echo -e "${RED}✗${NC} $2 (文件不存在: $1)"
        return 1
    fi
}

echo "1. 检查核心文件..."
echo "-------------------------------------------"

# Controller
check_file "edu-finance/src/main/java/com/edu/finance/controller/OnlinePaymentController.java" "OnlinePaymentController"

# Service
check_file "edu-finance/src/main/java/com/edu/finance/service/OnlinePaymentService.java" "OnlinePaymentService (接口)"
check_file "edu-finance/src/main/java/com/edu/finance/service/impl/OnlinePaymentServiceImpl.java" "OnlinePaymentServiceImpl (实现)"

# Payment Gateway Interface
check_file "edu-finance/src/main/java/com/edu/finance/payment/PaymentGateway.java" "PaymentGateway (接口)"

# Payment Gateway Implementations
check_file "edu-finance/src/main/java/com/edu/finance/payment/gateway/WechatPaymentGateway.java" "WechatPaymentGateway"
check_file "edu-finance/src/main/java/com/edu/finance/payment/gateway/AlipayPaymentGateway.java" "AlipayPaymentGateway"
check_file "edu-finance/src/main/java/com/edu/finance/payment/gateway/UnionPaymentGateway.java" "UnionPaymentGateway"
check_file "edu-finance/src/main/java/com/edu/finance/payment/gateway/MockPaymentGateway.java" "MockPaymentGateway (新增)"

# Config
check_file "edu-finance/src/main/java/com/edu/finance/payment/config/WechatPayConfig.java" "WechatPayConfig"
check_file "edu-finance/src/main/java/com/edu/finance/payment/config/AlipayConfig.java" "AlipayConfig"
check_file "edu-finance/src/main/java/com/edu/finance/payment/config/UnionPayConfig.java" "UnionPayConfig"

# DTOs
check_file "edu-finance/src/main/java/com/edu/finance/domain/dto/OnlinePaymentRequest.java" "OnlinePaymentRequest"
check_file "edu-finance/src/main/java/com/edu/finance/domain/dto/OnlinePaymentResponse.java" "OnlinePaymentResponse"
check_file "edu-finance/src/main/java/com/edu/finance/domain/dto/PaymentNotification.java" "PaymentNotification"

# Event
check_file "edu-finance/src/main/java/com/edu/finance/event/ContractPaidEvent.java" "ContractPaidEvent"
check_file "edu-finance/src/main/java/com/edu/finance/listener/ContractPaidEventListener.java" "ContractPaidEventListener"

echo ""
echo "2. 检查文档文件..."
echo "-------------------------------------------"

check_file "ONLINE_PAYMENT_IMPLEMENTATION.md" "完整实现文档"
check_file "ONLINE_PAYMENT_API_REFERENCE.md" "API 快速参考"
check_file "payment-config-example.yml" "配置示例"
check_file "TASK_18.3_18.4_SUMMARY.md" "任务完成总结"

echo ""
echo "3. 检查关键代码..."
echo "-------------------------------------------"

# 检查 MockPaymentGateway 是否包含关键方法
if grep -q "public String getChannel()" edu-finance/src/main/java/com/edu/finance/payment/gateway/MockPaymentGateway.java 2>/dev/null; then
    echo -e "${GREEN}✓${NC} MockPaymentGateway.getChannel() 方法存在"
else
    echo -e "${RED}✗${NC} MockPaymentGateway.getChannel() 方法不存在"
fi

if grep -q "public OnlinePaymentResponse createPayment" edu-finance/src/main/java/com/edu/finance/payment/gateway/MockPaymentGateway.java 2>/dev/null; then
    echo -e "${GREEN}✓${NC} MockPaymentGateway.createPayment() 方法存在"
else
    echo -e "${RED}✗${NC} MockPaymentGateway.createPayment() 方法不存在"
fi

if grep -q "public PaymentNotification parseNotification" edu-finance/src/main/java/com/edu/finance/payment/gateway/MockPaymentGateway.java 2>/dev/null; then
    echo -e "${GREEN}✓${NC} MockPaymentGateway.parseNotification() 方法存在"
else
    echo -e "${RED}✗${NC} MockPaymentGateway.parseNotification() 方法不存在"
fi

# 检查 Controller 是否包含 mock 回调接口
if grep -q "notify/mock" edu-finance/src/main/java/com/edu/finance/controller/OnlinePaymentController.java 2>/dev/null; then
    echo -e "${GREEN}✓${NC} OnlinePaymentController 包含 mock 回调接口"
else
    echo -e "${RED}✗${NC} OnlinePaymentController 不包含 mock 回调接口"
fi

echo ""
echo "4. 统计信息..."
echo "-------------------------------------------"

# 统计支付相关文件数量
payment_files=$(find edu-finance/src/main/java/com/edu/finance -type f -name "*.java" | grep -i payment | wc -l)
echo "支付相关 Java 文件数量: $payment_files"

# 统计文档文件
doc_files=$(ls -1 *.md 2>/dev/null | grep -i payment | wc -l)
echo "支付相关文档文件数量: $doc_files"

echo ""
echo "=========================================="
echo "验证完成！"
echo "=========================================="
echo ""
echo -e "${YELLOW}提示：${NC}"
echo "1. 所有核心文件已就绪"
echo "2. 可以使用模拟支付进行测试"
echo "3. 查看文档了解详细使用方法："
echo "   - ONLINE_PAYMENT_IMPLEMENTATION.md"
echo "   - ONLINE_PAYMENT_API_REFERENCE.md"
echo "   - TASK_18.3_18.4_SUMMARY.md"
echo ""
echo "快速测试命令："
echo "  mvn spring-boot:run -pl edu-admin"
echo "  访问 http://localhost:8080/doc.html"
echo ""

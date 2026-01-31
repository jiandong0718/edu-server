#!/bin/bash

# 课程包管理接口测试脚本
# 使用方法: ./test_course_package_api.sh

BASE_URL="http://localhost:8080"
TOKEN="your_jwt_token_here"

echo "========================================="
echo "课程包管理接口测试"
echo "========================================="

# 1. 分页查询课程包列表
echo -e "\n1. 测试分页查询课程包列表"
curl -X GET "${BASE_URL}/teaching/course-package/page?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# 2. 获取在售课程包列表
echo -e "\n\n2. 测试获取在售课程包列表"
curl -X GET "${BASE_URL}/teaching/course-package/list" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# 3. 创建课程包
echo -e "\n\n3. 测试创建课程包"
curl -X POST "${BASE_URL}/teaching/course-package" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "测试课程包",
    "description": "这是一个测试课程包",
    "price": 2980.00,
    "originalPrice": 3500.00,
    "validDays": 365,
    "status": 0,
    "sortOrder": 1,
    "items": [
      {
        "courseId": 1,
        "courseCount": 24,
        "sortOrder": 1
      },
      {
        "courseId": 2,
        "courseCount": 24,
        "sortOrder": 2
      }
    ]
  }'

# 4. 获取课程包详情（假设ID为1）
echo -e "\n\n4. 测试获取课程包详情"
curl -X GET "${BASE_URL}/teaching/course-package/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# 5. 更新课程包（假设ID为1）
echo -e "\n\n5. 测试更新课程包"
curl -X PUT "${BASE_URL}/teaching/course-package/1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "更新后的课程包",
    "description": "这是更新后的描述",
    "price": 2680.00,
    "originalPrice": 3500.00,
    "validDays": 365,
    "status": 0,
    "sortOrder": 1,
    "items": [
      {
        "courseId": 1,
        "courseCount": 30,
        "sortOrder": 1
      }
    ]
  }'

# 6. 上架课程包（假设ID为1）
echo -e "\n\n6. 测试上架课程包"
curl -X PUT "${BASE_URL}/teaching/course-package/1/publish" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# 7. 下架课程包（假设ID为1）
echo -e "\n\n7. 测试下架课程包"
curl -X PUT "${BASE_URL}/teaching/course-package/1/unpublish" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

# 8. 删除课程包（假设ID为999，不存在的ID）
echo -e "\n\n8. 测试删除课程包"
curl -X DELETE "${BASE_URL}/teaching/course-package/999" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json"

echo -e "\n\n========================================="
echo "测试完成"
echo "========================================="

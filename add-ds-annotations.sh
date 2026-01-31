#!/bin/bash

# 为各模块的 Mapper 添加 @DS 注解的脚本

# 定义颜色
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}开始为各模块 Mapper 添加 @DS 注解...${NC}"

# 函数：为指定目录下的所有 Mapper 添加 @DS 注解
add_ds_annotation() {
    local module_path=$1
    local ds_name=$2
    local module_name=$3

    echo -e "${GREEN}处理 ${module_name} 模块...${NC}"

    # 查找所有 Mapper.java 文件
    find "$module_path" -name "*Mapper.java" -type f | while read -r file; do
        # 检查文件是否已经有 @DS 注解
        if grep -q "@DS" "$file"; then
            echo "  跳过（已有@DS注解）: $(basename $file)"
        else
            # 检查是否有 package 声明
            if grep -q "^package " "$file"; then
                # 在 package 声明后添加 import
                if ! grep -q "import com.baomidou.dynamic.datasource.annotation.DS" "$file"; then
                    sed -i '' '/^package /a\
\
import com.baomidou.dynamic.datasource.annotation.DS;
' "$file"
                fi

                # 在 @Mapper 注解前添加 @DS 注解
                if grep -q "@Mapper" "$file"; then
                    sed -i '' "/@Mapper/i\\
@DS(\"$ds_name\")\\
" "$file"
                    echo "  ✓ 已添加: $(basename $file)"
                else
                    # 如果没有 @Mapper，在 public interface 前添加
                    sed -i '' "/^public interface/i\\
@DS(\"$ds_name\")\\
" "$file"
                    echo "  ✓ 已添加（无@Mapper）: $(basename $file)"
                fi
            fi
        fi
    done
}

# 系统模块
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java" "system" "edu-system"

# 学员模块
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java" "student" "edu-student"

# 教学模块
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java" "teaching" "edu-teaching"

# 财务模块
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/src/main/java" "finance" "edu-finance"

# 营销模块（包含通知）
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-marketing/src/main/java" "marketing" "edu-marketing"

# 通知模块
add_ds_annotation "/Users/liujiandong/Documents/work/package/edu/edu-server/edu-notification/src/main/java" "marketing" "edu-notification"

echo -e "${BLUE}完成！所有 Mapper 已添加 @DS 注解。${NC}"

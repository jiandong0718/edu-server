#!/usr/bin/env python3
"""
为各模块的 Mapper 接口批量添加 @DS 注解
"""

import os
import re

# 模块配置：(模块路径, 数据源名称)
MODULES = [
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-system/src/main/java/com/edu/system/mapper", "system"),
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-student/src/main/java/com/edu/student/mapper", "student"),
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-teaching/src/main/java/com/edu/teaching/mapper", "teaching"),
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-finance/src/main/java/com/edu/finance/mapper", "finance"),
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-marketing/src/main/java/com/edu/marketing/mapper", "marketing"),
    ("/Users/liujiandong/Documents/work/package/edu/edu-server/edu-notification/src/main/java/com/edu/notification/mapper", "marketing"),
]

def add_ds_annotation(file_path, ds_name):
    """为单个 Mapper 文件添加 @DS 注解"""
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # 检查是否已有 @DS 注解
    if '@DS' in content:
        return False, "已有@DS注解"

    # 添加 import
    if 'import com.baomidou.dynamic.datasource.annotation.DS;' not in content:
        # 在 package 声明后添加 import
        content = re.sub(
            r'(package .*?;)',
            r'\1\n\nimport com.baomidou.dynamic.datasource.annotation.DS;',
            content,
            count=1
        )

    # 在 public interface 前添加 @DS 注解
    content = re.sub(
        r'(\*/\n)(public interface)',
        rf'\1@DS("{ds_name}")\n\2',
        content,
        count=1
    )

    # 写回文件
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

    return True, "添加成功"

def process_module(mapper_dir, ds_name):
    """处理单个模块的所有 Mapper"""
    if not os.path.exists(mapper_dir):
        print(f"⚠️  目录不存在: {mapper_dir}")
        return

    print(f"\n📁 处理模块: {os.path.basename(os.path.dirname(mapper_dir))} (数据源: {ds_name})")

    count = 0
    for filename in os.listdir(mapper_dir):
        if filename.endswith('Mapper.java'):
            file_path = os.path.join(mapper_dir, filename)
            success, message = add_ds_annotation(file_path, ds_name)
            if success:
                print(f"  ✓ {filename}: {message}")
                count += 1
            else:
                print(f"  - {filename}: {message}")

    print(f"  共处理 {count} 个文件")

def main():
    print("=" * 60)
    print("开始为各模块 Mapper 添加 @DS 注解")
    print("=" * 60)

    total_count = 0
    for mapper_dir, ds_name in MODULES:
        process_module(mapper_dir, ds_name)

    print("\n" + "=" * 60)
    print("✅ 完成！所有 Mapper 已添加 @DS 注解")
    print("=" * 60)

if __name__ == '__main__':
    main()

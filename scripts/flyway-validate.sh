#!/bin/bash

###############################################################################
# Flyway Validate Script - 验证数据库迁移脚本
#
# 功能：
#   - 验证迁移脚本的完整性
#   - 检查脚本命名规范
#   - 验证已执行迁移的校验和
#   - 检测脚本冲突和问题
#
# 使用方法：
#   ./scripts/flyway-validate.sh [options]
#
# 选项：
#   -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
#   -h, --help              显示帮助信息
#
# 示例：
#   ./scripts/flyway-validate.sh                # 使用默认 dev 环境
#   ./scripts/flyway-validate.sh -p prod       # 使用 prod 环境
###############################################################################

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 默认配置
PROFILE="dev"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(dirname "$SCRIPT_DIR")"

# 打印带颜色的消息
print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 显示帮助信息
show_help() {
    cat << EOF
Flyway Validate Script - 验证数据库迁移脚本

使用方法:
    $0 [options]

选项:
    -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
    -h, --help              显示帮助信息

示例:
    $0                      # 使用默认 dev 环境
    $0 -p prod             # 使用 prod 环境

说明:
    此脚本会验证 Flyway 迁移脚本的完整性和一致性，包括：
    - 检查脚本命名规范
    - 验证已执行迁移的校验和
    - 检测版本号冲突
    - 检查脚本文件完整性

验证项目:
    1. 脚本命名规范检查
       - 版本化脚本: V{version}__{description}.sql
       - 可重复脚本: R__{description}.sql
       - 撤销脚本: U{version}__{description}.sql

    2. 版本号检查
       - 版本号格式: major.minor.patch
       - 版本号唯一性

    3. 校验和验证
       - 已执行脚本的校验和是否匹配
       - 检测脚本是否被修改

    4. 文件完整性
       - 检查脚本文件是否存在
       - 检查文件编码是否正确

EOF
}

# 解析命令行参数
parse_args() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            -p|--profile)
                PROFILE="$2"
                shift 2
                ;;
            -h|--help)
                show_help
                exit 0
                ;;
            *)
                print_error "未知选项: $1"
                show_help
                exit 1
                ;;
        esac
    done
}

# 检查 Maven 是否安装
check_maven() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven 未安装或不在 PATH 中"
        print_info "请安装 Maven: https://maven.apache.org/install.html"
        exit 1
    fi
}

# 检查项目目录
check_project() {
    if [ ! -f "$PROJECT_DIR/pom.xml" ]; then
        print_error "未找到项目 pom.xml 文件"
        print_error "当前目录: $PROJECT_DIR"
        exit 1
    fi

    if [ ! -d "$PROJECT_DIR/edu-admin" ]; then
        print_error "未找到 edu-admin 模块"
        exit 1
    fi
}

# 验证脚本命名规范
validate_naming() {
    print_info "=========================================="
    print_info "1. 验证脚本命名规范"
    print_info "=========================================="

    MIGRATION_DIR="$PROJECT_DIR/edu-admin/src/main/resources/db/migration"

    if [ ! -d "$MIGRATION_DIR" ]; then
        print_error "未找到迁移脚本目录: $MIGRATION_DIR"
        return 1
    fi

    local has_error=0

    # 检查版本化脚本命名
    print_info "检查版本化脚本 (V*.sql)..."
    for file in "$MIGRATION_DIR"/V*.sql; do
        if [ -f "$file" ]; then
            filename=$(basename "$file")
            # 验证格式: V{version}__{description}.sql
            if ! echo "$filename" | grep -qE '^V[0-9]+\.[0-9]+\.[0-9]+__[a-z0-9_]+\.sql$'; then
                print_warning "命名不规范: $filename"
                print_info "  期望格式: V{major}.{minor}.{patch}__{description}.sql"
                print_info "  示例: V1.0.0__init.sql"
                has_error=1
            fi
        fi
    done

    # 检查可重复脚本命名
    if ls "$MIGRATION_DIR"/R__*.sql 2>/dev/null | grep -q .; then
        print_info "检查可重复脚本 (R__*.sql)..."
        for file in "$MIGRATION_DIR"/R__*.sql; do
            if [ -f "$file" ]; then
                filename=$(basename "$file")
                if ! echo "$filename" | grep -qE '^R__[a-z0-9_]+\.sql$'; then
                    print_warning "命名不规范: $filename"
                    print_info "  期望格式: R__{description}.sql"
                    has_error=1
                fi
            fi
        done
    fi

    # 检查撤销脚本命名
    if ls "$MIGRATION_DIR"/U*.sql 2>/dev/null | grep -q .; then
        print_info "检查撤销脚本 (U*.sql)..."
        for file in "$MIGRATION_DIR"/U*.sql; do
            if [ -f "$file" ]; then
                filename=$(basename "$file")
                if ! echo "$filename" | grep -qE '^U[0-9]+\.[0-9]+\.[0-9]+__[a-z0-9_]+\.sql$'; then
                    print_warning "命名不规范: $filename"
                    print_info "  期望格式: U{major}.{minor}.{patch}__{description}.sql"
                    has_error=1
                fi
            fi
        done
    fi

    if [ $has_error -eq 0 ]; then
        print_success "脚本命名规范检查通过"
    else
        print_warning "发现命名不规范的脚本，建议修正"
    fi

    echo ""
}

# 检查版本号冲突
check_version_conflicts() {
    print_info "=========================================="
    print_info "2. 检查版本号冲突"
    print_info "=========================================="

    MIGRATION_DIR="$PROJECT_DIR/edu-admin/src/main/resources/db/migration"

    # 提取所有版本号
    local versions=()
    for file in "$MIGRATION_DIR"/V*.sql; do
        if [ -f "$file" ]; then
            filename=$(basename "$file")
            version=$(echo "$filename" | sed -E 's/^V([0-9]+\.[0-9]+\.[0-9]+)__.*/\1/')
            versions+=("$version")
        fi
    done

    # 检查重复版本号
    local duplicates=$(printf '%s\n' "${versions[@]}" | sort | uniq -d)

    if [ -n "$duplicates" ]; then
        print_error "发现重复的版本号:"
        echo "$duplicates" | while read -r version; do
            print_error "  版本 $version 存在多个脚本:"
            ls "$MIGRATION_DIR"/V${version}__*.sql | while read -r file; do
                echo "    - $(basename "$file")"
            done
        done
        echo ""
        print_info "解决方案："
        echo "  1. 重命名其中一个脚本文件"
        echo "  2. 或在开发环境启用 out-of-order: true"
        return 1
    else
        print_success "未发现版本号冲突"
    fi

    echo ""
}

# 检查文件编码
check_encoding() {
    print_info "=========================================="
    print_info "3. 检查文件编码"
    print_info "=========================================="

    MIGRATION_DIR="$PROJECT_DIR/edu-admin/src/main/resources/db/migration"

    local has_error=0

    for file in "$MIGRATION_DIR"/*.sql; do
        if [ -f "$file" ]; then
            # 检查是否包含 BOM
            if file "$file" | grep -q "with BOM"; then
                print_warning "文件包含 BOM: $(basename "$file")"
                print_info "  建议移除 BOM 标记"
                has_error=1
            fi

            # 检查换行符
            if file "$file" | grep -q "CRLF"; then
                print_warning "文件使用 CRLF 换行符: $(basename "$file")"
                print_info "  建议转换为 LF 换行符"
                has_error=1
            fi
        fi
    done

    if [ $has_error -eq 0 ]; then
        print_success "文件编码检查通过"
    else
        print_warning "发现编码问题，可能导致校验和不匹配"
    fi

    echo ""
}

# 执行 Flyway validate
run_flyway_validate() {
    print_info "=========================================="
    print_info "4. 执行 Flyway 验证"
    print_info "=========================================="
    print_info "环境: $PROFILE"
    print_info "项目目录: $PROJECT_DIR"
    print_info "=========================================="
    echo ""

    cd "$PROJECT_DIR"

    # 执行 Maven Flyway validate 命令
    print_info "正在执行 Flyway 验证..."
    echo ""

    if mvn flyway:validate -pl edu-admin -Dspring.profiles.active="$PROFILE"; then
        echo ""
        print_success "Flyway 验证通过"
        print_info "所有迁移脚本的完整性和一致性验证成功"
    else
        echo ""
        print_error "Flyway 验证失败"
        print_info "可能的原因："
        echo "  1. 迁移脚本被修改（校验和不匹配）"
        echo "  2. 缺少已执行的迁移脚本文件"
        echo "  3. 数据库连接失败"
        echo "  4. Flyway 元数据表损坏"
        echo ""
        print_info "解决方案："
        echo "  1. 如果脚本确实被修改，使用 flyway-repair.sh 修复"
        echo "  2. 如果缺少脚本文件，从版本控制恢复"
        echo "  3. 检查数据库连接配置"
        echo "  4. 查看详细错误信息进行排查"
        return 1
    fi
}

# 显示验证总结
show_summary() {
    echo ""
    print_info "=========================================="
    print_info "验证总结"
    print_info "=========================================="
    echo ""
    print_success "验证完成！"
    echo ""
    print_info "如果发现问题，请参考以下文档："
    echo "  - FLYWAY_GUIDE.md - Flyway 使用指南"
    echo "  - 常见问题章节"
    echo ""
    print_info "相关命令："
    echo "  - 查看迁移信息: ./scripts/flyway-info.sh"
    echo "  - 修复元数据: ./scripts/flyway-repair.sh"
    echo ""
}

# 主函数
main() {
    parse_args "$@"
    check_maven
    check_project

    # 执行各项验证
    validate_naming
    check_version_conflicts
    check_encoding
    run_flyway_validate

    # 显示总结
    show_summary
}

# 执行主函数
main "$@"

#!/bin/bash

###############################################################################
# Flyway Info Script - 查看数据库迁移版本信息
#
# 功能：
#   - 显示所有迁移脚本的状态
#   - 显示已执行和待执行的迁移
#   - 显示迁移的版本号、描述、类型和执行时间
#
# 使用方法：
#   ./scripts/flyway-info.sh [options]
#
# 选项：
#   -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
#   -h, --help              显示帮助信息
#
# 示例：
#   ./scripts/flyway-info.sh                    # 使用默认 dev 环境
#   ./scripts/flyway-info.sh -p prod           # 使用 prod 环境
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
Flyway Info Script - 查看数据库迁移版本信息

使用方法:
    $0 [options]

选项:
    -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
    -h, --help              显示帮助信息

示例:
    $0                      # 使用默认 dev 环境
    $0 -p prod             # 使用 prod 环境

说明:
    此脚本会显示所有 Flyway 迁移脚本的状态信息，包括：
    - Category: 迁移类型（Versioned/Repeatable/Undo）
    - Version: 版本号
    - Description: 描述信息
    - Type: 脚本类型（SQL/Java）
    - Installed On: 执行时间
    - State: 状态（Success/Pending/Failed）

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

# 执行 Flyway info
run_flyway_info() {
    print_info "=========================================="
    print_info "Flyway 迁移版本信息"
    print_info "=========================================="
    print_info "环境: $PROFILE"
    print_info "项目目录: $PROJECT_DIR"
    print_info "=========================================="
    echo ""

    cd "$PROJECT_DIR"

    # 执行 Maven Flyway info 命令
    print_info "正在查询 Flyway 迁移信息..."
    echo ""

    if mvn flyway:info -pl edu-admin -Dspring.profiles.active="$PROFILE" -q; then
        echo ""
        print_success "Flyway 信息查询完成"

        # 显示迁移脚本文件列表
        echo ""
        print_info "=========================================="
        print_info "迁移脚本文件列表"
        print_info "=========================================="

        MIGRATION_DIR="$PROJECT_DIR/edu-admin/src/main/resources/db/migration"
        if [ -d "$MIGRATION_DIR" ]; then
            echo ""
            print_info "脚本位置: $MIGRATION_DIR"
            echo ""

            # 列出所有版本化迁移脚本
            print_info "版本化迁移脚本 (V*.sql):"
            ls -lh "$MIGRATION_DIR"/V*.sql 2>/dev/null | awk '{print "  " $9 " (" $5 ")"}'

            # 列出可重复执行脚本
            if ls "$MIGRATION_DIR"/R__*.sql 2>/dev/null | grep -q .; then
                echo ""
                print_info "可重复执行脚本 (R__*.sql):"
                ls -lh "$MIGRATION_DIR"/R__*.sql 2>/dev/null | awk '{print "  " $9 " (" $5 ")"}'
            fi

            # 列出撤销脚本
            if ls "$MIGRATION_DIR"/U*.sql 2>/dev/null | grep -q .; then
                echo ""
                print_info "撤销脚本 (U*.sql):"
                ls -lh "$MIGRATION_DIR"/U*.sql 2>/dev/null | awk '{print "  " $9 " (" $5 ")"}'
            fi

            # 统计信息
            echo ""
            print_info "=========================================="
            print_info "统计信息"
            print_info "=========================================="
            VERSION_COUNT=$(ls "$MIGRATION_DIR"/V*.sql 2>/dev/null | wc -l | tr -d ' ')
            REPEATABLE_COUNT=$(ls "$MIGRATION_DIR"/R__*.sql 2>/dev/null | wc -l | tr -d ' ')
            UNDO_COUNT=$(ls "$MIGRATION_DIR"/U*.sql 2>/dev/null | wc -l | tr -d ' ')

            echo "  版本化脚本: $VERSION_COUNT 个"
            echo "  可重复脚本: $REPEATABLE_COUNT 个"
            echo "  撤销脚本: $UNDO_COUNT 个"
        else
            print_warning "未找到迁移脚本目录: $MIGRATION_DIR"
        fi

        echo ""
        print_info "=========================================="
        print_info "提示"
        print_info "=========================================="
        echo "  - Pending: 待执行的迁移"
        echo "  - Success: 已成功执行的迁移"
        echo "  - Failed: 执行失败的迁移"
        echo "  - Missing: 已执行但脚本文件缺失"
        echo ""
        print_info "更多信息请参考: FLYWAY_GUIDE.md"

    else
        echo ""
        print_error "Flyway 信息查询失败"
        print_info "请检查："
        echo "  1. 数据库连接配置是否正确"
        echo "  2. Flyway 配置是否正确"
        echo "  3. 数据库服务是否正常运行"
        exit 1
    fi
}

# 主函数
main() {
    parse_args "$@"
    check_maven
    check_project
    run_flyway_info
}

# 执行主函数
main "$@"

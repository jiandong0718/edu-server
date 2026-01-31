#!/bin/bash

###############################################################################
# Flyway Repair Script - 修复 Flyway 元数据
#
# 功能：
#   - 修复 Flyway 元数据表
#   - 更新迁移脚本的校验和
#   - 删除失败的迁移记录
#   - 修复校验和不匹配的问题
#
# 使用方法：
#   ./scripts/flyway-repair.sh [options]
#
# 选项：
#   -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
#   -f, --force             强制执行（跳过确认）
#   -h, --help              显示帮助信息
#
# 示例：
#   ./scripts/flyway-repair.sh                  # 使用默认 dev 环境
#   ./scripts/flyway-repair.sh -p prod         # 使用 prod 环境
#   ./scripts/flyway-repair.sh -f              # 强制执行
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
FORCE=false
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
Flyway Repair Script - 修复 Flyway 元数据

使用方法:
    $0 [options]

选项:
    -p, --profile <profile>  指定 Spring Profile (dev/prod)，默认：dev
    -f, --force             强制执行（跳过确认）
    -h, --help              显示帮助信息

示例:
    $0                      # 使用默认 dev 环境
    $0 -p prod             # 使用 prod 环境
    $0 -f                  # 强制执行

说明:
    此脚本会修复 Flyway 元数据表（flyway_schema_history），主要用于：

    1. 修复校验和不匹配
       - 当迁移脚本被修改后，校验和会不匹配
       - repair 会重新计算并更新校验和

    2. 删除失败的迁移记录
       - 删除 success = 0 的记录
       - 允许重新执行失败的迁移

    3. 修复元数据表
       - 修复损坏的元数据记录
       - 对齐脚本文件和数据库记录

使用场景:
    - 迁移脚本被修改（如修复 SQL 错误）
    - 迁移执行失败需要重试
    - 校验和不匹配错误
    - 手动修改了数据库但未更新元数据

注意事项:
    - 生产环境使用需谨慎，建议先备份数据库
    - repair 不会执行任何迁移，只修复元数据
    - 修复后需要重新验证和执行迁移

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
            -f|--force)
                FORCE=true
                shift
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

# 显示当前状态
show_current_status() {
    print_info "=========================================="
    print_info "当前 Flyway 状态"
    print_info "=========================================="
    echo ""

    cd "$PROJECT_DIR"

    # 尝试获取当前状态
    print_info "正在查询当前迁移状态..."
    echo ""

    if mvn flyway:info -pl edu-admin -Dspring.profiles.active="$PROFILE" -q 2>/dev/null; then
        echo ""
        print_success "状态查询成功"
    else
        echo ""
        print_warning "状态查询失败（可能存在元数据问题）"
    fi

    echo ""
}

# 确认操作
confirm_repair() {
    if [ "$FORCE" = true ]; then
        return 0
    fi

    print_warning "=========================================="
    print_warning "警告：即将执行 Flyway Repair"
    print_warning "=========================================="
    echo ""
    print_warning "此操作将："
    echo "  1. 重新计算所有迁移脚本的校验和"
    echo "  2. 删除失败的迁移记录"
    echo "  3. 修复元数据表中的问题"
    echo ""
    print_warning "环境: $PROFILE"
    echo ""

    if [ "$PROFILE" = "prod" ]; then
        print_error "=========================================="
        print_error "生产环境操作警告"
        print_error "=========================================="
        echo ""
        print_error "您正在对生产环境执行 repair 操作！"
        print_warning "强烈建议："
        echo "  1. 先备份数据库"
        echo "  2. 在测试环境验证"
        echo "  3. 确认操作的必要性"
        echo ""
    fi

    read -p "确认继续？(yes/no): " -r
    echo ""

    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        print_info "操作已取消"
        exit 0
    fi
}

# 执行 Flyway repair
run_flyway_repair() {
    print_info "=========================================="
    print_info "执行 Flyway Repair"
    print_info "=========================================="
    print_info "环境: $PROFILE"
    print_info "项目目录: $PROJECT_DIR"
    print_info "=========================================="
    echo ""

    cd "$PROJECT_DIR"

    # 执行 Maven Flyway repair 命令
    print_info "正在执行 Flyway repair..."
    echo ""

    if mvn flyway:repair -pl edu-admin -Dspring.profiles.active="$PROFILE"; then
        echo ""
        print_success "Flyway repair 执行成功"
        print_info "元数据已修复"
    else
        echo ""
        print_error "Flyway repair 执行失败"
        print_info "请检查："
        echo "  1. 数据库连接配置是否正确"
        echo "  2. Flyway 配置是否正确"
        echo "  3. 数据库服务是否正常运行"
        echo "  4. 是否有足够的数据库权限"
        exit 1
    fi
}

# 验证修复结果
verify_repair() {
    echo ""
    print_info "=========================================="
    print_info "验证修复结果"
    print_info "=========================================="
    echo ""

    cd "$PROJECT_DIR"

    # 执行验证
    print_info "正在验证迁移脚本..."
    echo ""

    if mvn flyway:validate -pl edu-admin -Dspring.profiles.active="$PROFILE" -q; then
        echo ""
        print_success "验证通过！"
        print_info "所有迁移脚本的校验和已更新"
    else
        echo ""
        print_warning "验证未通过"
        print_info "可能需要："
        echo "  1. 检查迁移脚本是否存在问题"
        echo "  2. 查看详细错误信息"
        echo "  3. 手动检查元数据表"
    fi

    # 显示当前状态
    echo ""
    print_info "当前迁移状态："
    echo ""
    mvn flyway:info -pl edu-admin -Dspring.profiles.active="$PROFILE" -q
}

# 显示后续步骤
show_next_steps() {
    echo ""
    print_info "=========================================="
    print_info "后续步骤"
    print_info "=========================================="
    echo ""
    print_info "1. 验证迁移状态"
    echo "   ./scripts/flyway-info.sh -p $PROFILE"
    echo ""
    print_info "2. 执行待处理的迁移"
    echo "   mvn spring-boot:run -pl edu-admin -Dspring.profiles.active=$PROFILE"
    echo ""
    print_info "3. 再次验证"
    echo "   ./scripts/flyway-validate.sh -p $PROFILE"
    echo ""
    print_info "更多信息请参考: FLYWAY_GUIDE.md"
    echo ""
}

# 备份建议
suggest_backup() {
    if [ "$PROFILE" = "prod" ]; then
        echo ""
        print_warning "=========================================="
        print_warning "生产环境备份建议"
        print_warning "=========================================="
        echo ""
        print_warning "在执行任何数据库操作前，建议先备份："
        echo ""
        echo "  # 备份整个数据库"
        echo "  mysqldump -u root -p edu_admin > backup_\$(date +%Y%m%d_%H%M%S).sql"
        echo ""
        echo "  # 仅备份 Flyway 元数据表"
        echo "  mysqldump -u root -p edu_admin flyway_schema_history > flyway_backup_\$(date +%Y%m%d_%H%M%S).sql"
        echo ""
        read -p "是否已完成备份？(yes/no): " -r
        echo ""

        if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
            print_warning "请先完成备份后再执行 repair 操作"
            exit 0
        fi
    fi
}

# 主函数
main() {
    parse_args "$@"
    check_maven
    check_project

    # 显示当前状态
    show_current_status

    # 生产环境备份建议
    suggest_backup

    # 确认操作
    confirm_repair

    # 执行 repair
    run_flyway_repair

    # 验证结果
    verify_repair

    # 显示后续步骤
    show_next_steps

    print_success "Flyway repair 完成！"
}

# 执行主函数
main "$@"

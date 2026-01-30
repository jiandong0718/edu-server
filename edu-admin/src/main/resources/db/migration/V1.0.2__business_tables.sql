-- 教育机构学生管理系统 - 业务模块表结构
-- V1.0.2__business_tables.sql

-- =============================================
-- 学员模块表 (stu_)
-- =============================================

-- 学员表
CREATE TABLE IF NOT EXISTS stu_student (
    id BIGINT NOT NULL COMMENT '学员ID',
    student_no VARCHAR(50) NOT NULL COMMENT '学员编号',
    name VARCHAR(50) NOT NULL COMMENT '学员姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    birthday DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像',
    school VARCHAR(100) COMMENT '学校',
    grade VARCHAR(50) COMMENT '年级',
    status VARCHAR(20) DEFAULT 'potential' COMMENT '状态：potential-潜在，trial-试听，enrolled-在读，suspended-休学，graduated-结业，refunded-退费',
    source VARCHAR(20) COMMENT '来源：offline-地推，referral-转介绍，online_ad-线上广告，walk_in-自然到访，phone-电话咨询',
    campus_id BIGINT COMMENT '所属校区ID',
    advisor_id BIGINT COMMENT '跟进顾问ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status),
    KEY idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员表';

-- 学员联系人表
CREATE TABLE IF NOT EXISTS stu_contact (
    id BIGINT NOT NULL COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    name VARCHAR(50) NOT NULL COMMENT '联系人姓名',
    relation VARCHAR(20) COMMENT '关系：father-父亲，mother-母亲，grandpa-爷爷，grandma-奶奶，other-其他',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    is_primary TINYINT DEFAULT 0 COMMENT '是否主要联系人',
    receive_notify TINYINT DEFAULT 1 COMMENT '是否接收通知',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员联系人表';

-- 学员标签表
CREATE TABLE IF NOT EXISTS stu_tag (
    id BIGINT NOT NULL COMMENT 'ID',
    name VARCHAR(50) NOT NULL COMMENT '标签名称',
    color VARCHAR(20) COMMENT '标签颜色',
    sort_order INT DEFAULT 0 COMMENT '排序',
    campus_id BIGINT COMMENT '校区ID（null表示全局标签）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员标签表';

-- 学员标签关联表
CREATE TABLE IF NOT EXISTS stu_student_tag (
    id BIGINT NOT NULL COMMENT 'ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    tag_id BIGINT NOT NULL COMMENT '标签ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_tag (student_id, tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学员标签关联表';

-- =============================================
-- 教学模块表 (tch_)
-- =============================================

-- 教师表
CREATE TABLE IF NOT EXISTS tch_teacher (
    id BIGINT NOT NULL COMMENT '教师ID',
    teacher_no VARCHAR(50) NOT NULL COMMENT '教师编号',
    name VARCHAR(50) NOT NULL COMMENT '教师姓名',
    gender TINYINT DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    avatar VARCHAR(500) COMMENT '头像',
    id_card VARCHAR(20) COMMENT '身份证号',
    entry_date DATE COMMENT '入职日期',
    teaching_years INT DEFAULT 0 COMMENT '教龄（年）',
    education VARCHAR(50) COMMENT '学历',
    graduate_school VARCHAR(100) COMMENT '毕业院校',
    major VARCHAR(100) COMMENT '专业',
    introduction TEXT COMMENT '教师简介',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-在职，leave-休假，resigned-离职',
    user_id BIGINT COMMENT '关联用户ID',
    campus_id BIGINT COMMENT '校区ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_teacher_no (teacher_no),
    KEY idx_campus_id (campus_id),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教师表';

-- 课程分类表
CREATE TABLE IF NOT EXISTS tch_course_category (
    id BIGINT NOT NULL COMMENT 'ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程分类表';

-- 课程表
CREATE TABLE IF NOT EXISTS tch_course (
    id BIGINT NOT NULL COMMENT '课程ID',
    name VARCHAR(100) NOT NULL COMMENT '课程名称',
    code VARCHAR(50) NOT NULL COMMENT '课程编码',
    category_id BIGINT COMMENT '课程分类ID',
    type VARCHAR(20) DEFAULT 'small_class' COMMENT '课程类型：one_to_one-一对一，small_class-小班课，large_class-大班课',
    description TEXT COMMENT '课程简介',
    cover_image VARCHAR(500) COMMENT '课程封面',
    duration INT DEFAULT 45 COMMENT '单次课时长（分钟）',
    total_hours INT DEFAULT 0 COMMENT '总课时数',
    original_price DECIMAL(10,2) COMMENT '原价',
    price DECIMAL(10,2) COMMENT '售价',
    hour_price DECIMAL(10,2) COMMENT '单课时价格',
    min_age INT COMMENT '适合年龄段（最小）',
    max_age INT COMMENT '适合年龄段（最大）',
    status TINYINT DEFAULT 0 COMMENT '状态：0-下架，1-上架',
    sort_order INT DEFAULT 0 COMMENT '排序',
    campus_id BIGINT COMMENT '校区ID（null表示全部校区可用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code),
    KEY idx_category_id (category_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='课程表';

-- 班级表
CREATE TABLE IF NOT EXISTS tch_class (
    id BIGINT NOT NULL COMMENT '班级ID',
    name VARCHAR(100) NOT NULL COMMENT '班级名称',
    code VARCHAR(50) COMMENT '班级编码',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    teacher_id BIGINT COMMENT '主讲教师ID',
    assistant_id BIGINT COMMENT '助教ID',
    classroom_id BIGINT COMMENT '教室ID',
    campus_id BIGINT NOT NULL COMMENT '校区ID',
    capacity INT DEFAULT 20 COMMENT '班级容量',
    current_count INT DEFAULT 0 COMMENT '当前人数',
    start_date DATE COMMENT '开班日期',
    end_date DATE COMMENT '结班日期',
    status VARCHAR(20) DEFAULT 'pending' COMMENT '状态：pending-待开班，ongoing-进行中，finished-已结班，cancelled-已取消',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_course_id (course_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_campus_id (campus_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级表';

-- 班级学员关联表
CREATE TABLE IF NOT EXISTS tch_class_student (
    id BIGINT NOT NULL COMMENT 'ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    join_date DATE COMMENT '加入日期',
    leave_date DATE COMMENT '退出日期',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态：active-在读，left-已退出',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_class_student (class_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='班级学员关联表';

-- 教室表
CREATE TABLE IF NOT EXISTS sys_classroom (
    id BIGINT NOT NULL COMMENT '教室ID',
    name VARCHAR(50) NOT NULL COMMENT '教室名称',
    campus_id BIGINT NOT NULL COMMENT '校区ID',
    capacity INT DEFAULT 20 COMMENT '容纳人数',
    equipment VARCHAR(500) COMMENT '设备配置',
    status TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    sort_order INT DEFAULT 0 COMMENT '排序',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_campus_id (campus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='教室表';

-- 排课表
CREATE TABLE IF NOT EXISTS tch_schedule (
    id BIGINT NOT NULL COMMENT 'ID',
    class_id BIGINT COMMENT '班级ID',
    course_id BIGINT COMMENT '课程ID',
    teacher_id BIGINT COMMENT '教师ID',
    classroom_id BIGINT COMMENT '教室ID',
    campus_id BIGINT COMMENT '校区ID',
    schedule_date DATE NOT NULL COMMENT '上课日期',
    start_time TIME NOT NULL COMMENT '开始时间',
    end_time TIME NOT NULL COMMENT '结束时间',
    class_hours INT DEFAULT 1 COMMENT '课时数',
    status VARCHAR(20) DEFAULT 'scheduled' COMMENT '状态：scheduled-已排课，ongoing-进行中，finished-已完成，cancelled-已取消',
    lesson_no INT COMMENT '课节序号',
    topic VARCHAR(200) COMMENT '课节主题',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    KEY idx_class_id (class_id),
    KEY idx_teacher_id (teacher_id),
    KEY idx_classroom_id (classroom_id),
    KEY idx_schedule_date (schedule_date),
    KEY idx_campus_id (campus_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='排课表';

-- 考勤表
CREATE TABLE IF NOT EXISTS tch_attendance (
    id BIGINT NOT NULL COMMENT 'ID',
    schedule_id BIGINT NOT NULL COMMENT '排课ID',
    student_id BIGINT NOT NULL COMMENT '学员ID',
    status VARCHAR(20) DEFAULT 'absent' COMMENT '状态：present-出勤，absent-缺勤，late-迟到，leave-请假',
    sign_time DATETIME COMMENT '签到时间',
    class_hours INT DEFAULT 1 COMMENT '消耗课时',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    create_by BIGINT COMMENT '创建人',
    update_by BIGINT COMMENT '更新人',
    deleted TINYINT DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (id),
    UNIQUE KEY uk_schedule_student (schedule_id, student_id),
    KEY idx_student_id (student_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考勤表';

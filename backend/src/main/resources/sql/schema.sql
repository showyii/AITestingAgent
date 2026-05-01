CREATE TABLE IF NOT EXISTS requirement_task (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    task_no VARCHAR(64) NOT NULL COMMENT '任务编号，业务唯一',
    title VARCHAR(255) NOT NULL COMMENT '需求标题',
    requirement_text TEXT NOT NULL COMMENT '用户输入的完整需求描述',
    source_type VARCHAR(32) NOT NULL DEFAULT 'MANUAL' COMMENT '来源类型：MANUAL/API/IMPORT',
    priority VARCHAR(16) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：LOW/MEDIUM/HIGH/CRITICAL',
    status VARCHAR(32) NOT NULL DEFAULT 'NEW' COMMENT '任务状态：NEW/PROCESSING/DONE/FAILED',
    biz_domain VARCHAR(64) DEFAULT NULL COMMENT '业务域，如支付/登录/订单',
    expected_deadline DATETIME DEFAULT NULL COMMENT '期望完成时间',
    ai_model VARCHAR(128) DEFAULT NULL COMMENT '执行任务使用的模型名称',
    created_by VARCHAR(64) DEFAULT NULL COMMENT '创建人',
    updated_by VARCHAR(64) DEFAULT NULL COMMENT '更新人',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_requirement_task_task_no (task_no),
    KEY idx_requirement_task_status (status),
    KEY idx_requirement_task_priority (priority),
    KEY idx_requirement_task_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='需求任务表';

CREATE TABLE IF NOT EXISTS test_case (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    case_no VARCHAR(64) NOT NULL COMMENT '测试用例编号，业务唯一',
    requirement_task_id BIGINT NOT NULL COMMENT '关联需求任务ID',
    case_title VARCHAR(255) NOT NULL COMMENT '测试用例标题',
    case_type VARCHAR(32) NOT NULL DEFAULT 'FUNCTIONAL' COMMENT '用例类型：FUNCTIONAL/INTERFACE/UI/PERFORMANCE/SECURITY',
    preconditions TEXT DEFAULT NULL COMMENT '前置条件',
    test_steps TEXT NOT NULL COMMENT '测试步骤',
    expected_result TEXT NOT NULL COMMENT '预期结果',
    actual_result TEXT DEFAULT NULL COMMENT '实际结果',
    execute_status VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '执行状态：PENDING/PASSED/FAILED/BLOCKED/SKIPPED',
    severity VARCHAR(16) NOT NULL DEFAULT 'MEDIUM' COMMENT '严重级别：LOW/MEDIUM/HIGH/CRITICAL',
    automation_flag TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否可自动化：0否1是',
    tags VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
    generated_by_ai TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否AI生成：0否1是',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_test_case_case_no (case_no),
    KEY idx_test_case_requirement_task_id (requirement_task_id),
    KEY idx_test_case_execute_status (execute_status),
    KEY idx_test_case_case_type (case_type),
    CONSTRAINT fk_test_case_requirement_task FOREIGN KEY (requirement_task_id)
        REFERENCES requirement_task(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试用例表';

CREATE TABLE IF NOT EXISTS bug_analysis (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    analysis_no VARCHAR(64) NOT NULL COMMENT '缺陷分析编号，业务唯一',
    requirement_task_id BIGINT NOT NULL COMMENT '关联需求任务ID',
    test_case_id BIGINT DEFAULT NULL COMMENT '关联测试用例ID（可空）',
    bug_title VARCHAR(255) NOT NULL COMMENT '缺陷标题',
    bug_description TEXT NOT NULL COMMENT '缺陷现象描述',
    root_cause TEXT DEFAULT NULL COMMENT '根因分析',
    impact_scope TEXT DEFAULT NULL COMMENT '影响范围',
    fix_suggestion TEXT DEFAULT NULL COMMENT '修复建议',
    risk_level VARCHAR(16) NOT NULL DEFAULT 'MEDIUM' COMMENT '风险级别：LOW/MEDIUM/HIGH/CRITICAL',
    reproducibility VARCHAR(16) NOT NULL DEFAULT 'ALWAYS' COMMENT '复现概率：ALWAYS/SOMETIMES/RARE',
    status VARCHAR(32) NOT NULL DEFAULT 'OPEN' COMMENT '状态：OPEN/CONFIRMED/FIXED/CLOSED/REJECTED',
    analyzed_by VARCHAR(64) DEFAULT NULL COMMENT '分析人（可为AI标识）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_bug_analysis_analysis_no (analysis_no),
    KEY idx_bug_analysis_requirement_task_id (requirement_task_id),
    KEY idx_bug_analysis_test_case_id (test_case_id),
    KEY idx_bug_analysis_status (status),
    KEY idx_bug_analysis_risk_level (risk_level),
    CONSTRAINT fk_bug_analysis_requirement_task FOREIGN KEY (requirement_task_id)
        REFERENCES requirement_task(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT,
    CONSTRAINT fk_bug_analysis_test_case FOREIGN KEY (test_case_id)
        REFERENCES test_case(id)
        ON DELETE SET NULL
        ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='缺陷分析表';

CREATE TABLE IF NOT EXISTS test_report (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    report_no VARCHAR(64) NOT NULL COMMENT '测试报告编号，业务唯一',
    requirement_task_id BIGINT NOT NULL COMMENT '关联需求任务ID',
    report_title VARCHAR(255) NOT NULL COMMENT '报告标题',
    summary TEXT DEFAULT NULL COMMENT '报告摘要',
    total_cases INT NOT NULL DEFAULT 0 COMMENT '用例总数',
    passed_cases INT NOT NULL DEFAULT 0 COMMENT '通过数',
    failed_cases INT NOT NULL DEFAULT 0 COMMENT '失败数',
    blocked_cases INT NOT NULL DEFAULT 0 COMMENT '阻塞数',
    skipped_cases INT NOT NULL DEFAULT 0 COMMENT '跳过数',
    defect_count INT NOT NULL DEFAULT 0 COMMENT '缺陷总数',
    pass_rate DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '通过率（百分比）',
    quality_score DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '质量评分（0-100）',
    conclusion VARCHAR(32) NOT NULL DEFAULT 'PENDING' COMMENT '结论：PASS/FAIL/PENDING',
    report_content LONGTEXT DEFAULT NULL COMMENT '完整报告内容（可为Markdown/JSON）',
    generated_by VARCHAR(64) DEFAULT NULL COMMENT '生成人（可为AI标识）',
    generated_at DATETIME DEFAULT NULL COMMENT '生成时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_test_report_report_no (report_no),
    KEY idx_test_report_requirement_task_id (requirement_task_id),
    KEY idx_test_report_conclusion (conclusion),
    CONSTRAINT fk_test_report_requirement_task FOREIGN KEY (requirement_task_id)
        REFERENCES requirement_task(id)
        ON DELETE CASCADE
        ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试报告表';


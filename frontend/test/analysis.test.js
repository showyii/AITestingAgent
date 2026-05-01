import test from 'node:test'
import assert from 'node:assert/strict'
import {
  extractTestCasePayload,
  extractAnalysisPayload,
  extractDefectAnalysisPayload,
  formatText,
  normalizeAnalysisResult,
  normalizeDefectAnalysisResult,
  normalizeTestCaseResult
} from '../src/utils/analysis.js'

test('兼容 res.data.data.analysis 结构', () => {
  const response = {
    data: {
      success: true,
      data: {
        analysis: {
          summary: '登录需求分析',
          coreFunctions: ['登录', '验证码'],
          risks: ['频控']
        }
      }
    }
  }

  const payload = extractAnalysisPayload(response)
  const result = normalizeAnalysisResult(payload)

  assert.equal(result.summary, '登录需求分析')
  assert.deepEqual(result.coreFunctions, ['登录', '验证码'])
  assert.deepEqual(result.risks, ['频控'])
})

test('兼容 res.data.data 包含包装层对象', () => {
  const response = {
    data: {
      success: true,
      data: {
        step: 'analyzeRequirement',
        success: true,
        data: {
          summary: '创建任务',
          coreFunctions: ['鉴权', '创建'],
          risks: ['重复提交']
        },
        analysis: {
          summary: '创建任务',
          coreFunctions: ['鉴权', '创建'],
          risks: ['重复提交']
        }
      }
    }
  }

  const result = normalizeAnalysisResult(extractAnalysisPayload(response))

  assert.equal(result.summary, '创建任务')
  assert.deepEqual(result.coreFunctions, ['鉴权', '创建'])
  assert.deepEqual(result.risks, ['重复提交'])
})

test('兼容 res.data.analysis 与字符串 JSON', () => {
  const response = {
    data: {
      analysis: JSON.stringify({
        summary: '支付流程',
        coreFunctions: ['下单', '支付'],
        risks: ['超时']
      })
    }
  }

  const result = normalizeAnalysisResult(extractAnalysisPayload(response))

  assert.equal(result.summary, '支付流程')
  assert.deepEqual(result.coreFunctions, ['下单', '支付'])
  assert.deepEqual(result.risks, ['超时'])
})

test('测试维度回退到 testDimensions', () => {
  const result = normalizeAnalysisResult({
    summary: '导出报表',
    coreFunctions: ['筛选', '导出'],
    testDimensions: ['权限', '性能']
  })

  assert.equal(result.summary, '导出报表')
  assert.deepEqual(result.coreFunctions, ['筛选', '导出'])
  assert.deepEqual(result.risks, ['权限', '性能'])
})

test('formatText 不让默认值覆盖真实结果', () => {
  assert.equal(formatText('真实摘要'), '真实摘要')
  assert.equal(formatText(''), '-')
  assert.equal(formatText(['A', 'B']), 'A\nB')
})

test('兼容测试用例 res.data.data.testCases 结构', () => {
  const response = {
    data: {
      success: true,
      data: {
        testCases: [
          {
            title: '短信登录成功',
            precondition: '用户已注册',
            steps: ['输入手机号', '输入验证码', '点击登录'],
            expectedResult: '登录成功',
            priority: '高'
          }
        ]
      }
    }
  }

  const result = normalizeTestCaseResult(extractTestCasePayload(response))

  assert.equal(result.length, 1)
  assert.equal(result[0].title, '短信登录成功')
  assert.deepEqual(result[0].steps, ['输入手机号', '输入验证码', '点击登录'])
  assert.equal(result[0].expectedResult, '登录成功')
})

test('兼容测试用例字符串 JSON 与 cases 别名', () => {
  const response = {
    data: {
      data: JSON.stringify({
        cases: [
          {
            name: '密码登录失败',
            preconditions: '账号已存在',
            testSteps: '输入错误密码后提交',
            expected: '提示密码错误',
            priority: 'P1'
          }
        ]
      })
    }
  }

  const result = normalizeTestCaseResult(extractTestCasePayload(response))

  assert.equal(result.length, 1)
  assert.equal(result[0].title, '密码登录失败')
  assert.equal(result[0].precondition, '账号已存在')
  assert.equal(result[0].steps, '输入错误密码后提交')
  assert.equal(result[0].expectedResult, '提示密码错误')
})

test('兼容测试用例单对象返回并自动转数组', () => {
  const response = {
    data: {
      cases: {
        caseNo: 'TC-88',
        caseTitle: '导出报表',
        preconditions: '用户已登录',
        testSteps: ['选择时间范围', '点击导出'],
        expectedResult: '下载成功',
        severity: '中'
      }
    }
  }

  const result = normalizeTestCaseResult(extractTestCasePayload(response))

  assert.equal(result.length, 1)
  assert.equal(result[0].caseId, 'TC-88')
  assert.equal(result[0].title, '导出报表')
  assert.deepEqual(result[0].steps, ['选择时间范围', '点击导出'])
  assert.equal(result[0].priority, '中')
})

test('兼容缺陷分析 res.data.data.analysis 结构', () => {
  const response = {
    data: {
      success: true,
      data: {
        analysis: {
          possibleRootCauses: ['字段映射错误', '后端校验缺失'],
          suggestedFixes: ['统一字段名', '补校验']
        }
      }
    }
  }

  const result = normalizeDefectAnalysisResult(extractDefectAnalysisPayload(response))

  assert.deepEqual(result.possibleCauses, ['字段映射错误', '后端校验缺失'])
  assert.deepEqual(result.fixSuggestions, ['统一字段名', '补校验'])
})

test('兼容缺陷分析 res.data.analysis 字符串 JSON', () => {
  const response = {
    data: {
      analysis: JSON.stringify({
        causes: ['线程竞争'],
        recommendations: ['增加锁控制']
      })
    }
  }

  const result = normalizeDefectAnalysisResult(extractDefectAnalysisPayload(response))

  assert.deepEqual(result.possibleCauses, ['线程竞争'])
  assert.deepEqual(result.fixSuggestions, ['增加锁控制'])
})

test('缺陷分析支持 solutions 与 rootCauses 别名', () => {
  const result = normalizeDefectAnalysisResult({
    rootCauses: ['缓存未刷新'],
    solutions: ['增加失效策略']
  })

  assert.deepEqual(result.possibleCauses, ['缓存未刷新'])
  assert.deepEqual(result.fixSuggestions, ['增加失效策略'])
})

<template>
  <div class="page">
    <header class="header">
      <h1>AI 测试助手</h1>
      <p>需求分析、测试用例生成、缺陷分析、测试报告一站式完成</p>
    </header>

    <main class="container">
      <section class="card">
        <h2>1. 需求输入与 AI 分析</h2>
        <textarea
          v-model="requirementText"
          class="input"
          rows="5"
          placeholder="请输入需求说明，例如：用户可以通过手机号和验证码登录系统..."
        />
        <div class="actions">
          <button class="btn primary" :disabled="loading.analysis" @click="onAnalyzeRequirement">
            {{ loading.analysis ? '分析中...' : 'AI 分析' }}
          </button>
          <button class="btn" :disabled="loading.testCases" @click="onGenerateTestCases">
            {{ loading.testCases ? '生成中...' : '生成测试用例' }}
          </button>
        </div>

        <div v-if="analysisResult" class="result">
          <h3>需求分析结果</h3>
          <p><strong>需求总结/分析摘要：</strong></p>
          <pre>{{ formatText(analysisResult.summary) }}</pre>

          <p><strong>核心功能点：</strong></p>
          <ul v-if="Array.isArray(analysisResult.coreFunctions) && analysisResult.coreFunctions.length" class="result-list">
            <li v-for="(item, idx) in analysisResult.coreFunctions" :key="`core-${idx}`">{{ item }}</li>
          </ul>
          <pre v-else>{{ formatText(analysisResult.coreFunctions) }}</pre>

          <p><strong>测试维度：</strong></p>
          <ul v-if="Array.isArray(analysisResult.risks) && analysisResult.risks.length" class="result-list">
            <li v-for="(item, idx) in analysisResult.risks" :key="`risk-${idx}`">{{ item }}</li>
          </ul>
          <pre v-else>{{ formatText(analysisResult.risks) }}</pre>
        </div>
      </section>

      <section class="card">
        <h2>2. 测试用例</h2>
        <div v-if="testCases.length" class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>用例编号</th>
                <th>用例标题</th>
                <th>前置条件</th>
                <th>测试步骤</th>
                <th>预期结果</th>
                <th>优先级</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(item, index) in testCases" :key="index">
                <td>{{ extractDisplayValue(item.caseId, item.id, `TC-${index + 1}`) }}</td>
                <td>{{ extractDisplayValue(item.title, item.name, item.caseTitle, item.raw) }}</td>
                <td>{{ extractDisplayValue(item.precondition, item.preconditions) }}</td>
                <td>{{ extractDisplayValue(item.steps, item.testSteps) }}</td>
                <td>{{ extractDisplayValue(item.expectedResult, item.expected) }}</td>
                <td>
                  <span class="tag" :class="priorityClass(item.priority)">
                    {{ extractDisplayValue(item.priority, '中') }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
        <p v-else class="empty">尚未生成测试用例</p>
      </section>

      <section class="card">
        <h2>3. 缺陷分析</h2>
        <textarea
          v-model="defectText"
          class="input"
          rows="4"
          placeholder="请输入失败现象或错误日志..."
        />
        <div class="actions">
          <button class="btn primary" :disabled="loading.defect" @click="onAnalyzeDefect">
            {{ loading.defect ? '分析中...' : '缺陷分析' }}
          </button>
        </div>

        <div v-if="defectAnalysisResult" class="result">
          <p><strong>可能原因：</strong></p>
          <ul v-if="Array.isArray(defectAnalysisResult.possibleCauses) && defectAnalysisResult.possibleCauses.length" class="result-list">
            <li v-for="(item, idx) in defectAnalysisResult.possibleCauses" :key="`cause-${idx}`">{{ item }}</li>
          </ul>
          <pre v-else>{{ formatText(defectAnalysisResult.possibleCauses) }}</pre>
          <p><strong>修复建议：</strong></p>
          <ul v-if="Array.isArray(defectAnalysisResult.fixSuggestions) && defectAnalysisResult.fixSuggestions.length" class="result-list">
            <li v-for="(item, idx) in defectAnalysisResult.fixSuggestions" :key="`fix-${idx}`">{{ item }}</li>
          </ul>
          <pre v-else>{{ formatText(defectAnalysisResult.fixSuggestions) }}</pre>
        </div>
      </section>

      <section class="card">
        <h2>4. 测试报告</h2>
        <div class="actions">
          <button class="btn primary" :disabled="loading.report" @click="onGenerateReport">
            {{ loading.report ? '生成中...' : '生成测试报告' }}
          </button>
          <button class="btn" :disabled="!reportText" @click="copyReport">复制文本</button>
        </div>

        <textarea
          v-model="reportText"
          class="input"
          rows="12"
          readonly
          placeholder="点击“生成测试报告”后展示完整报告..."
        />
      </section>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import {
  analyzeRequirement,
  generateTestCases,
  analyzeDefect,
  generateTestReport
} from './api/agent'
import {
  extractTestCasePayload,
  extractAnalysisPayload,
  extractDefectAnalysisPayload,
  formatText,
  normalizeAnalysisResult,
  normalizeDefectAnalysisResult,
  normalizeTestCaseResult
} from './utils/analysis'

const requirementText = ref('')
const defectText = ref('')
const analysisResult = ref(null)
const testCases = ref([])
const defectAnalysisResult = ref(null)
const reportText = ref('')
const errorMsg = ref('')

const loading = ref({
  analysis: false,
  testCases: false,
  defect: false,
  report: false
})

const priorityClass = (priority) => {
  const p = String(priority || '').toLowerCase()
  if (p.includes('高') || p.includes('high') || p === 'p0' || p === 'p1') return 'high'
  if (p.includes('低') || p.includes('low') || p === 'p3') return 'low'
  return 'mid'
}

const extractDisplayValue = (...values) => {
  const matched = values.find((item) => item !== undefined && item !== null)
  return formatText(matched)
}

const normalizeText = (value) => {
  const text = formatText(value).trim()
  return text === '-' ? '' : text
}

const normalizeTextList = (value) => {
  if (Array.isArray(value)) {
    return value
      .map((item) => normalizeText(item))
      .filter(Boolean)
  }

  const text = normalizeText(value)
  return text ? [text] : []
}

const normalizeMetrics = (metrics) => {
  if (!metrics || typeof metrics !== 'object' || Array.isArray(metrics)) return null

  const normalized = {
    total: normalizeText(metrics.total),
    passed: normalizeText(metrics.passed),
    failed: normalizeText(metrics.failed),
    passRate: normalizeText(metrics.passRate)
  }

  return Object.values(normalized).some(Boolean) ? normalized : null
}

const parseReportCandidate = (value) => {
  if (typeof value !== 'string') return value

  const trimmed = value.trim()
  if (!trimmed) return ''
  if (!trimmed.startsWith('{')) return trimmed

  try {
    return JSON.parse(trimmed)
  } catch (error) {
    console.warn('[测试报告] report JSON 字符串解析失败，按普通文本处理', error)
    return trimmed
  }
}

const toReportPayload = (candidate) => {
  const parsedCandidate = parseReportCandidate(candidate)

  if (typeof parsedCandidate === 'string' && parsedCandidate.trim()) {
    return {
      overview: '',
      report: parsedCandidate.trim(),
      metrics: null,
      topRisks: [],
      nextActions: []
    }
  }

  if (!parsedCandidate || typeof parsedCandidate !== 'object' || Array.isArray(parsedCandidate)) {
    return null
  }

  const report = normalizeText(parsedCandidate.report) || normalizeText(parsedCandidate.content)
  const overview = normalizeText(parsedCandidate.overview)
  const metrics = normalizeMetrics(parsedCandidate.metrics)
  const topRisks = normalizeTextList(parsedCandidate.topRisks)
  const nextActions = normalizeTextList(parsedCandidate.nextActions)

  if (report || overview || metrics || topRisks.length || nextActions.length) {
    return {
      overview,
      report,
      metrics,
      topRisks,
      nextActions
    }
  }

  return null
}

const extractReportPayload = (res) => {
  const root = res?.data
  const rawReport = root?.data?.report || root?.report || root
  const candidates = [rawReport, root?.data, root]

  for (const candidate of candidates) {
    const payload = toReportPayload(candidate)
    if (payload) return payload
  }

  return {
    overview: '',
    report: '',
    metrics: null,
    topRisks: [],
    nextActions: []
  }
}

const buildReportText = ({ overview, report, metrics, topRisks, nextActions }) => {
  const sections = ['测试报告']

  if (overview) {
    sections.push(`执行概览：${overview}`)
  }

  if (report) {
    sections.push(`详细结论：\n${report}`)
  }

  if (metrics) {
    const metricLines = [
      `total: ${metrics.total || '-'}`,
      `passed: ${metrics.passed || '-'}`,
      `failed: ${metrics.failed || '-'}`,
      `passRate: ${metrics.passRate || '-'}`
    ]
    sections.push(`统计指标：\n${metricLines.join('\n')}`)
  }

  if (topRisks.length) {
    sections.push(`主要风险：\n${topRisks.map((item, index) => `${index + 1}. ${item}`).join('\n')}`)
  }

  if (nextActions.length) {
    sections.push(`后续建议：\n${nextActions.map((item, index) => `${index + 1}. ${item}`).join('\n')}`)
  }

  return sections.length > 1 ? sections.join('\n\n') : ''
}

const onAnalyzeRequirement = async () => {
  if (!requirementText.value.trim()) {
    errorMsg.value = '请先输入需求说明'
    return
  }
  errorMsg.value = ''
  loading.value.analysis = true
  try {
    const res = await analyzeRequirement(requirementText.value)
    console.log('[AI分析] analyze 接口原始响应:', res)
    const raw = extractAnalysisPayload(res)
    const normalized = normalizeAnalysisResult(raw)
    console.log('[AI分析] 最终 analysisResult:', normalized)
    analysisResult.value = normalized
  } catch (error) {
    errorMsg.value = `需求分析失败：${error?.message || '未知错误'}`
  } finally {
    loading.value.analysis = false
  }
}

const onGenerateTestCases = async () => {
  if (!requirementText.value.trim()) {
    errorMsg.value = '请先输入需求说明'
    return
  }
  errorMsg.value = ''
  loading.value.testCases = true
  try {
    const res = await generateTestCases(requirementText.value, analysisResult.value)
    console.log('生成测试用例接口原始响应', res)
    const raw = extractTestCasePayload(res)
    const normalized = normalizeTestCaseResult(raw)
    console.log('最终 testCases', normalized)
    testCases.value = normalized
  } catch (error) {
    errorMsg.value = `生成测试用例失败：${error?.message || '未知错误'}`
  } finally {
    loading.value.testCases = false
  }
}

const onAnalyzeDefect = async () => {
  if (!defectText.value.trim()) {
    errorMsg.value = '请先输入失败现象或错误日志'
    return
  }
  errorMsg.value = ''
  loading.value.defect = true
  try {
    const res = await analyzeDefect(defectText.value)
    console.log('[缺陷分析] analyze-bug 接口原始响应:', res)
    const raw = extractDefectAnalysisPayload(res)
    const normalized = normalizeDefectAnalysisResult(raw)
    console.log('[缺陷分析] 最终 defectAnalysisResult:', normalized)
    defectAnalysisResult.value = normalized
  } catch (error) {
    errorMsg.value = `缺陷分析失败：${error?.message || '未知错误'}`
  } finally {
    loading.value.defect = false
  }
}

const onGenerateReport = async () => {
  errorMsg.value = ''
  loading.value.report = true
  try {
    const payload = {
      requirement: requirementText.value,
      analysis: analysisResult.value,
      testCases: testCases.value,
      defectInput: defectText.value,
      defectAnalysis: defectAnalysisResult.value
    }
    const res = await generateTestReport(payload)
    const reportPayload = extractReportPayload(res)
    const parsedReport = buildReportText(reportPayload)

    if (parsedReport) {
      reportText.value = parsedReport
      errorMsg.value = ''
      return
    }

    reportText.value = ''
    errorMsg.value = '生成测试报告失败：未解析到报告内容'
  } catch (error) {
    errorMsg.value = `生成测试报告失败：${error?.message || '未知错误'}`
  } finally {
    loading.value.report = false
  }
}

const copyReport = async () => {
  if (!reportText.value) return
  try {
    await navigator.clipboard.writeText(reportText.value)
  } catch (error) {
    errorMsg.value = `复制失败：${error?.message || '未知错误'}`
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

.page {
  min-height: 100vh;
  background: #f5f7fb;
  color: #1f2d3d;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, 'PingFang SC', sans-serif;
}

.header {
  padding: 28px 16px 10px;
  text-align: center;
}

.header h1 {
  margin: 0;
  font-size: 30px;
}

.header p {
  margin: 10px 0 0;
  color: #5f6b7a;
}

.container {
  width: min(1100px, 94vw);
  margin: 0 auto;
  padding: 10px 0 32px;
  display: grid;
  gap: 16px;
}

.card {
  background: #fff;
  border-radius: 12px;
  padding: 18px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}

.card h2 {
  margin: 0 0 12px;
  font-size: 19px;
}

.input {
  width: 100%;
  border: 1px solid #dce2ee;
  border-radius: 10px;
  padding: 12px;
  font-size: 14px;
  line-height: 1.6;
  resize: vertical;
  background: #fafbfd;
}

.actions {
  margin-top: 12px;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn {
  border: 1px solid #cfd8ea;
  background: #fff;
  color: #2b3a55;
  border-radius: 8px;
  padding: 8px 14px;
  cursor: pointer;
}

.btn.primary {
  background: #3b82f6;
  border-color: #3b82f6;
  color: #fff;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.result {
  margin-top: 14px;
  border: 1px dashed #d6deef;
  border-radius: 10px;
  background: #f8faff;
  padding: 12px;
}

pre {
  margin: 6px 0 12px;
  white-space: pre-wrap;
  word-break: break-word;
  font-family: inherit;
}

.result-list {
  margin: 6px 0 12px;
  padding-left: 20px;
}

.result-list li {
  line-height: 1.7;
  margin: 2px 0;
}

.table-wrapper {
  overflow: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

th,
td {
  border: 1px solid #e4e9f3;
  padding: 10px;
  text-align: left;
  vertical-align: top;
}

th {
  background: #f0f4fb;
}

.tag {
  display: inline-block;
  border-radius: 999px;
  padding: 2px 8px;
  font-size: 12px;
  font-weight: 600;
}

.tag.high {
  background: #fee2e2;
  color: #b91c1c;
}

.tag.mid {
  background: #fef3c7;
  color: #92400e;
}

.tag.low {
  background: #dcfce7;
  color: #166534;
}

.empty {
  color: #6b7280;
  margin: 4px 0 0;
}

.error {
  margin: 0;
  padding: 10px 12px;
  border-radius: 8px;
  color: #b42318;
  background: #fff1f2;
  border: 1px solid #fecdd3;
}
</style>

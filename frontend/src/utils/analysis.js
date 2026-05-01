export const parseMaybeJson = (value) => {
  if (typeof value !== 'string') return value
  const trimmed = value.trim()
  if (!trimmed) return value
  if (!(trimmed.startsWith('{') || trimmed.startsWith('['))) return value
  try {
    return JSON.parse(trimmed)
  } catch {
    return value
  }
}

const pickFirstNonEmpty = (...values) =>
  values.find((item) => item !== undefined && item !== null && item !== '')

const pickFirstDefined = (...values) =>
  values.find((item) => item !== undefined && item !== null)

export const normalizeToArray = (value) => {
  const parsed = parseMaybeJson(value)
  if (Array.isArray(parsed)) return parsed
  if (parsed === undefined || parsed === null || parsed === '') return []
  return [String(parsed)]
}

const looksLikeTestCase = (value) => {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false
  return [
    'title',
    'name',
    'precondition',
    'preconditions',
    'steps',
    'testSteps',
    'expected',
    'expectedResult',
    'priority'
  ].some((key) => key in value)
}

export const extractTestCasePayload = (response) => {
  const root = parseMaybeJson(response?.data)
  const candidates = [
    root?.data?.testCases,
    root?.data?.cases,
    root?.testCases,
    root?.cases,
    root?.data,
    root
  ]

  for (const candidate of candidates) {
    const parsed = parseMaybeJson(candidate)
    if (Array.isArray(parsed) || looksLikeTestCase(parsed)) {
      return parsed
    }
  }

  return (
    root?.data?.testCases ??
    root?.data?.cases ??
    root?.testCases ??
    root?.cases ??
    root?.data ??
    root ??
    []
  )
}

export const normalizeTestCaseResult = (source) => {
  const parsed = parseMaybeJson(source)

  if (Array.isArray(parsed)) {
    return parsed
      .map((item) => parseMaybeJson(item))
      .filter((item) => item !== undefined && item !== null && item !== '')
      .map((item, index) => normalizeTestCaseItem(item, index))
  }

  if (!parsed) return []

  if (typeof parsed === 'object') {
    if (Array.isArray(parsed.testCases) || Array.isArray(parsed.cases)) {
      return normalizeTestCaseResult(parsed.testCases ?? parsed.cases)
    }

    if (looksLikeTestCase(parsed)) {
      return [normalizeTestCaseItem(parsed, 0)]
    }
  }

  return [normalizeTestCaseItem(parsed, 0)]
}

export const normalizeTestCaseItem = (item, index = 0) => {
  const parsed = parseMaybeJson(item)

  if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
    return {
      caseId: `TC-${index + 1}`,
      raw: parsed,
      title: typeof parsed === 'string' ? parsed : '',
      precondition: '',
      steps: '',
      expectedResult: '',
      priority: ''
    }
  }

  return {
    ...parsed,
    caseId: pickFirstDefined(parsed.caseId, parsed.caseNo, parsed.id, `TC-${index + 1}`),
    title: pickFirstDefined(parsed.title, parsed.name, parsed.caseTitle, parsed.caseName, ''),
    precondition: pickFirstDefined(parsed.precondition, parsed.preconditions, ''),
    steps: pickFirstDefined(parsed.steps, parsed.testSteps, ''),
    expectedResult: pickFirstDefined(parsed.expectedResult, parsed.expected, ''),
    priority: pickFirstDefined(parsed.priority, parsed.severity, '')
  }
}

const looksLikeAnalysis = (value) => {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false
  return [
    'summary',
    'overview',
    'analysisSummary',
    'analysis_summary',
    'coreFunctions',
    'core_features',
    'core',
    'risks',
    'testDimensions',
    'dimensions',
    'testing_dimensions'
  ].some((key) => key in value)
}

export const extractAnalysisPayload = (response) => {
  const root = parseMaybeJson(response?.data)
  const candidates = [
    root?.data?.analysis,
    root?.analysis,
    root?.data,
    root
  ]

  for (const candidate of candidates) {
    const parsed = parseMaybeJson(candidate)
    if (looksLikeAnalysis(parsed) || typeof parsed === 'string') {
      return parsed
    }
  }

  return root?.data?.analysis ?? root?.analysis ?? root?.data ?? root ?? null
}

const looksLikeDefectAnalysis = (value) => {
  if (!value || typeof value !== 'object' || Array.isArray(value)) return false
  return [
    'possibleCauses',
    'causes',
    'reasons',
    'rootCauses',
    'possibleRootCauses',
    'fixSuggestions',
    'suggestions',
    'solutions',
    'solution',
    'recommendations',
    'suggestedFixes'
  ].some((key) => key in value)
}

export const extractDefectAnalysisPayload = (response) => {
  const root = parseMaybeJson(response?.data)
  const candidates = [
    root?.data?.analysis,
    root?.analysis,
    root?.data,
    root
  ]

  for (const candidate of candidates) {
    const parsed = parseMaybeJson(candidate)
    if (looksLikeDefectAnalysis(parsed) || typeof parsed === 'string') {
      return parsed
    }
  }

  return root?.data?.analysis ?? root?.analysis ?? root?.data ?? root ?? null
}

export const normalizeAnalysisResult = (source) => {
  const parsed = parseMaybeJson(source)

  if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
    return {
      summary: typeof parsed === 'string' ? parsed : '',
      coreFunctions: [],
      risks: []
    }
  }

  return {
    summary: pickFirstNonEmpty(
      parsed.summary,
      parsed.overview,
      parsed.analysisSummary,
      parsed.analysis_summary
    ) ?? '',
    coreFunctions: normalizeToArray(
      pickFirstNonEmpty(parsed.coreFunctions, parsed.core_features, parsed.core)
    ),
    risks: normalizeToArray(
      pickFirstNonEmpty(
        parsed.risks,
        parsed.testDimensions,
        parsed.dimensions,
        parsed.testing_dimensions
      )
    )
  }
}

export const normalizeDefectAnalysisResult = (source) => {
  const parsed = parseMaybeJson(source)

  if (!parsed || typeof parsed !== 'object' || Array.isArray(parsed)) {
    return {
      possibleCauses: typeof parsed === 'string' ? [parsed] : [],
      fixSuggestions: []
    }
  }

  return {
    possibleCauses: normalizeToArray(
      pickFirstNonEmpty(
        parsed.possibleCauses,
        parsed.causes,
        parsed.reasons,
        parsed.reason,
        parsed.rootCauses,
        parsed.possibleRootCauses
      )
    ),
    fixSuggestions: normalizeToArray(
      pickFirstNonEmpty(
        parsed.fixSuggestions,
        parsed.suggestions,
        parsed.solutions,
        parsed.solution,
        parsed.recommendations,
        parsed.suggestedFixes
      )
    )
  }
}

export const formatText = (value) => {
  const parsed = parseMaybeJson(value)
  if (parsed === undefined || parsed === null) return '-'
  if (typeof parsed === 'string') return parsed.trim() ? parsed : '-'
  if (Array.isArray(parsed)) return parsed.length ? parsed.join('\n') : '-'
  if (typeof parsed === 'object') return Object.keys(parsed).length ? JSON.stringify(parsed, null, 2) : '-'
  return String(parsed)
}

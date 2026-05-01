import axios from 'axios'

const http = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const analyzeRequirement = (requirement) => {
  return http.post('/agent/analyze', { requirement })
}

export const generateTestCases = (requirement) => {
  return http.post('/agent/generate-cases', { requirement })
}

export const analyzeDefect = (issueDescription) => {
  return http.post('/agent/analyze-bug', { issueDescription })
}

export const generateTestReport = (payload) => {
  return http.post('/agent/report', {
    requirement: payload?.requirement || '',
    testResultSummary: JSON.stringify(payload || {}, null, 2)
  })
}

export default http

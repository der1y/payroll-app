<template>
  <div class="container">
    <h1>Tips Report - Compute Summaries</h1>

    <div class="controls">
      <label class="file">
        Select CSV file
        <input type="file" accept=".csv" @change="onFile" />
      </label>
      <button @click="loadDefault">Load bundled CSV & Compute</button>
      <input v-model="filter" placeholder="Filter employee or date" />
    </div>

    <div v-if="error" class="error">{{ error }}</div>

    <div v-if="summariesResult.length">
      <h2>Employee Summaries</h2>
      <table>
        <thead>
          <tr>
            <th>Name</th>
            <th>Hours</th>
            <th>Tips</th>
            <th>Wage</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="s in filteredSummaries" :key="s.name">
            <td>{{ s.name }}</td>
            <td>{{ (s.hoursWorked || 0).toFixed(2) }}</td>
            <td>${{ (s.totalTips || 0).toFixed(2) }}</td>
            <td>${{ (s.wage || 0).toFixed(2) }}</td>
          </tr>
        </tbody>
      </table>
    </div>

    <div v-else class="empty">No summaries yet. Load the CSV to compute employee totals.</div>
  </div>
</template>

<script>
import Papa from 'papaparse'
import { ref, computed } from 'vue'

export default {
  name: 'App',
  setup() {
    const rows = ref([])
    const headers = ref([])
    const error = ref('')
    const filter = ref('')
    const summariesResult = ref([])

    function normalizeHeaders(h) {
      return h.map(x => x.trim())
    }

    function onFile(e) {
      const f = e.target.files && e.target.files[0]
      if (!f) return
      Papa.parse(f, {
        header: true,
        skipEmptyLines: false,
        complete: async (res) => {
          headers.value = normalizeHeaders(res.meta.fields || [])
          rows.value = res.data
          await sendToBackend(res.data)
        },
        error: (err) => { error.value = String(err) }
      })
    }

    async function loadDefault() {
      try {
        const resp = await fetch('/tips_report.csv')
        if (!resp.ok) throw new Error('Failed to fetch bundled CSV')
        const text = await resp.text()
        const res = Papa.parse(text, { header: true, skipEmptyLines: false })
        headers.value = normalizeHeaders(res.meta.fields || [])
        rows.value = res.data
        await sendToBackend(res.data)
      } catch (e) {
        error.value = e.message
      }
    }

    async function sendToBackend(parsedRows) {
      // The CSV uses a pattern where the employee name appears on a header row,
      // then subsequent shift rows have an empty first column. We need to
      // propagate the last seen employee name into those rows so the backend
      // can group shifts correctly.
      const mapped = []
      let lastName = ''
      for (const r of parsedRows) {
        const rawName = (r['Employee Name'] || '').trim()
        if (rawName) {
          // skip lines that are summary/total markers
          if (rawName.toLowerCase().includes('total')) {
            continue
          }
          lastName = rawName
        }
        if (!lastName) continue

        const role = (r['Task'] || '').trim()
        if (!role || role.toLowerCase() === 'n/a') continue

        const date = (r['Date'] || '').trim()
        // Only include actual shift rows that have a date
        if (!date) continue

        const timeIn = (r['Time In'] || '').trim()
        const timeOut = (r['Time Out'] || '').trim()
        const tips = parseFloat(((r['Tips'] || '') + '').replace(/[$,]/g, '')) || 0
        const sales = parseFloat(((r['Sales'] || '') + '').replace(/[$,]/g, '')) || 0

        mapped.push({
          name: lastName,
          role,
          date,
          timeIn,
          timeOut,
          tips,
          sales
        })
      }

      try {
        const resp = await fetch('http://localhost:8080/compute', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(mapped)
        })
        if (!resp.ok) throw new Error('Backend compute failed: ' + resp.status)
        const summaries = await resp.json()
        summariesResult.value = summaries
      } catch (e) {
        error.value = e.message
      }
    }

    const filteredSummaries = computed(() => {
      if (!filter.value) return summariesResult.value
      const q = filter.value.toLowerCase()
      return summariesResult.value.filter(r => Object.values(r).some(v => String(v).toLowerCase().includes(q)))
    })

    return { rows, headers, error, filter, onFile, loadDefault, summariesResult, filteredSummaries }
  }
}
</script>

<style>
:root{--bg:#f7fafc;--fg:#0f172a;--muted:#94a3b8}
*{box-sizing:border-box}
body{font-family:Inter,Segoe UI,Arial,sans-serif;background:var(--bg);color:var(--fg);margin:0;padding:24px}
.container{max-width:1000px;margin:0 auto}
h1{margin-bottom:8px}
.controls{display:flex;gap:8px;align-items:center;margin-bottom:16px}
.file input{display:none}
.file{background:#fff;padding:8px 12px;border-radius:6px;border:1px solid #e6edf3;cursor:pointer}
input[type="text"], input[type="file"]{padding:8px;border-radius:6px;border:1px solid #e6edf3}
button{padding:8px 12px;border-radius:6px;border:none;background:#2563eb;color:#fff;cursor:pointer}
table{width:100%;border-collapse:collapse;background:#fff;border-radius:8px;overflow:hidden}
th,td{padding:8px 10px;border-bottom:1px solid #eef2f7;text-align:left}
th{background:#f1f5f9}
.error{color:#b91c1c}
.empty{color:var(--muted)}
</style>

# Payroll Frontend (Vite + Vue)

This small frontend app loads and displays the `tips_report.csv` from the backend project. It supports loading the bundled CSV in `public/tips_report.csv` or uploading a CSV file.

Quick start (Windows, bash):

```bash
cd frontend
npm install
npm run dev
```

Open http://localhost:5173 and click "Load bundled CSV" or upload the CSV.

Notes:
- The app uses PapaParse to parse CSV with headers.
- Place a CSV at `frontend/public/tips_report.csv` or use the file input to upload a local file.
- If you want to serve the CSV from the Java backend, copy `data/tips_report.csv` to your backend static resources or configure a static route to serve it.

# Integration Test Script

The integration test script for post backend services.

## Process

- Run `pnpm install` and `pnpm run build` if it hasn't been run yet.
- Setting up a backend service and empty database.
- Setting the following environment variables or in `.env` file.
  - `SERVICE_URL`: the service URL, e.g. `http://localhost:3000`
  - `ADMIN_TOKEN`: the admin token.
- Run `node dist/app.js`. If the script quits with no error, the test is passed.

# Integration Test Script

The integration test script for post backend services.

## Prerequisites

- Setting up a backend service with empty data.
- Create an admin with the [create admin tool](../createadmin).
- Run `yarn install; yarn run build`.
- Edit `dist/app.js`, modify the definition of the following constances.
  - `adminId`: the ID of the admin.
  - `adminPassword`: the password of the admin.
  - `service`: the URL of the service.
- Run `node dist/app.js`. If the script quits with no error, the test is passed.

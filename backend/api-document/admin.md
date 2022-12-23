# Admin API

## Login

`POST /admin/login`

### Request

Body:

```json
{
    "id": "<Admin ID>",
    "password": "<Admin password>"
}
```

### Response

`200 OK`

Body:

```json
{
    expire: <token expire timestamp>,
    token: "<Auth token>"
}
```

### Errors

- `ID_OR_PASSWORD_INCORRECT`: `403 Forbidden`, the admin ID does not exist, or the password is incorrect.

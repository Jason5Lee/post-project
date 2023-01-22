# User API

## User login

`POST /login`

### Request

Body:

```json
{
  "userName": "<User name>",
  "password": "<User password>"
}
```

### Response

`200 OK`

Body:

```json
{
  "id": "<User ID>",
  "token": "<Auth token>",
  "expire": <token expire time stamp>
}
```

### Errors

- `USER_NAME_OR_PASSWORD_INCORRECT`: `403 Forbidden`, the user name does not exist, or the password is incorrect.

## User register

`POST /register`

### Request

Body:

```json
{
  "userName": "<User name>",
  "password": "<User password>"
}
```

### Response

`201 Created`

Headers:
- `Location` => `/user/<new user ID>`

Body:
  
```json
{
  "userId": "<new user ID>"
}
```

### Errors

- `USER_NAME_ALREADY_EXISTS`: `409 Conflict`, the user name already exists.

## Get user

`GET /user/<user ID>`

Get a user by id.

### Response

`200 OK`

```json
{
  "userName": "<user name>",
  "creationTime": <creation timestamp>
}
```

### Errors

- `USER_NOT_FOUND`: `404 Not Found`, user not found.

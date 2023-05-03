# User API

## User Login

`POST /login`

### Request

Body:

```json
{
  "userName": "<User Name>",
  "password": "<User Password>"
}
```

### Response

`200 OK`

Body:

```json
{
  "id": "<User ID>",
  "token": "<Auth Token>",
  "expire": <Token Expiry Timestamp>
}
```

### Errors

- `USER_NAME_OR_PASSWORD_INCORRECT`: `403 Forbidden` if the user name does not exist, or if the password is incorrect.

## User Registration

`POST /register`

### Request

Body:

```json
{
  "userName": "<User Name>",
  "password": "<User Password>"
}
```

### Response

`201 Created`

Headers:
- `Location`: `/user/<New User ID>`

Body:
  
```json
{
  "userId": "<New User ID>"
}
```

### Errors

- Errors may occur due to an invalid user name.
- Errors may occur due to an invalid password.
- `USER_NAME_ALREADY_EXISTS`: Returns `409 Conflict` if the user name already exists.

## Retrieve User Information

`GET /user/<User ID>`

Retrieves a user's information using their ID.

### Response

`200 OK`

```json
{
  "userName": "<user name>",
  "creationTime": <creation timestamp>
}
```

### Errors

- `USER_NOT_FOUND`: `404 Not Found` if the user could not be found.

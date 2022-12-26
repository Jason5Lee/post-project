# Common Errors

## Errors every API may return

- `INTERNAL_SERVER_ERROR`: `500 Internal Server Error`, the server encountered an internal error. Please report this to the developer.
  - The `reason` field contains a trace ID that will help the developer to locate the error.
- `LOW_PROBABILITY_ERROR`: `500 Internal Server Error`, an error that should be low-probability occurs.
- `BAD_REQUEST`: `400 Bad Request`, the request is invalid. The `reason` field contains the reason.
  - This error should indicate the bug of the client, instead of the user entering invalid value. For example, the client should not send a request with an invalid JSON format.
- `INVALID_TOKEN`: `401 Unauthorized`, the token is invalid.
  - The client-side may remove the token and require the user to login again when receiving this error.
- `FORBIDDEN`: `403 Forbidden`, the user is not allowed to perform this action.

## Invalid Errors

All errors cause by invalid value are `422 Unprocessable Entity`.

### User name

- `USER_NAME_EMPTY`: The user name is empty.
- `USER_NAME_TOO_SHORT`: The user name is too short.
- `USER_NAME_TOO_LONG`: The user name is too long.
- `USER_NAME_ILLEGAL`: The user name contains illegal characters.

### Title

- `TITLE_EMPTY`: The title is empty.
- `TITLE_TOO_SHORT`: The title is too short.
- `TITLE_TOO_LONG`: The title is too long.

### Text post content

- `TEXT_POST_CONTENT_EMPTY`: The post is empty.
- `TEXT_POST_CONTENT_TOO_LONG`: The post is too long.

### URL post content

- `URL_POST_CONTENT_EMPTY`: The URL is empty.
- `URL_POST_CONTENT_TOO_LONG`: The URL is too long.
- `URL_POST_CONTENT_INVALID`: The URL is invalid.

### Password

- `PASSWORD_EMPTY`: The password is empty.
- `PASSWORD_TOO_SHORT`: The password is too short.
- `PASSWORD_TOO_LONG`: The password is too long.

### Size

- `SIZE_NON_POSITIVE`: The size is not a positive number.
  - Note that in some implementations, when size is negative, it is considered as a deserialization error and responded as a Bad Request.

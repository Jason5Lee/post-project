# Common Errors

The following errors may occur in every API.

## General Errors

- `INTERNAL_SERVER_ERROR`: Corresponds to `500 Internal Server Error`. This means the server encountered an internal error. Please report this to the developer.
  - The `reason` field contains a trace ID that can help the developer locate the error.
- `LOW_PROBABILITY_ERROR`: Also corresponds to `500 Internal Server Error`. This error signifies an occurrence of an event that should have a low probability of happening.
- `BAD_REQUEST`: Corresponds to `400 Bad Request`. This means the request was invalid. The `reason` field provides more details.
  - This error generally indicates a bug in the client, not an invalid user input. For example, the client should not send a request with an invalid JSON format.
- `INVALID_AUTH`: Corresponds to `401 Unauthorized`. This signifies an invalid authorization header, which could be expired.
  - When this error is received, the client-side may remove the token and require the user to log in again.

## Invalid Value Errors

All errors caused by invalid values return `422 Unprocessable Entity`.

### User Name

- `USER_NAME_EMPTY`: The user name field is empty.
- `USER_NAME_TOO_SHORT`: The user name provided is too short.
- `USER_NAME_TOO_LONG`: The user name provided is too long.
- `USER_NAME_ILLEGAL`: The user name contains illegal characters.

### Title

- `TITLE_EMPTY`: The title field is empty.
- `TITLE_TOO_SHORT`: The title provided is too short.
- `TITLE_TOO_LONG`: The title provided is too long.

### Text Post Content

- `TEXT_POST_CONTENT_EMPTY`: The content of the post is empty.
- `TEXT_POST_CONTENT_TOO_LONG`: The content of the post is too long.

### URL Post Content

- `URL_POST_CONTENT_EMPTY`: The URL field is empty.
- `URL_POST_CONTENT_TOO_LONG`: The URL provided is too long.
- `URL_POST_CONTENT_INVALID`: The URL provided is invalid.

### Password

- `PASSWORD_EMPTY`: The password field is empty.
- `PASSWORD_TOO_SHORT`: The password provided is too short.
- `PASSWORD_TOO_LONG`: The password provided is too long.

### Size

- `SIZE_NON_POSITIVE_INTEGER`: The size provided is not a positive integer.
  - Depending on the deserialization implementation, an invalid size value might be considered a deserialization error, which would result in a `BAD_REQUEST`.

### Time

- `INVALID_TIME`: The time is not a positive integer.
  - Depending on the deserialization implementation, an invalid time value might be considered a deserialization error, which would result in a `BAD_REQUEST`.
  - In JavaScript-based implementations (e.g. TypeScript), this error will also be returned when the time provided is not a safe integer.

### Page

- `INVALID_PAGE`: The page is not a positive integer.
  - Depending on the deserialization implementation, an invalid page value might be considered a deserialization error, which would result in a `BAD_REQUEST`.
  - In JavaScript-based implementations (e.g. TypeScript), this error will also be returned when the page provided is not a safe integer.

### Page Size

- `INVALID_PAGE_SIZE`: The page size is not a positive integer.
  - Depending on the deserialization implementation, an invalid page size value might be considered a deserialization error, which would result in a `BAD_REQUEST`.
  - In JavaScript-based implementations (e.g. TypeScript), this error will also be returned when the page size provided is not a safe integer.
  
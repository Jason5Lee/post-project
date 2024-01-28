# Common Errors

The following errors may occur in every API.

## General Errors

- `API_NOT_FOUND`: the API endpoint is not found.
- `INTERNAL_SERVER_ERROR`: Corresponds to `500 Internal Server Error`. This means the server encountered an internal error. Please report this to the developer.
  - The `reason` field contains a trace ID that can help the developer locate the error.
- `BAD_REQUEST`: Corresponds to `400 Bad Request`. This means the request was invalid. The `reason` field provides more details.
  - This error generally indicates a bug in the client, not an invalid user input. For example, the client should not send a request with an invalid JSON format.
- `INVALID_AUTH`: Corresponds to `401 Unauthorized`. This signifies an invalid authorization header, which could be expired.
  - When this error is received, the client-side may remove the token and require the user to log in again.

## Invalid Value Errors

All errors resulting from invalid values return a `400 Bad Request` response. Default error identifiers are documented in the [validation document](./validation.md).

Note that specific API endpoints might use custom error identifiers. These identifiers allow for distinction between invalid errors of the same validation type, but originating from different fields.

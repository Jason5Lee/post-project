# API document

## API

- [Identity API](identity.md)
- [User API](user.md)
- [Post API](post.md)

## Authentication

The authentication process requires including the appropriate token in the Authorization header. There are two types of tokens based on user 

For regular users, the format should be `Bearer <token>`, where `<token>` is the token returned from the login process.

For administrative users, the format should be `Admin <admin_token>`, where `<admin_token>` is the token configured for admin access. 

## Error response

If any error happens, the response will be a JSON object with the following format:

```json
{
  "error": {
    "error": "<error identity>",
    "reason": "<reason>"
  }
}
```

`error` is a unique identity string of the error. `reason` is the error description.

## Common errors

Some errors may be returned by multiple APIs. They are listed in [the common error](common-error.md) page.

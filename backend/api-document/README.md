# API document

## API

- [Identity API](identity.md)
- [User API](user.md)
- [Admin API](admin.md)
- [Post API](post.md)

## Error response

If any error happens, the response will be a JSON object with the following format:

```json
{
  "error": {
    "error": "<error identity>",
    "reason": "<reason>",
    "message": "<error message>"
  }
}
```

`error` is a unique identity string of the error. `reason` is the error description for the developer. `message` is the message that is presentable on user interfaces.

## Common errors

Some errors may be returned by multiple APIs. They are listed in [the common error](common-error.md) page.

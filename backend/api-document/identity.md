# Identity API

## Get Identity

`GET /identity`

Retrieves the identity of the current user.

### Response

`200 OK`

Body:

```json
{
    "user": {
        "id": "<user ID>",
        "name": "<user name>"
    },
    "admin": true
}
```

Either the `user` or `admin` field will be present, but not both. If there is no authorization header, an empty object will be returned.

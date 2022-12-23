# Identity API

## Get Identity

`GET /identity`

Get the identity of the current user.

### Response

`200 OK`

Body:

```json
{
    "user": {
        "id": "<user ID>",
        "name": "<user name>"
    },
    "admin": {
        "id": "<admin ID>"
    }
}
```

At most one of `user` and `admin` field will present. Without authorization header, an empty object will be responded.

# Post API

## Get a post

`GET /post/<post ID>`

Retrieve a post by its ID.

### Response

`200 OK`

Body:

```json
{
    "creatorId": "<creator ID>",
    "creatorName": "<creator name>",
    "creationTime": <creation timestamp>,
    "title": "<post title>",
    "text": "<post content for text type, if applicable>",
    "url": "<post content for URL type, if applicable>",
    "lastModified": <last modified timestamp, if the post has been modified>
}
```

### Errors

- `POST_NOT_FOUND`: `404 Not Found`, the post was not found.

## Create a post

`POST /post`

Authorization: user only.

### Request

Body:

```json
{
    "title": "<post title>",
    "text": "<post content for text type>",
    "url": "<post content for URL type>"
}
```

Either the `text` or the `url` field should exist, but not both.

### Response

`201 Created`

Header: `Location` => `/post/<created post ID>`

Body:

```json
{
    "postId": "<created post ID>"
}
```

### Errors

- `USER_ONLY`: `403 Forbidden`, only users are permitted to create posts.
- Errors related to invalid title.
- Errors related to invalid text post content.
- Errors related to invalid URL post content.
- `TEXT_URL_EXACT_ONE`: `400 Bad Request`, exactly one of `url` and `text` should be present.
- `DUPLICATE_TITLE`: `409 Conflict`, the post title already exists.

## Edit a post

`PATCH /post/<post ID>`

Authorization: post creator only.

### Request

Body:

```json
{
    "text": "<post content for text type>",
    "url": "<post content for URL type>"
}
```

Either the `text` or the `url` field should exist, but not both.

### Response

`204 No Content`

### Errors

- Errors related to invalid text post content.
- Errors related to invalid URL post content.
- `TEXT_URL_EXACT_ONE`: `400 Bad Request`, exactly one of `url` and `text` should be present.
- `POST_NOT_FOUND`: `404 Not Found`, the post does not exist.
- `NOT_CREATOR`: `403 Forbidden`, the user is not the post's creator.
- `TYPE_DIFF`: `400 Bad Request`, the post type does not match the request.

## Delete a post

`DELETE /post/<post ID>`

Authorization: post creator or admin only.

### Response

`204 No Content`

### Errors

- `NOT_CREATOR_ADMIN`: `403 Forbidden`, the requester is neither the post's creator nor an admin.
- `POST_NOT_FOUND`: `404 Not Found`, the post does not exist.

## List posts information

`GET /post`

### Request

Query parameters:
- `page`: the page number.
- `pageSize`: the size of the page.
- `creator`: If present, only the posts created by the user with the ID specified in this parameter will be returned.
- `search`: If present, only the posts that match the search term specified in this parameter will be returned. Note that not all implementations support this parameter.

### Response

`200 OK`

Body:

```json
{
    "total": <total number of all posts (not just the page)>,
    "posts": [
        {
            "id": "<post ID>",
            "title": "<post title>",
            "creatorId": "<creator ID>",
            "creatorName": "<creator name>",
            "creationTime": <creation timestamp>
        },
        ...
    ]
}
```

The posts are sorted by their creation time in descending order.

### Errors

- Errors related to invalid page.
- Errors related to invalid page size.
- `CREATOR_NOT_FOUND`: `404 Not Found`, the specified creator does not exist.
- `SEARCH_NOT_IMPLEMENTED`: `501 Not Implemented`, the search function is not implemented in this service.

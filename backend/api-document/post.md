# Post API

## Get a post

`GET /post/<post ID>`

Get a post by ID.

### Response

`200 OK`

Body:

```json
{
    "creatorId": "<creator ID>",
    "creatorName": "<creator name>",
    "creationTime": <creation timestamp>,
    "title": "<post title>",
    "post": "<post content, exists only for post type>",
    "url": "<post URL, exists only for URL type>",
    "lastModified": <last modified timestamp, exists only if it has been modified>
}
```

### Errors

- `POST_NOT_FOUND`: `404 Not Found`, post not found.

## Create a post

`PUT /post`

Authorization: user only.

### Request

Body:

```json
{
    "title": "<post title>",
    "post": "<the post content>",
    "url": "<the URL content>"
}
```

Only exact one of `url` and `post` should present.

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

- Invalid errors of Title.
- Invalid errors of Post content.
- `POST_AND_URL_SHOULD_HAVE_EXACTLY_ONE`: `422 Unprocessable Entity`, only exact one of `url` and `post` should present.
- `DUPLICATE_TITLE`: `409 Conflict`, the post title duplicated.

## Edit a post

`POST /post/<post ID>`

Authorization: user only.

### Request

Body:

```json
{
    "post": "<the post content>",
    "url": "<the URL content>"
}
```

Note that you cannot change the post type. Only the corresponding field should be present.

### Response

`204 No Content`

### Errors

- Invalid errors of Post content.
- `ONLY_EXACT_ONE_OF_POST_URL`: `422 Unprocessable Entity`, only exact one of `url` and `post` should present.
- `POST_NOT_FOUND`: `404 Not Found`, the post does not exist.
- `NOT_CREATOR`: `403 Forbidden`, the user is not the creator of the post.
- `TYPE_DIFF`: `422 Unprocessable Entity`, the type of the post is different from the request.

## Delete a post

`DELETE /post/<post ID>`

Authorization: post creator or admin only.

### Response

`204 No Content`

### Errors

- `FORBIDDEN`: `403 Forbidden`, the caller is neither the creator of the post nor admin.
- `POST_NOT_FOUND`: `404 Not Found`, the post does not exist.


## List posts information

`GET /post`

### Request

Query parameters:
- `before`: the timestamp of the last post in the previous page. If not present, the latest posts will be returned.
- `after`: the timestamp of the first post in the next page.
- `size`: the maximum number of posts to return. If not present, the default value is 20. The maximum value is 500.
- `creator`: the ID of the creator of the posts. If not present, all posts will be returned.

### Response

`200 OK`

Body:

```json
{
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

### Errors

- `BOTH_BEFORE_AFTER`: `422 Unprocessable Entity`, both `before` and `after` are present.
- `CREATOR_NOT_FOUND`: `404 Not Found`, the creator does not exist.
- Invalid errors of Size.
# Validation

## User Name

A username is a string with a length of at least 3 and no more than 20 characters. It must only contain uppercase and lowercase letters, digits, underscores (`_`), and hyphens (`-`).

**Default Invalid Error Identifier**: `INVALID_USER_NAME`

## Password

A password is a string with a length of at least 5. The maximum length of a password is determined by the encryption algorithm used; for example, bcrypt has a limit of 72 characters.

**Default Invalid Error Identifier**: `INVALID_PASSWORD`

## Post Title

A title of a post is a non-empty string with no more than 171 characters.

**Default Invalid Error Identifier**: `INVALID_TITLE`

## Text Post Content

The content of a text post is a string with no more than 65535 characters. The content of the text post can be empty.

**Default Invalid Error Identifier**: `INVALID_TEXT_POST_CONTENT`

## URL Post Content

The content of a URL post is a non-empty valid URL string with no more than 65535 characters.

**Default Invalid Error Identifier**: `INVALID_URL_POST_CONTENT`

## Timestamp

A timestamp is defined as the number of milliseconds elapsed since the Unix epoch (00:00:00 UTC on 1 January 1970).

The timestamp must be a positive integer.

The maximum allowed value of the timestamp depends on the limitations of the runtime environment.

**Default Invalid Error Identifier**: `INVALID_TIMESTAMP`

## Page

A page is identified by a 1-based index number, which must be a positive integer.

The maximum allowed value of the page depends on the limitations of the runtime environment.

**Default Invalid Error Identifier**: `INVALID_PAGE`

## Page Size

A page size is a positive integer and cannot exceed 50.

**Default Invalid Error Identifier**: `INVALID_PAGE_SIZE`

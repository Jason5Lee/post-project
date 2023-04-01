from typing import Final
from utils.error import ErrorBody

empty: Final[ErrorBody] = {
    "error": {
        "error": "URL_POST_CONTENT_EMPTY",
        "reason": "URL post content cannot be empty",
        "message": "URL post content cannot be empty",
    }
}

tooLong: Final[ErrorBody] = {
    "error": {
        "error": "URL_POST_CONTENT_TOO_LONG",
        "reason": "URL is too long",
        "message": "URL is too long",
    }
}

invalid: Final[ErrorBody] = {
    "error": {
        "error": "URL_POST_CONTENT_INVALID",
        "reason": "URL is invalid",
        "message": "URL is invalid",
    }
}

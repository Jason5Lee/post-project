from typing import Final
from utils.error import ErrorBody

empty: Final[ErrorBody] = {
    "error": {
        "error": "TEXT_POST_CONTENT_EMPTY",
        "reason": "Text post content cannot be empty",
        "message": "Text post content cannot be empty",
    }
}

tooLong: Final[ErrorBody] = {
    "error": {
        "error": "TEXT_POST_CONTENT_TOO_LONG",
        "reason": "Text post content is too long",
        "message": "Text post content is too long",
    }
}

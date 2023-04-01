from typing import Final
from utils.error import ErrorBody

empty: Final[ErrorBody] = {
    "error": {
        "error": "TITLE_EMPTY",
        "reason": "Title cannot be empty",
        "message": "Title cannot be empty",
    }
}

tooShort: Final[ErrorBody] = {
    "error": {
        "error": "TITLE_TOO_SHORT",
        "reason": "Title is too short",
        "message": "Title is too short",
    }
}

tooLong: Final[ErrorBody] = {
    "error": {
        "error": "TITLE_TOO_LONG",
        "reason": "Title is too long",
        "message": "Title is too long",
    }
}

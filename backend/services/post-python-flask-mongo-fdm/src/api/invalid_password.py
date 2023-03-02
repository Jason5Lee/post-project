from typing import Final
from utils.error import ErrorBody

empty: Final[ErrorBody] = {
    "error": {
        "error": "PASSWORD_EMPTY",
        "reason": "Password cannot be empty",
        "message": "Password cannot be empty",
    }
}

tooShort: Final[ErrorBody] = {
    "error": {
        "error": "PASSWORD_TOO_SHORT",
        "reason": "Password is too short",
        "message": "Password is too short",
    }
}

tooLong: Final[ErrorBody] = {
    "error": {
        "error": "PASSWORD_TOO_LONG",
        "reason": "Password is too long",
        "message": "Password is too long",
    }
}

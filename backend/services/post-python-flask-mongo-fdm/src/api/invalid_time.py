from typing import Final
from utils.error import ErrorBody

invalid: Final[ErrorBody] = {
    "error": {
        "error": "INVALID_TIME",
        "reason": "Time must be a non-negative integer",
        "message": "Time must be a non-negative integer"
    }
}

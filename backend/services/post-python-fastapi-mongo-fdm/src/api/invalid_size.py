from typing import Final
from utils.error import ErrorBody

invalid: Final[ErrorBody] = {
    "error": {
        "error": "INVALID_SIZE",
        "reason": "Size must be a positive integer",
        "message": "Size must be a positive integer"
    }
}

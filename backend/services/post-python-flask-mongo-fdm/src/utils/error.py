from typing import Optional, TypedDict, Callable


class ErrorBody(TypedDict):
    error: "ErrBody"


class ErrBody(TypedDict):
    error: str
    reason: str
    message: str


class ResponseError(Exception):
    def __init__(self, status_code: int, body: ErrorBody):
        self.status_code = status_code
        self.body = body

    def __str__(self):
        return f"{self.status_code}: {self.body}"
    

InvalidError = Callable[[ErrorBody], Exception]

def onInvalidRespond(status: int, prefix: Optional[str] = None) -> InvalidError:
    def withoutPrefix(body: ErrorBody) -> Exception:
        return ResponseError(status, body)
    def withPrefix(body: ErrorBody) -> Exception:
        body = body.copy()
        body["error"]["error"] = f"{prefix}_{body['error']['error']}"
        return ResponseError(status, body)
    
    if prefix is None:
        return withoutPrefix
    else:
        return withPrefix

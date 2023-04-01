from abc import abstractmethod
from dataclasses import dataclass
from typing import Generic, Optional, TypeVar, TypedDict, Callable


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
    

T = TypeVar('T', covariant=True)
class ValidationResult(Generic[T]):
    @abstractmethod
    def onInvalidRespond(self, status_code: int, error_prefix: Optional[str] = None) -> T:
        raise NotImplementedError()
    
    @abstractmethod
    def onInvalidThrow(self, exception: Callable[[ErrorBody], Exception]) -> T:
        raise NotImplementedError()


@dataclass
class Valid(ValidationResult[T]):
    value: T

    def onInvalidRespond(self, status_code: int, error_prefix: Optional[str] = None) -> T:
        return self.value
    
    def onInvalidThrow(self, exception: Callable[[ErrorBody], Exception]) -> T:
        return self.value
    

@dataclass
class Invalid(ValidationResult[T]):
    error_body: ErrorBody

    def onInvalidRespond(self, status_code: int, error_prefix: Optional[str] = None) -> T:
        error_body = self.error_body
        if error_prefix is not None:
            error_body = error_body.copy()
            error_body["error"]["error"] = f"{error_prefix}_{error_body['error']['error']}"
        
        raise ResponseError(status_code, error_body)
    
    def onInvalidThrow(self, exception: Callable[[ErrorBody], Exception]) -> T:
        return self.value
    
    
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

# ValidateResult<T>: Valid, Wrapper
# Passing lambda: zero or one, wrapper
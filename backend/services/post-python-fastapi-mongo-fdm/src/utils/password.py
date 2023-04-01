from abc import abstractmethod
from typing import Protocol

class PasswordEncryptor(Protocol):
    @abstractmethod
    def encrypt(self, plain: str) -> str:
        raise NotImplementedError()
    
    @abstractmethod
    def verify(self, plain: str, encrypted: str) -> bool:
        raise NotImplementedError()

import re
from typing import Final, NewType, Optional, Union
from dataclasses import dataclass
from utils.error import InvalidError
from utils.password import PasswordEncryptor
from api import invalid_time, invalid_title, invalid_user_name, invalid_text_post_content, invalid_url_post_content, invalid_password, invalid_size

@dataclass(frozen=True)
class Time:
    utc: int


UserId = NewType("UserId", int)
AdminId = NewType("AdminId", int)

Identity = Union["IdentityUser", "IdentityAdmin"]
@dataclass
class IdentityUser:
    id: UserId
@dataclass
class IdentityAdmin:
    id: AdminId


UserName = NewType("UserName", str)

class Password:
    _plain: str

    def __init__(self, plain: str):
        pass # TODO: verify
        self._plain = plain

    def __str__(self):
        return "********"
    
    def encrypt(self, encryptor: PasswordEncryptor) -> str:
        return encryptor.encrypt(self._plain)
    
    def verify(self, encryptor: PasswordEncryptor, encrypted: str) -> bool:
        return encryptor.verify(self._plain, encrypted)

PostId = NewType("PostId", str)
Title = NewType("Title", str)

TextPostContent = NewType("TextPostContent", str)
UrlPostContent = NewType("UrlPostContent", str)

PostContent = Union["PostContentText", "PostContentUrl"]
@dataclass
class PostContentText:
    content: TextPostContent
@dataclass
class PostContentUrl:
    content: UrlPostContent


Size = NewType("Size", int)

def new_time(utc: int, invalidErr: InvalidError) -> Time:
    if utc < 0:
        raise invalidErr(invalid_time.invalid)
    
    return Time(utc)

_user_name_regex = re.compile(r"^[a-zA-Z0-9_-]+$")
def new_user_name(value: str, invalidErr: InvalidError) -> UserName:
    if len(value) == 0:
        raise invalidErr(invalid_user_name.empty)
    if len(value) < 3:
        raise invalidErr(invalid_user_name.tooShort)
    if len(value) > 20:
        raise invalidErr(invalid_user_name.tooLong)
    if not _user_name_regex.match(value):
        raise invalidErr(invalid_user_name.containsIllegalCharacter)
    
    return UserName(value)

def new_title(value: str, invalidErr: InvalidError) -> Title:
    if len(value) == 0:
        raise invalidErr(invalid_title.empty)
    if len(value) < 3:
        raise invalidErr(invalid_title.tooShort)
    if len(value) > 171:
        raise invalidErr(invalid_title.tooLong)
    
    return Title(value)

def new_text_post_content(value: str, invalidErr: InvalidError) -> TextPostContent:
    if len(value) == 0:
        raise invalidErr(invalid_text_post_content.empty)
    if len(value) > 65535:
        raise invalidErr(invalid_text_post_content.tooLong)
    
    return TextPostContent(value)

def new_url_post_content(value: str, invalidErr: InvalidError) -> UrlPostContent:
    if len(value) == 0:
        raise invalidErr(invalid_url_post_content.empty)
    if len(value) > 65535:
        raise invalidErr(invalid_url_post_content.tooLong)
    if not value.startswith("http://") and not value.startswith("https://"):
        raise invalidErr(invalid_url_post_content.invalid)
    
    return UrlPostContent(value)

def new_password(value: str, invalidErr: InvalidError) -> Password:
    if len(value) == 0:
        raise invalidErr(invalid_password.empty)
    if len(value) < 5:
        raise invalidErr(invalid_password.tooShort)
    if len(value) > 127:
        raise invalidErr(invalid_password.tooLong) # Password being too long creates long hash
    return Password(value)

_default_size: Final[int] = 20
_max_size: Final[int] = 500

def new_size(value: Optional[int], invalidErr: InvalidError) -> Size:
    if value is None:
        return Size(_default_size)
    if value <= 0:
        raise invalidErr(invalid_size.invalid)
    if value > _max_size:
        return Size(_max_size)
    
    return Size(value)

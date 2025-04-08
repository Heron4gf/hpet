# File: hpet/exceptions.py
# Purpose: Define custom exceptions for the hpet library.

class HpetError(Exception):
    """Base exception for errors related to the hpet library."""
    pass

class ContextNotSetError(HpetError):
    """Raised when hpet context-dependent functions/objects are used outside execution."""
    def __init__(self, message="HPET context is not set or accessible."):
        super().__init__(message)

class ApiError(HpetError):
    """Raised when a call to the Java backend API fails."""
    def __init__(self, message="An error occurred during a Java API call."):
        super().__init__(message)
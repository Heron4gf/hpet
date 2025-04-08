# File: hpet/pet.py
# Purpose: Defines the Pet class providing a Pythonic API.

from .exceptions import ApiError

class Pet:
    """
    Represents the pet associated with the current ability execution context.
    Instances are typically obtained via `hpet.pet`.
    """
    def __init__(self, pet_uuid: str, java_api_proxy):
        """
        Internal constructor. Use hpet.pet.
        Args:
            pet_uuid (str): The UUID of the pet this object represents.
            java_api_proxy: The Py4J proxy to the Java PythonEntryPoint.
        """
        if not pet_uuid:
            raise ValueError("Pet UUID cannot be empty.")
        self.uuid: str = pet_uuid
        self._api = java_api_proxy

    def _call_api(self, method_name, *args):
        """Internal helper to call Java API methods specific to pets."""
        try:
            method = getattr(self._api, method_name, None)
            if not method:
                 raise ApiError(f"Java API method '{method_name}' not found in PythonEntryPoint.")
            # Pass self.uuid as the first argument for pet-specific methods
            return method(self.uuid, *args)
        except Exception as e:
            log_method = getattr(self._api, 'logWarning', print)
            log_method(f"API Error calling '{method_name}' for pet {self.uuid}: {e}")
            raise ApiError(f"Failed to call Java API method '{method_name}' for pet") from e

    # --- Pet Specific Methods ---
    def level(self) -> int:
        """Gets the pet's current level."""
        # Assumes getPetLevel(petUuid) exists in Java EntryPoint
        return self._call_api("getPetLevel")

    # Add more methods here based on what Java `PythonEntryPoint` exposes for pets
    # Example:
    # def rename(self, new_name: str):
    #     """Renames the pet."""
    #     return self._call_api("renamePet", str(new_name))

    # --- Standard Python Methods ---
    def __repr__(self):
        # Add more info like type/level if available via API calls
        return f"Pet(uuid='{self.uuid}')"
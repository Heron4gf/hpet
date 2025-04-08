# File: hpet/context.py
# Purpose: Manages the thread-local context for ability executions.

import threading
from .exceptions import ContextNotSetError

# Thread-local storage ensures context isolation between potential concurrent executions
_local_context = threading.local()

def set_current_context(context_data: dict, java_api_proxy):
    """
    Sets the context data and Java API proxy for the current thread.
    Called by the bridge client before executing a user script.

    Args:
        context_data (dict): Data map received from Java (owner UUID, etc.).
        java_api_proxy: The Py4J proxy object for the Java PythonEntryPoint.
    """
    _local_context.data = context_data
    _local_context.java_api = java_api_proxy
    _local_context.is_set = True

def clear_current_context():
    """
    Clears the context for the current thread.
    Called by the bridge client after script execution (or on error).
    """
    _local_context.is_set = False
    # Optionally delete attributes for cleaner garbage collection if needed
    # if hasattr(_local_context, 'data'): del _local_context.data
    # if hasattr(_local_context, 'java_api'): del _local_context.java_api

def _is_context_set() -> bool:
    """Checks if the context has been set for the current thread."""
    return getattr(_local_context, 'is_set', False)

def _get_context_value(key: str, default=None):
    """Safely gets a value from the context data map."""
    if _is_context_set():
        return _local_context.data.get(key, default)
    raise ContextNotSetError(f"Cannot access context value '{key}': Context not set.")

def get_java_api():
    """
    Gets the Py4J Java API proxy object for the current context.

    Returns:
        The Java API proxy object.

    Raises:
        ContextNotSetError: If the context is not currently set.
    """
    if _is_context_set():
        return _local_context.java_api
    raise ContextNotSetError("Cannot access Java API: HPET context not set.")

def get_context_data() -> dict:
    """Gets the raw context data dictionary."""
    if _is_context_set():
        return _local_context.data
    raise ContextNotSetError("Cannot access context data: HPET context not set.")

def get_owner_uuid() -> str:
    """Gets the owner's UUID from the current context."""
    uuid = _get_context_value("owner_uuid")
    if uuid is None:
        raise ContextNotSetError("Owner UUID not found in context.")
    return str(uuid) # Ensure string

def get_pet_uuid() -> str:
    """Gets the pet's UUID from the current context."""
    uuid = _get_context_value("pet_uuid")
    if uuid is None:
        raise ContextNotSetError("Pet UUID not found in context.")
    return str(uuid)

def get_enemy_uuid():
    """Gets the enemy's UUID from the current context (may be None)."""
    return _get_context_value("enemy_uuid") # Default is None if key missing

# --- Internal functions to create contextual Player/Pet instances ---
# These are called by the properties in __init__.py

_player_instance_cache = None
_pet_instance_cache = None

def _get_current_player_instance():
    """Gets or creates the Player instance for the current context owner."""
    global _player_instance_cache
    if not _is_context_set():
         raise ContextNotSetError("Cannot get current player: HPET context not set.")

    owner_uuid = get_owner_uuid() # Raises if owner UUID missing

    # Simple cache invalidation based on UUID change
    if _player_instance_cache is None or _player_instance_cache.uuid != owner_uuid:
         # Import locally to prevent circular imports at module level
         from .player import Player
         _player_instance_cache = Player(owner_uuid, get_java_api())

    return _player_instance_cache

def _get_current_pet_instance():
    """Gets or creates the Pet instance for the current context pet."""
    global _pet_instance_cache
    if not _is_context_set():
         raise ContextNotSetError("Cannot get current pet: HPET context not set.")

    pet_uuid = get_pet_uuid() # Raises if pet UUID missing

    if _pet_instance_cache is None or _pet_instance_cache.uuid != pet_uuid:
          # Import locally
         from .pet import Pet
         _pet_instance_cache = Pet(pet_uuid, get_java_api())

    return _pet_instance_cache
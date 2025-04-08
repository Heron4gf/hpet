# File: hpet/__init__.py
# Purpose: Initializes the hpet package and exports the public API.

# Import the core classes users will need
from .player import Player
from .pet import Pet
# Import custom exceptions if defined
from .exceptions import ContextNotSetError, ApiError

# Import context accessors to provide the 'magic' global-like instances
# These functions internally handle fetching the right object for the current context
from .context import (
    _get_current_player_instance,
    _get_current_pet_instance,
    get_java_api # Expose Java API access for advanced use cases if desired
)

# --- Define Global-like Contextual Accessors ---
# These properties use the functions above to dynamically get the current player/pet
# when the user accesses `hpet.player` or `hpet.pet`.

def _player_prop():
    """Property getter for the contextual player."""
    try:
        return _get_current_player_instance()
    except ContextNotSetError:
        # Re-raise or handle more gracefully if needed
        raise AttributeError("Cannot access hpet.player: HPET context not available.")
    except Exception as e:
        # Catch unexpected errors during instance creation
        raise AttributeError(f"Error creating contextual player instance: {e}") from e

def _pet_prop():
    """Property getter for the contextual pet."""
    try:
        return _get_current_pet_instance()
    except ContextNotSetError:
        raise AttributeError("Cannot access hpet.pet: HPET context not available.")
    except Exception as e:
        raise AttributeError(f"Error creating contextual pet instance: {e}") from e

player = property(_player_prop)
pet = property(_pet_prop)


# --- Utility Functions (Exported) ---

def get_player_by_name(name: str):
    """
    Looks up another player by their exact name.

    Args:
        name (str): The exact player name (case-sensitive).

    Returns:
        Player: The Player object if found and online, otherwise None.
        Raises ContextNotSetError if called outside of ability execution context.
    """
    java_api = get_java_api() # Will raise ContextNotSetError if context is invalid
    try:
        player_proxy = java_api.getPlayerByName(str(name))
        if player_proxy:
            # Wrap the Java Player proxy in our Python Player class
            return Player(player_proxy.getUniqueId().toString(), java_api)
    except Exception as e:
         # Log or handle potential Py4J errors during the call
         print(f"Error calling getPlayerByName via Java API: {e}") # TODO: Use proper logging via java_api?
    return None


# --- Define what gets imported with 'from hpet import *' ---
# (Though explicit imports are generally preferred)
__all__ = [
    # Classes
    'Player',
    'Pet',
    # Contextual Instances
    'player',
    'pet',
    # Utility Functions
    'get_player_by_name',
    # Exceptions
    'ContextNotSetError',
    'ApiError',
    # Advanced Access (Optional)
    # 'get_java_api',
]

# --- Package Level Initialization (Optional) ---
# print("HPET Python library initialized.")
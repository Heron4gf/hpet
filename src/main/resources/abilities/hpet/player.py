# File: hpet/player.py
# Purpose: Defines the Player class providing a Pythonic API over Java methods.

from .exceptions import ApiError

class Player:
    """
    Represents a player, proxying actions to the Java backend via the API object.
    Instances are typically obtained via `hpet.player` or `hpet.get_player_by_name`.
    """
    def __init__(self, player_uuid: str, java_api_proxy):
        """
        Internal constructor. Use hpet.player or hpet.get_player_by_name.
        Args:
            player_uuid (str): The UUID of the player this object represents.
            java_api_proxy: The Py4J proxy to the Java PythonEntryPoint.
        """
        if not player_uuid:
            raise ValueError("Player UUID cannot be empty.")
        self.uuid: str = player_uuid
        self._api = java_api_proxy
        # Cache for player name to avoid repeated lookups (optional)
        self._name_cache = None

    def _call_api(self, method_name, *args):
        """Internal helper to call Java API methods with basic error handling."""
        try:
            method = getattr(self._api, method_name, None)
            if not method:
                 raise ApiError(f"Java API method '{method_name}' not found in PythonEntryPoint.")
            # Pass self.uuid as the first argument for player-specific methods
            return method(self.uuid, *args)
        except Exception as e:
            # Catch Py4J errors or errors from within the Java method
            # Log using the API itself if possible
            log_method = getattr(self._api, 'logWarning', print) # Fallback to print
            log_method(f"API Error calling '{method_name}' for player {self.uuid}: {e}")
            # Re-raise as a specific ApiError or return a default value?
            # Returning None/False might hide errors. Raising is often better.
            raise ApiError(f"Failed to call Java API method '{method_name}'") from e

    # --- Properties ---
    @property
    def name(self) -> str:
        """Gets the player's current name. May involve a Java call."""
        if self._name_cache is None:
            # Example of fetching name - requires getPlayerName(uuid) in EntryPoint
            # Or get the proxy and call getName() on it.
            try:
                player_proxy = self._api.getPlayerByUUID(self.uuid)
                self._name_cache = player_proxy.getName() if player_proxy else f"OfflinePlayer({self.uuid[:8]})"
            except Exception:
                 self._name_cache = f"Player({self.uuid[:8]})" # Fallback
        return self._name_cache

    @property
    def health(self) -> float:
        """Gets the player's current health points."""
        return self._call_api("getPlayerHealth")

    @property
    def max_health(self) -> float:
        """Gets the player's maximum health points."""
        return self._call_api("getPlayerMaxHealth")

    # --- Actions ---
    def heal(self, hearts: float):
        """Heals the player by a number of hearts (1 heart = 2 health points)."""
        return self._call_api("healPlayer", float(hearts) * 2.0)

    def damage(self, hearts: float):
        """Damages the player by a number of hearts (1 heart = 2 damage points)."""
        return self._call_api("damagePlayer", float(hearts) * 2.0)

    def kill(self):
        """Kills the player by setting health to 0. Use with caution."""
        return self._call_api("killPlayer")

    def send_message(self, message: str):
        """Sends a chat message directly to this player."""
        # This Java method doesn't take UUID first in our current EntryPoint design
        # Adjust call or EntryPoint method if needed. Let's assume EntryPoint handles it.
        self._api.sendMessageToPlayer(self.uuid, str(message)) # Direct call

    def set_fly(self, can_fly: bool):
        """Sets whether the player is allowed to fly."""
        return self._call_api("setPlayerFly", bool(can_fly))

    def set_walk_speed(self, speed: float):
        """Sets the player's walk speed (clamped 0.0-1.0)."""
        return self._call_api("setPlayerWalkSpeed", float(speed))

    def apply_potion_effect(self, effect_name: str, duration_seconds: int, amplifier: int = 0):
        """Applies a potion effect. Amplifier 0=Level I, 1=Level II, etc."""
        return self._call_api("applyPotionEffectToPlayer",
                              str(effect_name).upper(), int(duration_seconds), int(amplifier))

    def set_health(self, health_points: float):
         """Sets the player's health (clamped 0 to max_health)."""
         return self._call_api("setPlayerHealth", float(health_points))

    def attacker(self):
        """
        Gets the Player who last attacked this player, if known by the Java context.
        Returns: Player object or None.
        """
        from .context import get_context_data # Get context data for the Java call
        context_data = get_context_data()
        try:
             # Call the specific Java method that requires context
             attacker_player_proxy = self._api.getPlayerAttacker(self.uuid, context_data)
             if attacker_player_proxy:
                 # Return a new Python Player wrapper for the attacker
                 return Player(attacker_player_proxy.getUniqueId().toString(), self._api)
        except Exception as e:
             raise ApiError("Failed to call Java API method 'getPlayerAttacker'") from e
        return None

    def is_online(self) -> bool:
         """Checks if the player is currently online."""
         try:
             player_obj = self._api.getPlayerByUUID(self.uuid)
             return player_obj is not None and player_obj.isOnline()
         except Exception:
             return False # Assume offline on error

    # --- Standard Python Methods ---
    def __eq__(self, other):
        return isinstance(other, Player) and self.uuid == other.uuid

    def __hash__(self):
        return hash(self.uuid)

    def __repr__(self):
        # Try to include name if cached, otherwise just UUID
        name_part = f", name='{self._name_cache}'" if self._name_cache else ""
        return f"Player(uuid='{self.uuid}'{name_part})"
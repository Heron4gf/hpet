# File: main_bridge_client.py
# Purpose: Connects to Java Py4J Gateway, handles execution requests, manages context.

import time
import traceback
import importlib.util
import os
import logging
from py4j.java_gateway import JavaGateway, CallbackServerParameters, GatewayParameters, Py4JNetworkError

# Import the hpet context manager FIRST
try:
    import hpet.context
    import hpet.exceptions
except ImportError as e:
    print(f"FATAL: Failed to import the 'hpet' library package. Ensure it's in the Python path.")
    print(f"Error details: {e}")
    # Optionally, exit or provide more detailed setup instructions.
    # For now, we'll let it potentially fail later if imports don't work.
    pass


# --- Configuration ---
# These could be loaded from a config file or environment variables
GATEWAY_PORT = 25333 # Default Py4J port
GATEWAY_ADDRESS = "127.0.0.1"
CONNECTION_RETRY_DELAY = 15 # Seconds between connection attempts
LOG_LEVEL = logging.INFO # Or logging.DEBUG for more verbose logs

# Setup basic logging
logging.basicConfig(level=LOG_LEVEL, format='%(asctime)s - %(levelname)s - %(message)s')


class PythonScriptExecutor:
    """
    This object is registered with Java. Its methods are called by Java via Py4J
    to trigger the execution of specific Python ability scripts.
    """
    def __init__(self, java_entry_point):
        self._java_api = java_entry_point # Renamed for clarity
        self._log(logging.INFO, "PythonScriptExecutor initialized and connected to Java EntryPoint.")

    def _log(self, level, message, exception=None):
        """Helper to log messages, potentially back to Java too."""
        logging.log(level, message)
        # Optionally send logs back to Java console via entry point
        try:
            if level >= logging.WARNING:
                log_method = getattr(self._java_api, 'logWarning', None)
            else:
                log_method = getattr(self._java_api, 'logInfo', None)

            if log_method:
                full_message = message
                if exception:
                    # Include exception details if provided
                    full_message += f"\nException: {exception}\nTraceback:\n{traceback.format_exc()}"
                log_method(full_message)
        except Exception:
            # Avoid errors during logging itself
            logging.error("Failed to log message back to Java EntryPoint.")

    def execute_script(self, script_path, function_name, context_map):
        """
        Called BY Java. Sets context, loads/runs user script function, clears context.
        Args:
            script_path (str): Absolute path to the target .py script.
            function_name (str): Name of the function within the script to execute.
            context_map (Java Map): Context data from Java (owner UUID, etc.).
        """
        py_context = {}
        try:
             # Convert Java Map safely. Py4J auto_convert helps but explicit dict() is robust.
             py_context = dict(context_map)
        except Exception as convert_error:
             self._log(logging.ERROR, f"Failed to convert context map: {convert_error}", convert_error)
             # Decide: continue with empty context or abort? Abort seems safer.
             return

        self._log(logging.DEBUG, f"Received request: execute '{function_name}' in '{script_path}' with context: {py_context}")

        # --- Set Context for the hpet library ---
        try:
             hpet.context.set_current_context(py_context, self._java_api)
        except Exception as ctx_err:
             self._log(logging.ERROR, f"Failed to set execution context: {ctx_err}", ctx_err)
             return # Cannot proceed without context

        # --- Execute User Script ---
        try:
            if not os.path.exists(script_path):
                self._log(logging.WARNING, f"Script not found: {script_path}")
                return

            # Dynamic module loading
            module_name = f"hpet_ability_scripts.{os.path.splitext(os.path.basename(script_path))[0]}" # More unique name
            spec = importlib.util.spec_from_file_location(module_name, script_path)

            if spec is None or spec.loader is None:
                 self._log(logging.ERROR, f"Cannot create module spec/loader for: {script_path}")
                 return

            module = importlib.util.module_from_spec(spec)
            spec.loader.exec_module(module) # Load the script code as a module
            self._log(logging.DEBUG, f"Module '{module_name}' loaded from '{script_path}'.")

            # Find and call the target function
            if hasattr(module, function_name):
                user_func = getattr(module, function_name)
                self._log(logging.INFO, f"Executing function '{function_name}' in '{script_path}'...")
                user_func() # Call the user's function (no arguments passed directly)
                self._log(logging.INFO, f"Finished executing '{function_name}' in '{script_path}'.")
            else:
                self._log(logging.WARNING, f"Function '{function_name}' not found in {script_path}")

        except hpet.exceptions.ContextNotSetError as cns_err:
             # This specifically catches errors if the user script tries to use hpet.player/pet
             # but the context somehow got cleared prematurely (shouldn't happen with finally).
             self._log(logging.ERROR, f"HPET Context Error during script execution: {cns_err}", cns_err)
        except Exception as exec_err:
            # Catch-all for errors *within* the user's script or loading process
            self._log(logging.ERROR, f"Error during execution of {script_path} function '{function_name}': {exec_err}", exec_err)
            # Optionally raise back to Java if needed for specific handling there
            # from py4j.protocol import Py4JJavaError
            # raise Py4JJavaError(f"Python Error: {e}", traceback.format_exc())
        finally:
            # --- Ensure Context is Cleared ---
            hpet.context.clear_current_context()
            self._log(logging.DEBUG, "Execution context cleared.")

    # Required by Py4J for identifying this class when registering from Java side
    class Java:
        implements = []


# --- Main Connection and Keep-Alive Loop ---
def run_bridge_client():
    """Connects to the Java Gateway and keeps the connection alive."""
    logging.info("Starting Python Bridge Client...")
    gateway = None
    while True:
        gateway = None # Ensure reset before attempt
        try:
            logging.info(f"Attempting connection to Java Gateway ({GATEWAY_ADDRESS}:{GATEWAY_PORT})...")
            gateway = JavaGateway(
                gateway_parameters=GatewayParameters(
                    address=GATEWAY_ADDRESS,
                    port=GATEWAY_PORT,
                    auto_convert=True,
                    auto_close=True # Close socket if Python process exits
                ),
                callback_server_parameters=CallbackServerParameters(
                    address=GATEWAY_ADDRESS,
                    port=0, # Ephemeral port
                    daemonize=True # Run callback server in background thread
                )
            )
            logging.info("Successfully connected to Java Gateway.")

            # --- Registration with Java ---
            java_entry_point = gateway.entry_point
            executor = PythonScriptExecutor(java_entry_point)
            # Call the registration method on the Java side
            java_entry_point.registerPythonExecutionHandler(executor)
            logging.info("PythonScriptExecutor registered with Java EntryPoint.")
            # --- ---

            # --- Keep-Alive Loop ---
            while True:
                try:
                     # Ping JVM via a simple call to check connection health
                     current_time_ms = gateway.jvm.System.currentTimeMillis()
                     logging.debug(f"Gateway connection alive (ping successful: {current_time_ms}).")
                     time.sleep(30) # Check every 30 seconds
                except Py4JNetworkError:
                    logging.warning("Connection to Java Gateway lost. Attempting to reconnect...")
                    break # Exit inner loop to trigger outer loop's reconnect logic
                except Exception as ping_err:
                    logging.error(f"Non-network error during gateway ping check: {ping_err}", ping_err)
                    # Decide whether to break or continue based on error type if needed
                    time.sleep(30) # Still wait before next check

        except (Py4JNetworkError, ConnectionRefusedError) as conn_err:
            logging.error(f"Connection failed: {conn_err}. Ensure Java server/plugin is running.")
        except Exception as setup_err:
             logging.critical(f"Unexpected error during connection/setup: {setup_err}", setup_err)
             # If setup fails critically, maybe stop retrying? For now, keep looping.
        finally:
            if gateway:
                try:
                    gateway.shutdown()
                    logging.info("Py4J Gateway connection shut down.")
                except Exception as shutdown_err:
                    logging.warning(f"Error during gateway shutdown: {shutdown_err}")
            # --- Retry Delay ---
            logging.info(f"Waiting {CONNECTION_RETRY_DELAY} seconds before next connection attempt...")
            time.sleep(CONNECTION_RETRY_DELAY)


if __name__ == "__main__":
    # Ensure the 'hpet' directory is discoverable (e.g., if running from parent dir)
    # Or manage installation via pip if distributing properly.
    run_bridge_client()
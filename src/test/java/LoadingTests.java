import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.MockPlugin;
import be.seeseemelk.mockbukkit.ServerMock;
import it.heron.hpet.main.PetPlugin;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoadingTests {

    private ServerMock server;
    private PetPlugin plugin;

    @BeforeEach
    public void setUp()
    {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(PetPlugin.class);
    }

    @Test
    public void reload() {
        plugin.reload();
    }

    @Test
    public void testDefaultModules() {
        String[] defaultModules = {"Messages", "PetsLoader", "PetsHandler", "teleport", "tridentThrow", "respawn", "changeWorld", "Vanish"};
        assertTrue(checkModules(defaultModules));
        reload();
        assertTrue(checkModules(defaultModules));
    }

    public boolean checkModules(String... moduleNames) {
        boolean ok = true;
        for(String module : moduleNames) {
            if(plugin.getModulesHandler().hasModule(module)) {
                System.out.println("Loaded: "+module);
            } else {
                System.out.println("Not Loaded: "+module);
                ok = false;
            }
        }
        return ok;
    }

    @Test
    public void testPluginModules() {
        String[] defaultModules = {"PlaceholderAPI", "HeadDatabase", "CMI", "ItemsAdder", "Vault"};
        assertFalse(checkModules(defaultModules));
        reload();
        assertFalse(checkModules(defaultModules));
    }


    @AfterEach
    public void tearDown()
    {
        MockBukkit.unmock();
    }

}

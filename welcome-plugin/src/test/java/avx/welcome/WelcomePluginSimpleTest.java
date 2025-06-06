package avx.welcome;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WelcomePlugin Basic Structure Tests")
class WelcomePluginSimpleTest {

    @Test
    @DisplayName("Plugin class should exist and be properly structured")
    void testPluginClassExists() {
        // Test that the WelcomePlugin class exists and can be referenced
        Class<?> pluginClass = WelcomePlugin.class;
        
        assertNotNull(pluginClass, "WelcomePlugin class should exist");
        assertEquals("WelcomePlugin", pluginClass.getSimpleName(), 
                "Plugin should have correct simple name");
        assertEquals("avx.welcome", pluginClass.getPackage().getName(), 
                "Plugin should be in correct package");
    }

    @Test
    @DisplayName("Plugin should extend JavaPlugin")
    void testPluginInheritance() {
        // Verify that the plugin extends JavaPlugin (indirectly through class hierarchy)
        boolean extendsJavaPlugin = false;
        Class<?> currentClass = WelcomePlugin.class;
        
        while (currentClass != null) {
            if (currentClass.getSimpleName().equals("JavaPlugin")) {
                extendsJavaPlugin = true;
                break;
            }
            currentClass = currentClass.getSuperclass();
        }
        
        assertTrue(extendsJavaPlugin, "Plugin should extend JavaPlugin");
    }

    @Test
    @DisplayName("Plugin should implement Listener interface")
    void testListenerImplementation() {
        // Check if the plugin implements the Listener interface
        boolean implementsListener = false;
        Class<?>[] interfaces = WelcomePlugin.class.getInterfaces();
        
        for (Class<?> iface : interfaces) {
            if (iface.getSimpleName().equals("Listener")) {
                implementsListener = true;
                break;
            }
        }
        
        assertTrue(implementsListener, "Plugin should implement Listener interface");
    }

    @Test
    @DisplayName("Plugin should have required methods")
    void testRequiredMethods() {
        // Test that required methods exist (this will compile only if they exist)
        assertDoesNotThrow(() -> {
            // These method calls will only compile if the methods exist
            WelcomePlugin.class.getDeclaredMethod("onEnable");
            WelcomePlugin.class.getDeclaredMethod("onDisable");
            WelcomePlugin.class.getDeclaredMethod("onPlayerJoin", 
                    Class.forName("org.bukkit.event.player.PlayerJoinEvent"));
        }, "Plugin should have all required methods");
    }

    @Test
    @DisplayName("Plugin should have correct package structure")
    void testPackageStructure() {
        String packageName = WelcomePlugin.class.getPackage().getName();
        
        assertTrue(packageName.startsWith("avx"), "Package should start with 'avx'");
        assertTrue(packageName.contains("welcome"), "Package should contain 'welcome'");
        assertEquals("avx.welcome", packageName, "Package should be exactly 'avx.welcome'");
    }

    @Test
    @DisplayName("Plugin class should be public")
    void testClassVisibility() {
        int modifiers = WelcomePlugin.class.getModifiers();
        
        assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), 
                "Plugin class should be public");
        assertFalse(java.lang.reflect.Modifier.isAbstract(modifiers), 
                "Plugin class should not be abstract");
        assertFalse(java.lang.reflect.Modifier.isFinal(modifiers), 
                "Plugin class should not be final");
    }
} 
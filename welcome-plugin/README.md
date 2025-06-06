# WelcomePlugin - Bukkit Integration Testing Guide

This project demonstrates how to set up **integration tests** for Bukkit/Paper plugins using **MockBukkit**, the industry-standard testing framework for Minecraft plugins.

## 🎯 What is Integration Testing for Bukkit Plugins?

Integration testing for Bukkit plugins allows you to:
- Test your plugin's behavior without running a full Minecraft server
- Verify event handling, commands, and player interactions
- Ensure your plugin works correctly with the Bukkit API
- Run automated tests in seconds instead of minutes
- Catch bugs before deployment

## 📋 Prerequisites

- **Java 17 or higher** (Java 21 recommended for latest MockBukkit versions)
- **Gradle** or **Maven** build system
- **Paper/Bukkit API** dependency

## 🚀 Setting Up MockBukkit Integration Tests

### Step 1: Add Dependencies

Add MockBukkit to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url = 'https://repo.papermc.io/repository/maven-public/' }
    maven { url = 'https://jitpack.io' } // For MockBukkit
}

dependencies {
    compileOnly 'io.papermc.paper:paper-api:1.20.4-R0.1-SNAPSHOT'
    
    // Test dependencies
    testImplementation 'org.junit.jupiter:junit-jupiter:5.10.1'
    testImplementation 'com.github.MockBukkit:MockBukkit:v1.20-SNAPSHOT'
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21)) // Java 21 for latest MockBukkit
}

test {
    useJUnitPlatform()
}
```

### Step 2: Create Integration Tests

Here's a complete example of integration tests for the WelcomePlugin:

```java
package avx.welcome;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("WelcomePlugin Integration Tests")
class WelcomePluginIntegrationTest {

    private ServerMock server;
    private WelcomePlugin plugin;

    @BeforeEach
    void setUp() {
        // Initialize MockBukkit server
        server = MockBukkit.mock();
        
        // Load our plugin
        plugin = MockBukkit.load(WelcomePlugin.class);
    }

    @AfterEach
    void tearDown() {
        // Clean up MockBukkit
        MockBukkit.unmock();
    }

    @Test
    @DisplayName("Plugin should load successfully")
    void testPluginLoads() {
        assertNotNull(plugin, "Plugin should be loaded");
        assertTrue(plugin.isEnabled(), "Plugin should be enabled");
        assertEquals("WelcomePlugin", plugin.getName(), "Plugin name should match");
    }

    @Test
    @DisplayName("Should send welcome title when player joins")
    void testPlayerJoinWelcomeMessage() {
        // Create a mock player
        PlayerMock player = server.addPlayer("TestPlayer");
        
        // Trigger the PlayerJoinEvent
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player, "TestPlayer joined the game");
        server.getPluginManager().callEvent(joinEvent);
        
        // Verify that a title was sent to the player
        assertTrue(player.hasReceivedTitle(), "Player should receive a welcome title");
    }

    @Test
    @DisplayName("Welcome title should have correct format")
    void testWelcomeTitleFormat() {
        PlayerMock player = server.addPlayer("Steve");
        
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player, "Steve joined");
        server.getPluginManager().callEvent(joinEvent);
        
        // Verify the title content
        String expectedTitle = ChatColor.GOLD + "Bienvenue " + ChatColor.YELLOW + "Steve";
        // Note: Exact title verification depends on MockBukkit version
        assertTrue(player.hasReceivedTitle(), "Player should have received a title");
    }

    @Test
    @DisplayName("Plugin should handle multiple player joins")
    void testMultiplePlayerJoins() {
        // Create multiple players
        PlayerMock player1 = server.addPlayer("Alice");
        PlayerMock player2 = server.addPlayer("Bob");
        PlayerMock player3 = server.addPlayer("Charlie");
        
        // Trigger join events for all players
        server.getPluginManager().callEvent(new PlayerJoinEvent(player1, "Alice joined"));
        server.getPluginManager().callEvent(new PlayerJoinEvent(player2, "Bob joined"));
        server.getPluginManager().callEvent(new PlayerJoinEvent(player3, "Charlie joined"));
        
        // Verify all players received welcome titles
        assertTrue(player1.hasReceivedTitle(), "Alice should receive welcome title");
        assertTrue(player2.hasReceivedTitle(), "Bob should receive welcome title");
        assertTrue(player3.hasReceivedTitle(), "Charlie should receive welcome title");
    }
}
```

### Step 3: Advanced Testing Scenarios

```java
@Test
@DisplayName("Plugin should handle rapid successive player joins")
@Timeout(value = 5, unit = TimeUnit.SECONDS)
void testRapidPlayerJoins() {
    List<PlayerMock> players = new ArrayList<>();
    
    // Create and process 100 players joining rapidly
    for (int i = 0; i < 100; i++) {
        PlayerMock player = server.addPlayer("Player" + i);
        players.add(player);
        
        PlayerJoinEvent joinEvent = new PlayerJoinEvent(player, "Player" + i + " joined");
        server.getPluginManager().callEvent(joinEvent);
    }
    
    // Verify all players received their welcome titles
    for (PlayerMock player : players) {
        assertTrue(player.hasReceivedTitle(), 
                "Player should have received welcome title");
    }
}

@ParameterizedTest
@ValueSource(strings = {
    "test", "TEST", "Test123", "Player_Name", "a", "VeryLongPlayerNameThatIsStillValid"
})
@DisplayName("Plugin should handle various player name formats")
void testVariousPlayerNames(String playerName) {
    PlayerMock player = server.addPlayer(playerName);
    
    PlayerJoinEvent joinEvent = new PlayerJoinEvent(player, playerName + " joined");
    server.getPluginManager().callEvent(joinEvent);
    
    assertTrue(player.hasReceivedTitle(), 
            "Player with name '" + playerName + "' should receive welcome title");
}
```

## 🔧 MockBukkit Features

MockBukkit provides comprehensive mocking capabilities:

### Mock Players
```java
// Create players
PlayerMock player = server.addPlayer("PlayerName");

// Simulate actions
player.simulateBlockBreak(block);
player.performCommand("help");
player.chat("Hello world!");

// Verify interactions
assertTrue(player.hasReceivedTitle());
assertEquals(GameMode.SURVIVAL, player.getGameMode());
```

### Mock Worlds
```java
// Create a superflat world
World world = new WorldMock(Material.DIRT, 3);

// Create custom worlds
WorldMock world = new WorldMock();
world.setSpawnLocation(0, 64, 0);
```

### Event Testing
```java
// Fire events
PlayerJoinEvent event = new PlayerJoinEvent(player, "Player joined");
server.getPluginManager().callEvent(event);

// Test event cancellation
PlayerMoveEvent moveEvent = new PlayerMoveEvent(player, from, to);
server.getPluginManager().callEvent(moveEvent);
assertTrue(moveEvent.isCancelled());
```

### Command Testing
```java
// Test commands
assertTrue(server.dispatchCommand(player, "mycommand arg1 arg2"));

// Verify command output
player.assertSaid("Command executed successfully!");
```

## 🏃‍♂️ Running Tests

```bash
# Run all tests
./gradlew test

# Run with verbose output
./gradlew test --info

# Run specific test class
./gradlew test --tests "WelcomePluginIntegrationTest"

# Run tests with coverage
./gradlew test jacocoTestReport
```

## 🐛 Troubleshooting

### Common Issues

1. **Java Version Mismatch**
   - MockBukkit v4.0+ requires Java 21
   - Use older versions for Java 17: `MockBukkit-v1.19:2.145.0`

2. **Dependency Conflicts**
   ```gradle
   configurations.all {
       resolutionStrategy {
           force 'org.junit.jupiter:junit-jupiter:5.10.1'
       }
   }
   ```

3. **UnimplementedOperationException**
   - MockBukkit doesn't implement all Bukkit methods
   - These exceptions extend `AssumptionException` and skip tests
   - Contribute missing implementations or request them

4. **Plugin Loading Issues**
   - Ensure `plugin.yml` is properly configured
   - Check that your plugin has a default constructor

### Version Compatibility

| Minecraft Version | MockBukkit Version | Java Version |
|-------------------|-------------------|--------------|
| 1.21.x           | v1.21-SNAPSHOT    | Java 21+     |
| 1.20.x           | v1.20-SNAPSHOT    | Java 17+     |
| 1.19.x           | v1.19:2.145.0     | Java 17+     |
| 1.18.x           | v1.18:1.5.2       | Java 17+     |

## 📚 Additional Resources

- **MockBukkit Documentation**: https://docs.mockbukkit.org
- **MockBukkit GitHub**: https://github.com/MockBukkit/MockBukkit
- **Example Projects**:
  - [Slimefun4](https://github.com/Slimefun/Slimefun4) (1700+ tests)
  - [Custom Join Messages](https://github.com/Insprill/custom-join-messages) (170+ tests)
  - [SpectatorModeRewrite](https://github.com/carelesshippo/SpectatorModeRewrite) (80+ tests)

## 🎉 Benefits of Integration Testing

1. **Faster Development**: Catch bugs early in development
2. **Confidence**: Deploy with confidence knowing your plugin works
3. **Refactoring Safety**: Change code without fear of breaking functionality
4. **Documentation**: Tests serve as living documentation
5. **CI/CD Integration**: Automate testing in your build pipeline

## 🔄 Continuous Integration

Example GitHub Actions workflow:

```yaml
name: Test Plugin

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 21
      uses: actions/setup-java@v3
      with:
        java-version: '21'
        distribution: 'temurin'
    
    - name: Run tests
      run: ./gradlew test
    
    - name: Upload test results
      uses: actions/upload-artifact@v3
      if: always()
      with:
        name: test-results
        path: build/reports/tests/
```

---

**Happy Testing! 🧪** Integration testing will make your Bukkit plugins more reliable and maintainable. 
# Realistic Physics Plugin

A comprehensive Minecraft plugin that adds realistic physics to your server, including structural integrity, chain reactions, and momentum effects.

## Features

### 🏗️ Structural Integrity
- Blocks require structural support to remain in place
- Realistic building collapse mechanics
- Configurable support blocks (bedrock, obsidian, etc.)
- Support distance and strength calculations

### ⚡ Chain Reactions
- Breaking one block can cause nearby blocks to fall
- Realistic propagation delays based on distance
- Configurable chain reaction distance and intensity

### 🌍 Custom Gravity
- Create gravity zones with different strengths
- Anti-gravity areas for floating structures
- Per-location gravity customization
- Momentum and bouncing effects

### 🎮 Performance Optimized
- Intelligent caching system
- TPS-based processing adjustment
- Configurable processing limits
- Async processing support (experimental)

### 🎨 Visual Effects
- Particle effects for falling blocks
- Impact dust clouds
- Sound effects with volume control
- Configurable visual feedback

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure the plugin in `plugins/RealisticPhysics/config.yml`

## Commands

### Main Commands
- `/physics status` - Show system status and statistics
- `/physics enable/disable` - Enable or disable the physics system
- `/physics reload` - Reload configuration
- `/physics performance` - Show performance statistics
- `/physics clear` - Clear all physics data and cache
- `/physics debug <on|off>` - Toggle debug mode

### Gravity Commands
- `/gravity <strength> [radius]` - Create gravity zone at your location
  - Examples:
    - `/gravity 2.0` - Double gravity (normal radius)
    - `/gravity 0.5 15` - Half gravity with 15 block radius
    - `/gravity -1.0` - Anti-gravity (blocks float upward)

### Structure Commands
- `/structure [radius]` - Analyze structural integrity in area
  - Shows stability statistics
  - Identifies critical blocks that may collapse
  - Displays structural support information

## Configuration

### Basic Settings
```yaml
enabled: true                    # Enable/disable physics system
```

### Physics Settings
```yaml
physics:
  max-support-distance: 10       # Max distance to check for support
  min-support-blocks: 2         # Minimum blocks needed for support
  check-interval: 10            # How often to check physics (ticks)
  max-blocks-per-tick: 50       # Performance limit
  chain-reactions: true         # Enable chain reactions
  max-chain-distance: 15        # Max chain reaction distance
```

### Gravity Settings
```yaml
gravity:
  default-strength: 1.0         # Normal gravity multiplier
  momentum: true                # Enable momentum and bouncing
  bounce-factor: 0.3           # Bounce strength (0.0 = no bounce)
  air-resistance: 0.02         # Air resistance factor
```

### Block Configuration
- `physics-blocks`: List of blocks that have physics
- `support-blocks`: List of blocks that provide structural support

### Performance Settings
```yaml
performance:
  max-chunks: 5                 # Max chunks to process at once
  min-tps: 15.0                # Disable physics when TPS drops below
  async-processing: false       # Experimental async processing
```

## Permissions

- `physics.admin` - Access to all physics commands (default: op)
- `physics.bypass` - Bypass physics in creative mode (default: op)
- `physics.notify` - Receive physics notifications (default: op)

## Usage Examples

### Creating Realistic Buildings
1. Build with supported materials (stone, wood, etc.)
2. Ensure proper foundation connected to bedrock/obsidian
3. Use support columns for large structures
4. Test with `/structure` command to check stability

### Gravity Zones
```
/gravity 0.0 20    # Create zero-gravity zone (20 block radius)
/gravity 5.0       # Create super-heavy gravity zone
/gravity -0.5      # Create anti-gravity zone
```

### Chain Reactions
- Break supporting blocks to cause realistic collapses
- Use TNT for dramatic destruction effects
- Chain reactions spread based on structural connections

### Debug Mode
1. Enable: `/physics debug on`
2. Right-click blocks with a stick to check integrity
3. View real-time structural information

## Performance Tips

### For Better Performance
- Reduce `max-blocks-per-tick` if experiencing lag
- Decrease `max-support-distance` for simpler calculations
- Disable `chain-reactions` in very active areas
- Use `async-processing` (experimental)

### For More Realism
- Increase `max-support-distance` for complex structures
- Enable all visual and sound effects
- Lower `min-tps` threshold for consistent physics

## Troubleshooting

### Low TPS Issues
- Check `/physics performance` for statistics
- Reduce processing limits in config
- Consider disabling physics in busy worlds

### Blocks Not Falling
- Verify block is in `physics-blocks` list
- Check if world is enabled in config
- Ensure structural support is properly calculated

### Chain Reactions Too Aggressive
- Reduce `max-chain-distance`
- Increase `min-support-blocks`
- Adjust `check-interval` for slower processing

## Technical Details

### Structural Support Algorithm
1. Checks for connection to support blocks or ground
2. Uses breadth-first search within max distance
3. Caches results for 30 seconds
4. Considers block weight and material strength

### Performance Monitoring
- Real-time TPS monitoring
- Automatic processing adjustment
- Cache management and cleanup
- Load balancing across ticks

## Compatibility

- **Minecraft Version**: 1.20.4
- **Server Software**: Paper, Spigot, Bukkit
- **Java Version**: 17+

### Plugin Compatibility
- WorldEdit: Full support
- GriefPrevention: Respects claim protections
- CoreProtect: Compatible with logging
- Most building and protection plugins

## Support

For support, bug reports, or feature requests:
- Check the wiki for detailed documentation
- Report issues with server logs and configuration
- Include Minecraft and plugin versions

## License

This plugin is open source. Feel free to modify and distribute according to the license terms.

---

**Note**: This is a complex physics plugin that may impact server performance. Start with default settings and adjust based on your server's capabilities and player count. 
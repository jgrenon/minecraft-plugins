# Realistic Physics Plugin - Installation & Quick Start

## ✅ Installation Complete!

Your Realistic Physics Plugin has been successfully built and installed in your server's plugins directory.

## 🚀 Quick Start

### 1. Start Your Server
```bash
cd ../server
./start.sh
```

### 2. First-Time Setup
1. The plugin will create a config file at `plugins/RealisticPhysics/config.yml`
2. Check the console for "Realistic Physics Plugin enabled!" message
3. Join your server as an operator

### 3. Test the Plugin
Once in-game, try these commands:

```
/physics status          # Check if plugin is running
/physics debug on        # Enable debug mode
/structure 10           # Analyze nearby structures
```

### 4. Basic Testing
1. **Build a simple structure** with stone blocks
2. **Remove the bottom block** - the structure should collapse realistically
3. **Try gravity zones**: `/gravity 2.0` (double gravity)
4. **Test chain reactions** by breaking support blocks

## 🎮 Key Features to Test

### Structural Integrity
- Build towers and remove bottom blocks
- Create bridges and remove center supports
- Build floating structures (should fall)

### Chain Reactions
- Create complex structures
- Use TNT to trigger large collapses
- Watch how destruction spreads realistically

### Gravity Zones
```
/gravity 0.5 15    # Weak gravity zone
/gravity 2.0       # Strong gravity
/gravity -1.0      # Anti-gravity (floating!)
```

### Debug Tools
- Enable debug: `/physics debug on`
- Right-click blocks with a stick to check structural integrity
- Use `/structure [radius]` to analyze areas

## 📊 Performance Monitoring

Check server performance:
```
/physics performance    # Show TPS and processing stats
/physics status        # Overall system status
```

## ⚙️ Configuration

Edit `plugins/RealisticPhysics/config.yml` to customize:
- Which blocks have physics
- Gravity strength and effects
- Performance limits
- Visual effects

After editing config, reload with:
```
/physics reload
```

## 🛠️ Troubleshooting

### Plugin Not Loading
- Check console for error messages
- Ensure Java 17+ is installed
- Verify Paper/Spigot server

### Performance Issues
- Use `/physics performance` to check TPS
- Reduce `max-blocks-per-tick` in config
- Disable chain reactions in busy areas

### Blocks Not Falling
- Check if blocks are in `physics-blocks` list
- Verify world is enabled in config
- Use debug mode to check structural support

## 🎯 Recommended First Steps

1. **Start small**: Test with simple stone structures
2. **Enable debug mode**: `/physics debug on`
3. **Monitor performance**: `/physics performance`
4. **Adjust settings**: Edit config.yml as needed
5. **Create gravity zones**: Experiment with different strengths

## 📚 Advanced Usage

- Create floating islands with anti-gravity zones
- Build realistic castles that collapse properly
- Design puzzle maps using structural integrity
- Create movie-like destruction sequences

## 🆘 Need Help?

- Check the README.md for detailed documentation
- Use `/physics` for command help
- Monitor console logs for errors
- Test in creative mode first

---

**Have fun with realistic physics! 🚀**

Remember: This plugin can be performance-intensive. Start with default settings and adjust based on your server's capabilities. 
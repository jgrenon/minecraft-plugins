# AI Builder Plugin

An intelligent Minecraft plugin that interprets natural language prompts to automatically build structures in your world.

## Features

- **🤖 OpenAI Integration**: Real AI conversation API for sophisticated structure interpretation
- **🧠 Dual AI Systems**: OpenAI for advanced requests, keyword fallback for reliability
- **📝 Natural Language Processing**: Describe what you want to build in plain English
- **🏗️ Multiple Structure Types**: Houses, castles, towers, bridges, pyramids, domes, and more
- **🧱 Material Recognition**: Specify materials like stone, wood, brick, glass, etc.
- **📏 Size Control**: Use descriptive sizes (small, large, huge) or exact dimensions
- **🎨 Style Support**: Medieval, modern, rustic, fantasy, and other architectural styles
- **⚡ Progressive Building**: Watch your structures build block by block with real-time progress
- **🔐 Permission System**: Control who can build what with granular permissions
- **📊 Build Management**: Cancel, list, and monitor active builds with detailed status

## Commands

### Building Commands
- `/build <description>` - Build a structure from a natural language description
- `/construct <description>` - Alias for `/build`
- `/create <description>` - Alias for `/build`
- `/make <description>` - Alias for `/build`

### Management Commands
- `/ai help` - Show help and examples
- `/ai status` - Show plugin status and active builds
- `/ai list` - List all active builds
- `/ai cancel [player]` - Cancel your build or another player's build (admin)
- `/ai structures` - List all available structure types
- `/ai reload` - Reload plugin configuration (admin only)

## Usage Examples

### Basic Structures
```
/build a house
/build a small wooden house
/build a large stone castle
/build a tall tower
/build a bridge
```

### With Materials
```
/build a brick house
/build a glass dome
/build a cobblestone wall
/build an iron tower
/build a quartz pyramid
```

### With Sizes
```
/build a tiny house
/build a huge castle
/build a 15x15 tower
/build a 20x10 bridge
/build a massive pyramid
```

### With Styles
```
/build a medieval castle
/build a modern house
/build a rustic wooden cabin
/build a fantasy tower
/build a gothic cathedral
```

### Complex Examples
```
/build a large medieval stone castle with towers
/build a small rustic wooden house with a garden
/build a 25x25 glass dome
/build a beautiful brick bridge over water
/build a massive sandstone pyramid
/build an ornate gothic cathedral with stained glass windows
/build a futuristic space station made of iron and glass
/build a cozy hobbit hole with a round door and flower garden
```

## Structure Types

- **House** - Residential buildings with doors, windows, and roofs
- **Castle** - Large fortified structures with walls and towers
- **Tower** - Tall vertical structures with multiple floors
- **Bridge** - Connecting structures spanning gaps
- **Wall** - Defensive barriers and boundaries
- **Pyramid** - Stepped triangular structures
- **Dome** - Rounded vault structures
- **Tree** - Organic tree structures with trunks and leaves
- **Garden** - Landscaped areas with grass and flowers
- **Road** - Paths for transportation

## Permissions

- `aibuilder.build` - Allow use of build commands (default: true)
- `aibuilder.admin` - Allow use of admin commands (default: op)
- `aibuilder.bypass` - Bypass cooldowns and restrictions (default: op)
- `aibuilder.unlimited` - No size or complexity limits (default: op)

## Configuration

The plugin can be configured via `config.yml`:

```yaml
build:
  max-size: 50          # Maximum structure size
  cooldown: 30          # Cooldown between builds (seconds)
  enable-limits: true   # Enable size and cooldown limits
  max-concurrent: 3     # Max concurrent builds on server

messages:
  progress: true        # Show build progress messages
  show-interpretation: true  # Show AI interpretation

ai:
  advanced-recognition: true  # Enable advanced structure recognition
  default-size: 8            # Default size when not specified
  enable-styles: true        # Enable style interpretation

performance:
  blocks-per-tick: 5    # Blocks placed per tick
  max-blocks: 10000     # Maximum blocks per structure

# OpenAI Integration (for advanced AI building)
openai:
  api-key: ""           # Your OpenAI API key
  model: "gpt-4o-mini"  # OpenAI model to use
  enabled: true         # Enable OpenAI integration
```

## Installation

1. Download the plugin JAR file
2. Place it in your server's `plugins` folder
3. Restart your server
4. Configure permissions as needed
5. **Optional**: Add your OpenAI API key to `config.yml` for advanced AI features
6. Start building with AI!

### OpenAI Setup (Optional but Recommended)

For the most sophisticated AI building experience:

1. Get an OpenAI API key from [OpenAI Platform](https://platform.openai.com/api-keys)
2. Add your API key to the `config.yml` file:
   ```yaml
   openai:
     api-key: "your-api-key-here"
     model: "gpt-4o-mini"
     enabled: true
   ```
3. Restart your server
4. Enjoy incredibly sophisticated structure generation!

**Note**: Without OpenAI, the plugin uses intelligent keyword matching which still works great for most builds.

## How It Works

The AI Builder plugin uses a sophisticated dual-AI system:

### 🤖 OpenAI Mode (When API key is configured)
1. **AI Conversation**: Sends your prompt to OpenAI's GPT model
2. **Structured Analysis**: AI analyzes your request and generates a detailed build recipe
3. **Advanced Generation**: Creates complex structures with multiple components
4. **Smart Details**: Adds architectural features, decorations, and realistic proportions
5. **Progressive Building**: Constructs the structure layer by layer

### 🔧 Keyword Mode (Fallback system)
1. **Structure Detection**: Identifies what type of structure you want to build
2. **Material Recognition**: Determines the primary building material
3. **Size Calculation**: Interprets size descriptors or exact dimensions
4. **Style Application**: Applies architectural style modifiers
5. **Generation**: Creates a build plan using pre-defined generators
6. **Construction**: Places blocks progressively for a smooth building experience

The plugin automatically falls back to keyword mode if OpenAI is unavailable, ensuring reliable building regardless of your setup.

## Tips for Better Results

- Be specific about what you want: "large stone castle" vs "castle"
- Include materials: "wooden house" vs "house"
- Specify size when important: "10x10 tower" vs "tower"
- Use style keywords: "medieval", "modern", "rustic", etc.
- Combine descriptors: "beautiful large medieval stone castle"

## Troubleshooting

**Build not starting?**
- Check you have the `aibuilder.build` permission
- Make sure you don't have another build in progress
- Verify the area is clear and you have space

**Structure too small/large?**
- Use size keywords: tiny, small, medium, large, huge
- Specify exact dimensions: "15x15 house"
- Check the max-size configuration setting

**Wrong material?**
- Be explicit about materials: "stone house" not just "house"
- Use common material names: wood, stone, brick, glass, iron

## Support

For issues, suggestions, or contributions, please visit our GitHub repository or contact the development team.

## Version History

- **1.0** - Initial release with core AI building functionality 
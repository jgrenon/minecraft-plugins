package avx.aibuilder.data;

import java.util.List;

public class BuildRecipe {
    private String structureType;
    private String name;
    private String description;
    private Dimensions dimensions;
    private String primaryMaterial;
    private String accentMaterial;
    private String roofMaterial;
    private String style;
    private int complexity;
    private List<String> features;
    private List<Component> components;
    private List<DecorativeElement> decorativeElements;
    
    // Constructors
    public BuildRecipe() {}
    
    // Getters and Setters
    public String getStructureType() {
        return structureType;
    }
    
    public void setStructureType(String structureType) {
        this.structureType = structureType;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Dimensions getDimensions() {
        return dimensions;
    }
    
    public void setDimensions(Dimensions dimensions) {
        this.dimensions = dimensions;
    }
    
    public String getPrimaryMaterial() {
        return primaryMaterial;
    }
    
    public void setPrimaryMaterial(String primaryMaterial) {
        this.primaryMaterial = primaryMaterial;
    }
    
    public String getAccentMaterial() {
        return accentMaterial;
    }
    
    public void setAccentMaterial(String accentMaterial) {
        this.accentMaterial = accentMaterial;
    }
    
    public String getRoofMaterial() {
        return roofMaterial;
    }
    
    public void setRoofMaterial(String roofMaterial) {
        this.roofMaterial = roofMaterial;
    }
    
    public String getStyle() {
        return style;
    }
    
    public void setStyle(String style) {
        this.style = style;
    }
    
    public int getComplexity() {
        return complexity;
    }
    
    public void setComplexity(int complexity) {
        this.complexity = complexity;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public void setFeatures(List<String> features) {
        this.features = features;
    }
    
    public List<Component> getComponents() {
        return components;
    }
    
    public void setComponents(List<Component> components) {
        this.components = components;
    }
    
    public List<DecorativeElement> getDecorativeElements() {
        return decorativeElements;
    }
    
    public void setDecorativeElements(List<DecorativeElement> decorativeElements) {
        this.decorativeElements = decorativeElements;
    }
    
    // Nested classes
    public static class Dimensions {
        private int width;
        private int length;
        private int height;
        
        public Dimensions() {}
        
        public Dimensions(int width, int length, int height) {
            this.width = width;
            this.length = length;
            this.height = height;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getLength() {
            return length;
        }
        
        public void setLength(int length) {
            this.length = length;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        @Override
        public String toString() {
            return width + "x" + length + "x" + height;
        }
    }
    
    public static class Component {
        private String name;
        private String material;
        private String pattern;
        private int priority;
        
        public Component() {}
        
        public Component(String name, String material, String pattern, int priority) {
            this.name = name;
            this.material = material;
            this.pattern = pattern;
            this.priority = priority;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getMaterial() {
            return material;
        }
        
        public void setMaterial(String material) {
            this.material = material;
        }
        
        public String getPattern() {
            return pattern;
        }
        
        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
        
        public int getPriority() {
            return priority;
        }
        
        public void setPriority(int priority) {
            this.priority = priority;
        }
    }
    
    public static class DecorativeElement {
        private String type;
        private String material;
        private String placement;
        
        public DecorativeElement() {}
        
        public DecorativeElement(String type, String material, String placement) {
            this.type = type;
            this.material = material;
            this.placement = placement;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getMaterial() {
            return material;
        }
        
        public void setMaterial(String material) {
            this.material = material;
        }
        
        public String getPlacement() {
            return placement;
        }
        
        public void setPlacement(String placement) {
            this.placement = placement;
        }
    }
    
    @Override
    public String toString() {
        return String.format("%s: %s (%s)", name, description, dimensions);
    }
} 
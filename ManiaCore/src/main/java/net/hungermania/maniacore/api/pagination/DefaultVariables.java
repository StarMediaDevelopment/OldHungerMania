package net.hungermania.maniacore.api.pagination;

public enum DefaultVariables {
    
    TYPE("<type>"), COMMAND("<command>");
    
    private final String value;
    
    DefaultVariables(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
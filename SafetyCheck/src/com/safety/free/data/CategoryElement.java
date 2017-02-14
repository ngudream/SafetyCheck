package com.safety.free.data;

public class CategoryElement extends Element {

    private String assess;

    public CategoryElement() {
    }
    
    public CategoryElement(String name, boolean isShow){
        super(name, isShow);
    }

    public String getAssess() {
        return assess;
    }

    public void setAssess(String assess) {
        this.assess = assess;
    }
}

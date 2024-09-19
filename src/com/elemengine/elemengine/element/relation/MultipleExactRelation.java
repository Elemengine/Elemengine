package com.elemengine.elemengine.element.relation;

import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.ElementHolder;

public final class MultipleExactRelation implements ElementRelation {
    
    private final String folderName;
    private final Element[] elements;
    
    MultipleExactRelation(String folderName, Element[] elements) {
        this.folderName = folderName;
        this.elements = elements;
    }
    
    public Element[] elements() {
        return elements;
    }

    @Override
    public String folderName() {
        return folderName;
    }
    
    @Override
    public boolean includes(Element element) {
        for (Element e : elements) {
            if (element == e) return true;
        }

        return false;
    }

    @Override
    public boolean check(ElementHolder holder) {
        for (Element element : elements) {
            if (!holder.hasElement(element)) return false;
        }

        return true;
    }

    @Override
    public int size() {
        return elements.length;
    }
}

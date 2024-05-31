package com.elemengine.elemengine.element.relation;

import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.ElementHolder;

public final class SingleRelation implements ElementRelation {
    
    private final Element element;
    
    SingleRelation(Element element) {
        this.element = element;
    }
    
    public Element element() {
        return element;
    }
    
    @Override
    public boolean includes(Element element) {
        return this.element == element;
    }

    @Override
    public boolean check(ElementHolder holder) {
        return holder.hasElement(element);
    }

    @Override
    public String folderName() {
        return element.getFolderName();
    }
}

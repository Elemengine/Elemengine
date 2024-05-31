package com.elemengine.elemengine.element.relation;

import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.ElementHolder;

public sealed interface ElementRelation permits MultipleAnyRelation, MultipleExactRelation, SingleRelation {

    boolean includes(Element element);
    boolean check(ElementHolder holder);
    String folderName();
    
    static ElementRelation single(Element element) {
        return new SingleRelation(element);
    }
    
    static ElementRelation exact(String folderName, Element...elements) {
        return new MultipleExactRelation(folderName, elements);
    }
    
    static ElementRelation any(String folderName, Element...elements) {
        return new MultipleAnyRelation(folderName, elements);
    }
}

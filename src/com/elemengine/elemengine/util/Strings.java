package com.elemengine.elemengine.util;

import java.util.function.Consumer;

public final class Strings {
    
    private Strings() {}
    
    public static void wrapAnd(String base, int minChars, Consumer<String> forLine) {
        if (base.length() <= minChars) {
            forLine.accept(base);
            return;
        }
        
        String line = "";
        
        int last = 0;
        while (last < base.length()) {
            int end = last + minChars;
            if (end >= base.length()) {
                forLine.accept(base.substring(last, base.length()));
                return;
            }
            
            line += base.subSequence(last, end);
            
            char c = base.charAt(end);
            
            while (!Character.isSpaceChar(c) && end < base.length() - 1) {
                line += c;
                c = base.charAt(++end);
            }
            
            line += base.charAt(end);
            
            last = end + 1;
            forLine.accept(line);
            line = "";
        }
    }
}

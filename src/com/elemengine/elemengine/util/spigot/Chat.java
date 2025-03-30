package com.elemengine.elemengine.util.spigot;

import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.function.Function;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public final class Chat {
    
    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacy('&');

    private Chat() {}
    
    public static Component format(String str, Map<String, Component> tags) {
        if (tags == null || tags.isEmpty()) {
            return fromLegacy(str);
        }
        
        PriorityQueue<FoundTag> queue = new PriorityQueue<>((a, b) -> a.index() - b.index());
        
        for (Entry<String, Component> entry : tags.entrySet()) {
            int index = str.indexOf(entry.getKey());
            if (index < 0) {
                continue;
            }
            
            queue.add(new FoundTag(index, entry.getKey(), entry.getValue()));
        }
        
        if (queue.isEmpty()) {
            return fromLegacy(str);
        }
        
        TextComponent.Builder bldr = Component.text();
        int last = 0;
        while (!queue.isEmpty()) {
            FoundTag next = queue.poll();
            
            bldr.append(fromLegacy(str.substring(last, next.index)));
            bldr.append(next.replace);
            
            last = next.index + next.tag.length();
        }
        
        bldr.append(fromLegacy(str.substring(last)));
        
        return bldr.asComponent();
    }
    
    public static Component fromLegacy(String str) {
        return LEGACY.deserialize(str);
    }
    
    public static String toLegacy(Component comp) {
        return LEGACY.serialize(comp);
    }
    
    public static Component combine(Component comp, Function<String, String> combiner) {
        return fromLegacy(combiner.apply(LEGACY.serialize(comp)));
    }
    
    private static record FoundTag(int index, String tag, Component replace) {
        
    }
}

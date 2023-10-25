package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.skill.Skill;

public class TabCompleteList {
    
    public static List<String> abilities() {
        return Abilities.manager().registered().stream().map(a -> a.getName()).collect(Collectors.toList());
    }

    public static List<String> bindables(AbilityUser user) {
        return Abilities.manager().getUserBindables(user).stream().map((a) -> a.getName()).collect(Collectors.toList());
    }

    public static List<String> slots() {
        return new ArrayList<>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));
    }

    public static List<String> onlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map((p) -> p.getName()).collect(Collectors.toList());
    }

    public static List<String> skills(boolean parentOnly) {
        return Skill.streamValues().filter((s) -> !parentOnly || !s.getChildren().isEmpty()).map((s) -> s.toString().toLowerCase()).collect(Collectors.toList());
    }
}

package com.elementalplugin.elemental.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.elementalplugin.elemental.ability.Abilities;
import com.elementalplugin.elemental.ability.AbilityUser;
import com.elementalplugin.elemental.skill.Skills;

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
        return Skills.manager().registered().stream().filter((s) -> !parentOnly || !s.getChildren().isEmpty()).map((s) -> s.getName()).collect(Collectors.toList());
    }
}

package com.elementalplugin.elemental.command;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.command.CommandSender;

import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.ability.Abilities;
import com.elementalplugin.elemental.ability.AbilityInfo;
import com.elementalplugin.elemental.ability.Bindable;
import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.skill.Skills;
import com.elementalplugin.elemental.storage.Config;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AbilitiesCommand extends SubCommand {

    public AbilitiesCommand() {
        super("abilities", "Show the abilities for a skill", "/pk abilities <skill>", Arrays.asList("abils", "display", "list", "catalog"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Too many args given: " + getUsage());
            return;
        }

        Skill skill = Manager.of(Skills.class).get(args[0]);

        if (skill == null) {
            sender.sendMessage(ChatColor.RED + "No skill found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        }

        Set<AbilityInfo> abils = Manager.of(Abilities.class).fromSkill(skill);

        if (abils.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No " + skill.getColoredName() + ChatColor.RED + " abilities were found!");
            return;
        }

        ComponentBuilder builder = new ComponentBuilder(skill.getName().toUpperCase()).color(skill.getColor()).bold(true);

        for (AbilityInfo ability : abils) {
            if (sender.hasPermission("elemental.ability." + ability.getName())) {
                builder.append("\n" + ability.getName(), FormatRetention.NONE).color(ability.getDisplayColor()).event(new HoverEvent(Action.SHOW_TEXT, new Text(ability.getDescription())));

                if (ability instanceof Bindable) {
                    builder.append(" ", FormatRetention.NONE).append("[bind]", FormatRetention.NONE).event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to bind this ability to your current slot")))
                            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemental bind " + ability.getName()));
                }
            }
        }

        sender.spigot().sendMessage(builder.create());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.skills(false);
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}
}

package com.elementalplugin.elemental.skill;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.Particle.DustTransition;

import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.effect.ParticleData;
import com.elementalplugin.elemental.storage.Config;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

public class Skills extends Manager {

    private Map<String, Skill> cache = new HashMap<>();

    @Override
    protected int priority() {
        return 10;
    }

    @Override
    protected boolean active() {
        return false;
    }

    @Override
    protected void startup() {
        Skill air = this.register(new Skill("airbending", "#f7e32e", "control wind", Material.STRING, new ParticleData(Particle.REDSTONE).amount(15).data(new DustOptions(Color.TEAL, 0.6f))));
        Skill earth = this.register(new Skill("earthbending", "#0cb153", "control earth", Material.GRASS_BLOCK, null));
        Skill fire = this.register(new Skill("firebending", "#f42a10", "control flames", Material.BLAZE_POWDER, new ParticleData(Particle.FLAME).amount(15).extra(0.012)));
        Skill water = this.register(new Skill("waterbending", "#0074D9", "control water", Material.WATER_BUCKET, null));

        this.register(new Skill("energybending", "#6b1fed", "control chi", Material.POPPED_CHORUS_FRUIT, new ParticleData(Particle.SPELL_WITCH)));

        // air specializations
        this.register(new Skill("flying", "#f8e5c4", "fly through the air", Material.ELYTRA, null), air);
        this.register(new Skill("spiritprojecting", "#fff0c2", "astral projection", Material.VEX_SPAWN_EGG, null), air);

        // earth specializations
        this.register(new Skill("lavabending", "#d84d12", "control lava", Material.LAVA_BUCKET, null), earth);
        this.register(new Skill("metalbending", "#c6c7c9", "control metal", Material.IRON_INGOT, null), earth);
        this.register(new Skill("sandbending", "#bcac7c", "control sand", Material.SAND, null), earth);

        // fire specializations
        this.register(new Skill("combustionbending", "dark_red", "sparky boom boom", Material.TNT, null), fire);
        this.register(new Skill("lightningbending", "aqua", "control lightning", Material.TWISTING_VINES, new ParticleData(Particle.REDSTONE).amount(15).data(new DustTransition(Color.fromRGB(126, 252, 242), Color.fromRGB(3, 138, 132), 0.9f))), fire);
        this.register(new Skill("bluefire", "blue", "flames are blue", Material.SOUL_LANTERN, new ParticleData(Particle.SOUL_FIRE_FLAME).amount(15).extra(0.012)), fire);

        // water specializations
        this.register(new Skill("healing", "#62bcc0", "infuse water with spirit energy to heal", Material.ALLAY_SPAWN_EGG, null), water);
        this.register(new Skill("plantbending", "#32ad6f", "control plants", Material.VINE, null), water);
        this.register(new Skill("bloodbending", "#00368c", "control living beings", Material.REDSTONE, null), water);

        // mixed specializations
        this.register(new Skill("mudbending", "#635844", "control mud", Material.MUD, null), earth, water);
        this.register(new Skill("physique", "#cf46fb", "having an athletic physique", Material.TOTEM_OF_UNDYING, null), air, earth, fire, water);
    }

    @Override
    protected void tick() {}

    @Override
    protected void clean() {
        cache.clear();
    }

    /**
     * Attempt to register a new skill with the given parameters
     * 
     * @param skill   the skill to be registered
     * @param parents which skills this one derives from
     * @return the skill
     */
    public Skill register(Skill skill, Skill... parents) {
        Preconditions.checkArgument(skill != null, "Cannot register null skill");
        Preconditions.checkArgument(skill.getName() != null && !skill.getName().isBlank(), "Skill name cannot be empty!");
        Preconditions.checkArgument(!cache.values().stream().anyMatch(s -> s.getName().equals(skill.getName())), "Attempted to register Skill with an existing name");

        Config.process(skill);

        cache.put(skill.getName(), skill);

        for (Skill parent : parents) {
            if (parent == null) {
                continue;
            }

            parent.children = new ImmutableSet.Builder<Skill>().addAll(parent.children).add(skill).build();
        }

        skill.parents = ImmutableSet.copyOf(parents);
        skill.children = ImmutableSet.of();

        return skill;
    }

    public Skill get(String name) {
        return cache.get(name);
    }

    public Set<Skill> registered() {
        return new HashSet<>(cache.values());
    }

    public static Skills manager() {
        return Manager.of(Skills.class);
    }
}

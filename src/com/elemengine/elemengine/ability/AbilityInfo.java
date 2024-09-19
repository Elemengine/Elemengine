package com.elemengine.elemengine.ability;

import java.math.BigInteger;

import org.bukkit.event.Listener;

import com.elemengine.elemengine.ability.activation.Trigger;
import com.elemengine.elemengine.ability.util.Cooldown;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.relation.ElementRelation;
import com.elemengine.elemengine.element.relation.MultipleAnyRelation;
import com.elemengine.elemengine.element.relation.MultipleExactRelation;
import com.elemengine.elemengine.element.relation.SingleRelation;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class AbilityInfo implements Configurable, Listener {

    private final String name, author, version, description;
    private final ElementRelation relation;
    
    BigInteger bitFlag;

    protected AbilityInfo(String name, String description, String author, String version, ElementRelation relation) {
        this.name = name;
        this.description = description;
        this.author = author;
        this.version = version;
        this.relation = relation;
    }

    public BaseComponent createComponent() {
        TextComponent comp = new TextComponent();
        
        if (relation instanceof SingleRelation single) {
            comp.setColor(single.element().getChatColor());
            comp.addExtra(this.name);
        } else {
            Element[] elements = null;
            if (relation instanceof MultipleAnyRelation any) {
                elements = any.elements();
            } else if (relation instanceof MultipleExactRelation exc) {
                elements = exc.elements();
            }
            
            int i = 0;
            final int l = this.name.length() / elements.length;
            for (Element element : elements) {
                TextComponent extra = new TextComponent(this.name.substring(i * l, (i + 1) * l));
                extra.setColor(element.getChatColor());
                comp.addExtra(extra);
                ++i;
            }
        }
        
        return comp;
    }
    
    public final BigInteger getBitFlag() {
        return bitFlag;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getAuthor() {
        return author;
    }

    public final String getVersion() {
        return version;
    }

    public Cooldown.Tag getCooldownTag() {
        return Cooldown.tag(name, createComponent(), true);
    }

    public ElementRelation getElementRelation() { return relation; }

    public boolean isForElement(Element element) {
        return relation.includes(element);
    }

    @Override
    public String getFileName() {
        return name;
    }

    @Override
    public String getFolderName() {
        String folder = relation.folderName();
        if (!folder.startsWith("elements/")) {
            folder = "elements/" + folder;
        }
        
        return folder;
    }

    @Override
    public void postProcessed(Config config) {}

    protected void onRegister() {}

    public boolean canActivate(AbilityUser user, Trigger trigger) {
        return relation.check(user);
    }
}

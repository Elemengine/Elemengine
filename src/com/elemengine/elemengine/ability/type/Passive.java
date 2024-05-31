package com.elemengine.elemengine.ability.type;

import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.ability.AbilityUser;

public interface Passive {

    public AbilityInstance<?> createPassiveInstance(AbilityUser user);
    
}

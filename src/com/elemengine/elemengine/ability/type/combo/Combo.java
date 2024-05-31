package com.elemengine.elemengine.ability.type.combo;

import java.util.List;

import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.ability.AbilityUser;

public interface Combo {
    
    public AbilityInstance<?> createComboInstance(AbilityUser user);

    public List<SequenceInfo> getSequence();

    public default String getSequenceString() {
        String str = "";
        List<SequenceInfo> seq = this.getSequence();
        
        for (int i = 0; i < seq.size(); ++i) {
            str += seq.get(i).getAbility() + " (" + seq.get(i).getTrigger().getDisplay() + ") ";
            
            if (i < seq.size() - 1) {
                str += "> ";
            }
        }
        
        return str;
    }
}

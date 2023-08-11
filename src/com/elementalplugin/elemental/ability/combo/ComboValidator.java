package com.elementalplugin.elemental.ability.combo;

import java.util.ArrayList;
import java.util.List;

import com.elementalplugin.elemental.ability.AbilityInfo;
import com.elementalplugin.elemental.ability.activation.SequenceInfo;
import com.elementalplugin.elemental.ability.activation.Trigger;

public class ComboValidator {

    public enum Result {
        FAILED, COMPLETE, INCOMPLETE;
    }

    private ComboTree current;
    private List<SequenceInfo> sequence;

    /**
     * Construct a new agent that starts from the given {@link ComboTree}
     * 
     * @param starting where to start in the {@link ComboTree}
     */
    public ComboValidator(ComboTree starting) {
        this.current = starting;
        this.sequence = new ArrayList<>();
    }

    /**
     * Update the agent to the next branch of the {@link ComboTree} if possible
     * 
     * @param ability The given {@link Ability}
     * @param trigger The given {@link Activation}
     * @return {@link Result} of updating this agent
     */
    public Result update(AbilityInfo ability, Trigger trigger) {
        sequence.add(SequenceInfo.of(ability.getName(), trigger));
        current = current.getBranch(ability, trigger);

        return current == null ? Result.FAILED : (current.doesBranch() ? Result.INCOMPLETE : Result.COMPLETE);
    }

    /**
     * Gets the sequence that was followed by this agent
     * 
     * @return followed sequence
     */
    public List<SequenceInfo> getSequence() {
        return sequence;
    }
}

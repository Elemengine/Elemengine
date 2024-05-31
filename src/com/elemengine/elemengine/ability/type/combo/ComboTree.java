package com.elemengine.elemengine.ability.type.combo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.activation.Trigger;

public class ComboTree {

    private ComboTree root;
    private SequenceInfo info;
    private List<ComboTree> branches;

    public ComboTree() {
        this((String) null, null, null);
    }

    public ComboTree(AbilityInfo ability, Trigger trigger) {
        this(ability.getName(), trigger, null);
    }

    public ComboTree(AbilityInfo ability, Trigger trigger, ComboTree root) {
        this(ability.getName(), trigger, root);
    }

    public ComboTree(String ability, Trigger trigger, ComboTree root) {
        this.info = SequenceInfo.of(ability, trigger);
        this.root = root;
        this.branches = new ArrayList<>();
    }

    /**
     * Branches off this {@link ComboTree} with the given {@link Ability} and
     * {@link Activation}. This either makes a new branch if one doesn't exist, or
     * returns the one that exists.
     * 
     * @param ability The {@link Ability} name for the desired branch
     * @param trigger The {@link Activation} for the desired branch
     * @return next branch of this {@link ComboTree}
     */
    public ComboTree branch(String ability, Trigger trigger) {
        for (ComboTree branch : branches) {
            if (branch.info.matches(ability, trigger)) {
                return branch;
            }
        }

        ComboTree branch = new ComboTree(ability, trigger, this);
        branches.add(branch);
        return branch;
    }

    /**
     * Check whether a branch exists for the given {@link Ability} and
     * {@link Activation}
     * 
     * @param ability The {@link Ability} of the desired branch
     * @param trigger The {@link Activation} of the desired branch
     * @return true if branch exists
     */
    public boolean exists(AbilityInfo ability, Trigger trigger) {
        for (ComboTree branch : branches) {
            if (branch.info.matches(ability.getName(), trigger)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get the branch for the given {@link Ability} and {@link Activation}
     * 
     * @param ability The {@link Ability} of the desired branch
     * @param trigger The {@link Activation} of the desired branch
     * @return null if branch doesn't exist
     */
    public ComboTree getBranch(AbilityInfo ability, Trigger trigger) {
        for (ComboTree branch : branches) {
            if (branch.info.matches(ability.getName(), trigger)) {
                return branch;
            }
        }

        return null;
    }

    /**
     * Check whether this {@link ComboTree} branches
     * 
     * @return true if this {@link ComboTree} does branch
     */
    public boolean doesBranch() {
        return !branches.isEmpty();
    }

    /**
     * Get the sequence that lead to this branch
     * 
     * @return branch sequence
     */
    public Queue<SequenceInfo> sequence() {
        LinkedList<SequenceInfo> sequence = new LinkedList<>();
        ComboTree branch = this;
        while (branch.root != null && branch.info != null) {
            sequence.addFirst(branch.info);
            branch = branch.root;
        }
        return sequence;
    }

    /**
     * Construct the branches of this {@link ComboTree} based on a given sequence
     * 
     * @param sequence the combo sequence for activation
     * @return the given sequence if it is accepted
     * @throws IllegalArgumentException if the last branch of the given sequence
     *                                  already exists and branches further
     */
    public List<SequenceInfo> build(List<SequenceInfo> sequence) throws IllegalArgumentException {
        ComboTree branch = this;

        for (SequenceInfo info : sequence) {
            branch = branch.branch(info.getAbility(), info.getTrigger());
        }

        if (branch.doesBranch()) {
            throw new IllegalArgumentException("Given sequence to build ends on branching tree");
        }

        return sequence;
    }
}

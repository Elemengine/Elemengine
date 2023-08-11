package com.elementalplugin.elemental.storage;

import com.elementalplugin.elemental.util.data.Pair;

public class CollisionData {

    private String[] tags = new String[2];
    private Pair<String> tagPair;
    private String[] effects;
    private String[][] args;
    private Operator type;

    public CollisionData(String left, String right, Operator type) {
        this(left, right, type, null, null);
    }

    public CollisionData(String left, String right, Operator type, String[] effects, String[][] args) {
        this.tags[0] = left;
        this.tags[1] = right;
        this.tagPair = Pair.of(left, right);
        this.type = type;
        this.effects = effects;
        this.args = args;
    }

    public String getLeft() {
        return tags[0];
    }

    public String getRight() {
        return tags[1];
    }

    public Pair<String> getTags() {
        return tagPair;
    }

    public Operator getOperator() {
        return type;
    }

    public int getEffectAmount() {
        return effects == null ? 0 : effects.length;
    }

    public String getEffect(int index) {
        return effects == null ? "" : effects[index];
    }

    public String[] getArgs(int index) {
        return effects == null ? new String[0] : args[index];
    }

    public boolean isRemoved(String tag) {
        if (type == Operator.NEITHER) {
            return false;
        } else if (type == Operator.BOTH) {
            return true;
        }

        return tag.equals(tags[type.ordinal()]);
    }

    @Override
    public String toString() {
        return tags[0] + " " + type.getSymbol() + " " + tags[1];
    }

    public static CollisionData parse(String line) throws CollisionParseException {
        String[] split = line.split(" ");
        if (split.length < 3) {
            throw new CollisionParseException(line, "arg amount");
        }

        Operator op;
        try {
            op = Operator.fromSymbol(split[1]);
        } catch (Exception e) {
            throw new CollisionParseException(line, "operator");
        }

        String[] effects = null;
        String[][] args = null;

        if (split.length > 3) {
            effects = new String[split.length - 3];
            args = new String[split.length - 3][];
            for (int i = 0; i < split.length - 3; ++i) {
                String[] other = split[i + 3].split(":");
                effects[i] = other[0].trim();
                args[i] = other[1].split(",");
            }
        }

        return new CollisionData(split[0], split[2], op, effects, args);
    }

    public enum Operator {
        LEFT("<"), RIGHT(">"), NEITHER("="), BOTH("x");

        private String symbol;

        private Operator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public static Operator fromSymbol(String symbol) {
            switch (symbol) {
            case "=":
                return NEITHER;
            case "<":
                return LEFT;
            case ">":
                return RIGHT;
            case "x":
                return BOTH;
            }

            throw new IllegalArgumentException("No operator from " + symbol + " found");
        }
    }

    public static class CollisionParseException extends Exception {
        private static final long serialVersionUID = -8326822547202771825L;

        private CollisionParseException(String line, String field) {
            super("Problem with " + field + " in line '" + line + "'");
        }
    }
}

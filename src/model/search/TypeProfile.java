package model.search;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * For each of the four PIR types, records whether a condition can be satisfied
 * by that type: always, never, or only for some records.
 *
 * Used to prove that a condition can never be true at all: when all four types
 * come out ALWAYS_FALSE, no record can ever satisfy it.
 */
public final class TypeProfile {

    public enum Status {
        ALWAYS_TRUE,
        ALWAYS_FALSE,
        MAYBE
    }

    private final Map<Type, Status> statuses;

    private TypeProfile(Map<Type, Status> statuses) {
        this.statuses = statuses;
    }

    /** A condition that is always true for the given type and never true for the other three. */
    public static TypeProfile ofType(Type type) {
        Map<Type, Status> map = new EnumMap<>(Type.class);
        for (Type each : Type.values())
            map.put(each, each == type ? Status.ALWAYS_TRUE : Status.ALWAYS_FALSE);
        return new TypeProfile(map);
    }

    /** A condition that can only ever be true for the given types (a field of those types). */
    public static TypeProfile maybeFor(EnumSet<Type> types) {
        Map<Type, Status> map = new EnumMap<>(Type.class);
        for (Type each : Type.values())
            map.put(each, types.contains(each) ? Status.MAYBE : Status.ALWAYS_FALSE);
        return new TypeProfile(map);
    }

    public Status statusOf(Type type) {
        return statuses.get(type);
    }

    public TypeProfile and(TypeProfile other) {
        Map<Type, Status> map = new EnumMap<>(Type.class);
        for (Type each : Type.values())
            map.put(each, and(statuses.get(each), other.statusOf(each)));
        return new TypeProfile(map);
    }

    public TypeProfile or(TypeProfile other) {
        Map<Type, Status> map = new EnumMap<>(Type.class);
        for (Type each : Type.values())
            map.put(each, or(statuses.get(each), other.statusOf(each)));
        return new TypeProfile(map);
    }

    public TypeProfile not() {
        Map<Type, Status> map = new EnumMap<>(Type.class);
        for (Type each : Type.values())
            map.put(each, not(statuses.get(each)));
        return new TypeProfile(map);
    }

    /** True when no record of any type can satisfy this condition. */
    public boolean isImpossible() {
        for (Type each : Type.values())
            if (statuses.get(each) != Status.ALWAYS_FALSE)
                return false;
        return true;
    }

    /** The types that are not ruled out by this condition. */
    public EnumSet<Type> possibleTypes() {
        EnumSet<Type> possible = EnumSet.noneOf(Type.class);
        for (Type each : Type.values())
            if (statuses.get(each) != Status.ALWAYS_FALSE)
                possible.add(each);
        return possible;
    }

    /**
     * Explains why the two sides of an AND can never hold together.
     * Only used once isImpossible() has already been checked on the combination.
     */
    public static String describeConflict(TypeProfile left, TypeProfile right) {
        EnumSet<Type> leftTypes = left.possibleTypes();
        EnumSet<Type> rightTypes = right.possibleTypes();

        if (leftTypes.size() == 1 && rightTypes.size() == 1 && !leftTypes.equals(rightTypes)) {
            return "A record cannot be both " + leftTypes.iterator().next().getLabel()
                    + " and " + rightTypes.iterator().next().getLabel() + ".";
        }
        return "The two conditions can never be satisfied by the same kind of record.";
    }

    private static Status and(Status a, Status b) {
        if (a == Status.ALWAYS_FALSE || b == Status.ALWAYS_FALSE)
            return Status.ALWAYS_FALSE;
        if (a == Status.ALWAYS_TRUE)
            return b;
        if (b == Status.ALWAYS_TRUE)
            return a;
        return Status.MAYBE;
    }

    private static Status or(Status a, Status b) {
        if (a == Status.ALWAYS_TRUE || b == Status.ALWAYS_TRUE)
            return Status.ALWAYS_TRUE;
        if (a == Status.ALWAYS_FALSE)
            return b;
        if (b == Status.ALWAYS_FALSE)
            return a;
        return Status.MAYBE;
    }

    private static Status not(Status status) {
        return switch (status) {
            case ALWAYS_TRUE -> Status.ALWAYS_FALSE;
            case ALWAYS_FALSE -> Status.ALWAYS_TRUE;
            case MAYBE -> Status.MAYBE;
        };
    }
}

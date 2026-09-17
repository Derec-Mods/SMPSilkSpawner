package io.github.derec4.silkspawner.listener;

public final class SilkBreakContext {

    private static final ThreadLocal<Boolean> SKIP_XP = ThreadLocal.withInitial(() -> false);

    private SilkBreakContext() {
    }

    public static void setSkipXp(boolean skipXp) {
        SKIP_XP.set(skipXp);
    }

    public static boolean shouldSkipXp() {
        return Boolean.TRUE.equals(SKIP_XP.get());
    }

    public static void clear() {
        SKIP_XP.remove();
    }
}

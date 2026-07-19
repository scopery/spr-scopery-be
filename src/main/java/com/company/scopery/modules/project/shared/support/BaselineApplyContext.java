package com.company.scopery.modules.project.shared.support;

/**
 * Thread-local bypass for post-baseline strict edit guard while an approved
 * ChangeRequest is being applied through domain actions.
 */
public final class BaselineApplyContext {
    private static final ThreadLocal<Boolean> ACTIVE = ThreadLocal.withInitial(() -> Boolean.FALSE);

    private BaselineApplyContext() {}

    public static boolean isActive() {
        return Boolean.TRUE.equals(ACTIVE.get());
    }

    public static void run(Runnable action) {
        ACTIVE.set(Boolean.TRUE);
        try {
            action.run();
        } finally {
            ACTIVE.remove();
        }
    }

    public static <T> T call(java.util.concurrent.Callable<T> action) {
        ACTIVE.set(Boolean.TRUE);
        try {
            return action.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ACTIVE.remove();
        }
    }
}

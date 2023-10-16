package ch.abacus.test;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Reproducer {

    public static void main(String[] args) {
        while (!Thread.currentThread().isInterrupted()) {
            test(Level.INFO,"test", null);
        }
    }

    private static final String LOGGER = "X";
    private static final boolean LOG_TO_SYSTEM_OUT = false;

    static void test(Level level, String message, Throwable throwable){
        if (LOG_TO_SYSTEM_OUT){
            System.out.println("LOG: " + level + " -> " + message);
        } else {
            try {
                if (System.currentTimeMillis() > 0){
                    ((WeakReference)currentAParams().aMap().get("XY")).get();
                }
            } catch (Exception e){
            }
            getLogger().log(level, message, throwable);
        }
    }

    static java.util.logging.Logger getLogger(){
        return java.util.logging.Logger.getLogger(LOGGER);
    }

    static AParams currentAParams() {
        return ASession.currentSession().getApp().getParams();
    }

    static class AParams {
        AMap aMap() {
            return new AMap();
        }
    }

    static class ASession {
        AApp app = new AApp();
        static ASession.CurrentSession currentSession = new ASession.CurrentSession();

        AApp getApp() {
            return app;
        }

        static ASession currentSession() {
            return currentSession.getCurrentSession();
        }

        static class CurrentSession {
            static final ThreadLocal<ASession> CURRENT = new ThreadLocal<>();

            CurrentSession() {
                CURRENT.set(new ASession());
            }

            ASession getCurrentSession() {
                return CURRENT.get();
            }
        }
    }

    static class AApp {
        AParams getParams() {
            return new AParams();
        }
    }

    static class AMap {
        private final ReentrantLock lock = new ReentrantLock();
        private final Map<Object, Object> map = new HashMap<>();

        Object get(Object key) {
            lock.lock();
            try {
                return map.get(key);
            } finally {
                lock.unlock();
            }
        }
    }
}

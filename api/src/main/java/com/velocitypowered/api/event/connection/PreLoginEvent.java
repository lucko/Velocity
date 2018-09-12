package com.velocitypowered.api.event.connection;

import com.google.common.base.Preconditions;
import com.velocitypowered.api.event.ResultedEvent;
import com.velocitypowered.api.proxy.InboundConnection;

import net.kyori.text.Component;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This event is fired when a player has initiated a connection with the proxy but before the proxy authenticates the
 * player with Mojang or before the player's proxy connection is fully established (for offline mode).
 */
public class PreLoginEvent implements ResultedEvent<PreLoginEvent.PreLoginComponentResult> {
    private final InboundConnection connection;
    private final String username;
    private PreLoginComponentResult result;

    public PreLoginEvent(InboundConnection connection, String username) {
        this.connection = Preconditions.checkNotNull(connection, "connection");
        this.username = Preconditions.checkNotNull(username, "username");
        this.result = PreLoginComponentResult.allowed();
    }

    public InboundConnection getConnection() {
        return connection;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public PreLoginComponentResult getResult() {
        return result;
    }

    @Override
    public void setResult(@NonNull PreLoginComponentResult result) {
        this.result = Preconditions.checkNotNull(result, "result");
    }

    @Override
    public String toString() {
        return "PreLoginEvent{" +
                "connection=" + connection +
                ", username='" + username + '\'' +
                ", result=" + result +
                '}';
    }

    /**
     * Represents an "allowed/allowed with forced online\offline mode/denied" result with a reason allowed for denial.
     */
    public static class PreLoginComponentResult extends ResultedEvent.ComponentResult {
        private static final PreLoginComponentResult ALLOWED = new PreLoginComponentResult((Component) null);
        private static final PreLoginComponentResult FORCE_ONLINEMODE = new PreLoginComponentResult(true, false);
        private static final PreLoginComponentResult FORCE_OFFLINEMODE = new PreLoginComponentResult(false, true);

        private final boolean onlineMode;
        private final boolean forceOfflineMode;

        private PreLoginComponentResult(boolean allowedOnlineMode, boolean forceOfflineMode) {
            super(true, null);
            this.onlineMode = allowedOnlineMode;
            this.forceOfflineMode = forceOfflineMode;
        }

        private PreLoginComponentResult(@Nullable Component reason) {
            super(reason == null, reason);
            // Don't care about this
            this.onlineMode = false;
            this.forceOfflineMode = false;
        }

        public boolean isOnlineModeAllowed() {
            return this.onlineMode;
        }

        public boolean isForceOfflineMode() {
            return forceOfflineMode;
        }

        @Override
        public String toString() {
            if (isForceOfflineMode()) {
                return "allowed with force offline mode";
            }
            if (isOnlineModeAllowed()) {
                return "allowed with online mode";
            }
            
            return super.toString();
        }

        /**
         * Returns a result indicating the connection will be allowed through the proxy.
         * @return the allowed result
         */
        public static PreLoginComponentResult allowed() {
            return ALLOWED;
        }

        /**
         * Returns a result indicating the connection will be allowed through the proxy, but the connection will be
         * forced to use online mode provided that the proxy is in offline mode. This acts similarly to {@link #allowed()}
         * on an online-mode proxy.
         * @return the result
         */
        public static PreLoginComponentResult forceOnlineMode() {
            return FORCE_ONLINEMODE;
        }

        /**
         * Returns a result indicating the connection will be allowed through the proxy, but the connection will be
         * forced to use offline mode even when proxy running in online mode
         * @return the result
         */
        public static PreLoginComponentResult forceOfflineMode() {
            return FORCE_OFFLINEMODE;
        }

        /**
         * Denies the login with the specified reason.
         * @param reason the reason for disallowing the connection
         * @return a new result
         */
        public static PreLoginComponentResult denied(@NonNull Component reason) {
            Preconditions.checkNotNull(reason, "reason");
            return new PreLoginComponentResult(reason);
        }
    }
}

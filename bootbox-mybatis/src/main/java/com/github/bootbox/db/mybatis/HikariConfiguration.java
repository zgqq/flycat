package com.github.bootbox.db.mybatis;

/**
 * Created by zgq on 17-3-16.
 */
public class HikariConfiguration {
    private long idleTimeout;
    private long connectionTimeout;
    private long leakDetectionThreshold;

    private String initSQL;

    public long getIdleTimeout() {
        return idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout) {
        this.idleTimeout = idleTimeout;
    }

    public long getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public long getLeakDetectionThreshold() {
        return leakDetectionThreshold;
    }

    public void setLeakDetectionThreshold(long leakDetectionThreshold) {
        this.leakDetectionThreshold = leakDetectionThreshold;
    }

    public String getInitSQL() {
        return initSQL;
    }

    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL;
    }

    @Override
    public String toString() {
        return "HikariConfiguration{" +
                "idleTimeout=" + idleTimeout +
                ", connectionTimeout=" + connectionTimeout +
                ", leakDetectionThreshold=" + leakDetectionThreshold +
                ", initSQL='" + initSQL + '\'' +
                '}';
    }
}

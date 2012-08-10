package com.changyou.loganalysis.config;

import java.io.File;

import org.apache.commons.digester3.Digester;
import org.apache.log4j.Logger;

public class AnalysisConfigurator {
    private static Logger logger = Logger.getLogger(AnalysisConfigurator.class);

    private static AnalysisConfigurator configInstance = null;

    private static String XML_CONFIG_FILE = "config.xml";

    private LogAnalysisConfig config = null;

    public AnalysisConfigurator() {
        try {
            init();
        } catch (Exception e) {
            logger.error("error when loading config", e);
        }
    }

    private void init() throws Exception {
        config = digestConfig(XML_CONFIG_FILE);
    }

    public static AnalysisConfigurator getInstance() {
        if (configInstance == null) {
            synchronized (AnalysisConfigurator.class) {
                if (configInstance == null) {
                    configInstance = new AnalysisConfigurator();
                }
            }
        }

        return configInstance;
    }

    public LogAnalysisConfig getConfig() {
        return config;
    }

    private LogAnalysisConfig digestConfig(String configFile) throws Exception {
        Digester d = new Digester();
        d.addObjectCreate("log-analysis", "com.changyou.loganalysis.config.LogAnalysisConfig");
        d.addSetNestedProperties("log-analysis/analysis-worker", new String[] { "script-exec", "script-file",
                "thread-pool-size" }, new String[] { "scriptExec", "scriptFile", "threadPoolSize" });

        String pattern = "log-analysis/profile";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.ProfileConfig");
        d.addSetProperties(pattern);
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addProfileMap");

        pattern = "log-analysis/log-config";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogConfig");
        d.addSetProperties(pattern, new String[] { "name", "memo", "profile", "parent-path" }, new String[] { "name",
                "memo", "profile", "parentPath" });
        d.addSetNext(pattern, "addLogConfig");

        pattern = "log-analysis/log-config/log";
        d.addObjectCreate(pattern, "com.changyou.loganalysis.config.LogEntity");
        d.addSetProperties(pattern, new String[] { "file", "err-file", "memo" }, new String[] { "file", "errFile",
                "memo" });
        d.addSetNestedProperties(pattern, new String[] { "log-format", "log-separator", "log-costunit" }, new String[] {
                "logFormat", "logSeparator", "logCostunit" });
        d.addSetNext(pattern, "addLogEntity");

        LogAnalysisConfig config = d.parse(new File(configFile));

        return config;
    }
}
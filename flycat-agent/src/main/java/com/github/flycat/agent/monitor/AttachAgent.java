package com.github.flycat.agent.monitor;

import com.github.flycat.util.shell.PidUtils;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.security.CodeSource;

public class AttachAgent {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachAgent.class);

    public static void attachAgent() throws Throwable {

        String currentPid = PidUtils.currentPid();

        VirtualMachine virtualMachine;
        virtualMachine = VirtualMachine.attach(currentPid);

        try {
            File appHomeDir = null;
            CodeSource codeSource = AttachAgent.class.getProtectionDomain().getCodeSource();
            File bootJarPath = null;
            if (codeSource != null) {
//                bootJarPath = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                bootJarPath = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                appHomeDir = bootJarPath.getParentFile();
            }

//            String agentPath = new File(appHomeDir, "flycat-agent.jar").getAbsolutePath();
            String agentPath = bootJarPath.getAbsolutePath();
            LOGGER.info("Agent path jar, parent:{}, path:{}", appHomeDir.getAbsolutePath(), agentPath);
            virtualMachine.loadAgent(agentPath,
                    "");
        } finally {
            if (null != virtualMachine) {
                virtualMachine.detach();
            }
        }
    }
}

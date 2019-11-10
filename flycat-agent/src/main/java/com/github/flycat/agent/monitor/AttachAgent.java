package com.github.flycat.agent.monitor;

import com.github.flycat.util.shell.PidUtils;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.File;
import java.security.CodeSource;

public class AttachAgent {

    private void attachAgent() throws Exception {

        VirtualMachineDescriptor virtualMachineDescriptor = null;
        String currentPid = PidUtils.currentPid();

        VirtualMachine virtualMachine = null;
        virtualMachine = VirtualMachine.attach(currentPid);

        try {
            File appHomeDir = null;
            CodeSource codeSource = AttachAgent.class.getProtectionDomain().getCodeSource();
            File bootJarPath;
            if (codeSource != null) {
                bootJarPath = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                appHomeDir = bootJarPath.getParentFile();
            }

            String agentPath = new File(appHomeDir, "flycat-agent.jar").getAbsolutePath();
            virtualMachine.loadAgent(agentPath,
                    "");
        } finally {
            if (null != virtualMachine) {
                virtualMachine.detach();
            }
        }
    }
}

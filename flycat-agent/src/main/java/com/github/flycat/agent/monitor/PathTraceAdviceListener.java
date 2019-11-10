package com.github.flycat.agent.monitor;

import com.github.flycat.agent.monitor.command.CommandProcess;
import com.github.flycat.agent.monitor.command.TraceCommand;

/**
 * @author ralf0131 2017-01-05 13:59.
 */
public class PathTraceAdviceListener extends AbstractTraceAdviceListener {

    public PathTraceAdviceListener(TraceCommand command, CommandProcess process) {
        super(command, process);
    }
}

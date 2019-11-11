package com.github.flycat.agent.monitor.command;

/**
 * The base command class that Java annotated command should extend.
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
public abstract class AnnotatedCommand {

    /**
     * @return the command name
     */
    public String name() {
        return null;
    }

    /**
     * Process the command, when the command is done processing it should call the {@link CommandProcess#end()} method.
     *
     * @param process the command process
     */
    public abstract void process(CommandProcess process);

}



package com.tencent.tws.framework.common;

public interface ICommandHandler {
    public boolean doCommand(TwsMsg oMsg, Device oDeviceFrom);
}

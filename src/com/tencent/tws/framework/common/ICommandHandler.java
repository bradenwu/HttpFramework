
package com.tencent.tws.framework.common;

import android.bluetooth.BluetoothClass.Device;

public interface ICommandHandler {
    public boolean doCommand(TwsMsg oMsg, Device oDeviceFrom);
}

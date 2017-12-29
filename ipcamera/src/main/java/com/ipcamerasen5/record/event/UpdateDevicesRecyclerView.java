package com.ipcamerasen5.record.event;

import com.ipcamerasen5.record.db.IpCamDevice;

/**
 * 更新recordActivity处recyclerView的UI
 * Created by chenqianghua on 2017/7/5.
 */

public class UpdateDevicesRecyclerView {
    private IpCamDevice mIpCamDevice;

    public UpdateDevicesRecyclerView(IpCamDevice ipCamDevice) {
        mIpCamDevice = ipCamDevice;
    }
}

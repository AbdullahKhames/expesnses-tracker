import { v4 as uuid } from "uuid";

const DeviceIdHolder = {
    getDeviceId: function() {
        let deviceId = localStorage.getItem("deviceId");
        // console.log(deviceId);
        if (!deviceId) {
            deviceId = uuid();
            localStorage.setItem("deviceId", deviceId);
        }
        return deviceId;
    }
};

export default DeviceIdHolder;

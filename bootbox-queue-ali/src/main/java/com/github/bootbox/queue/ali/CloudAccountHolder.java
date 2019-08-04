package com.github.bootbox.queue.ali;

import com.aliyun.mns.client.CloudAccount;

public class CloudAccountHolder {
    private final CloudAccount account;

    public CloudAccountHolder(AliConnectConfig config) {
        account = new CloudAccount(config.getYourAccessId(), config.getYourAccessKey(),
                config.getEndpoint());
    }

    public CloudAccount getAccount() {
        return account;
    }
}

/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

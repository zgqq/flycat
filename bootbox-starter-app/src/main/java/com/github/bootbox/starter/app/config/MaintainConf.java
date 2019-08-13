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
package com.github.bootbox.starter.app.config;

import java.util.StringJoiner;

public class MaintainConf {
    private String responseMsg;
    private Integer responseSwitch;

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public Integer getResponseSwitch() {
        return responseSwitch;
    }

    public void setResponseSwitch(Integer responseSwitch) {
        this.responseSwitch = responseSwitch;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MaintainConf.class.getSimpleName() + "[", "]")
                .add("responseMsg='" + responseMsg + "'")
                .add("responseSwitch=" + responseSwitch)
                .toString();
    }
}

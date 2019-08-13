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
package com.github.bootbox.alarm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogAlarmSender implements AlarmSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAlarmSender.class);

    @Override
    public void sendNotify(String message) {
        LOGGER.info("send nothing, message:{}", message);
    }

//    @Override
//    public void sendNotify(String type, String token, String message) {
//        LOGGER.info("Nothing send notify");
//    }
}

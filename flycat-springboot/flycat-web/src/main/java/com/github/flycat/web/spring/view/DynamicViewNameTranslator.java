/**
 * Copyright 2019 zgqq <zgqjava@gmail.com>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.flycat.web.spring.view;

import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;

import javax.servlet.http.HttpServletRequest;

public class DynamicViewNameTranslator implements RequestToViewNameTranslator {
    private volatile DefaultRequestToViewNameTranslator viewNameTranslator = new DefaultRequestToViewNameTranslator();

    public void setPrefix(String prefix) {
        final DefaultRequestToViewNameTranslator translator
                = new DefaultRequestToViewNameTranslator();
        translator.setPrefix(prefix);
        viewNameTranslator = translator;
    }

    @Override
    public String getViewName(HttpServletRequest request) throws Exception {
        return viewNameTranslator.getViewName(request);
    }
}

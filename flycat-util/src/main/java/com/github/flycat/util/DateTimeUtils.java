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
package com.github.flycat.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {

    public static long currentTimeMillisPlusDays(int days) {
        return System.currentTimeMillis() + TimeUnit.DAYS.toMillis(days);
    }

    public static long currentTimeMillisPlusSeconds(int seconds) {
        return System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
    }

    public static int isContinuousDay(LocalDateTime lastDateTime, LocalDateTime nextDateTime) {
        long between = ChronoUnit.DAYS.between(lastDateTime.toLocalDate(), nextDateTime.toLocalDate());
        return isContinuousPeriod(between);
    }

    public static int isContinuousWeek(LocalDateTime lastDateTime, LocalDateTime nextDateTime) {
        final LocalDate start = lastDateTime.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate();
        final LocalDate end = nextDateTime.with(ChronoField.DAY_OF_WEEK, 1).toLocalDate();
        return isContinuousPeriod(ChronoUnit.WEEKS.between(start, end));
    }

    public static int isContinuousMonth(LocalDateTime lastDateTime, LocalDateTime now) {
        int state;
        final LocalDate start = lastDateTime.withDayOfMonth(1).toLocalDate();
        final LocalDate end = now.withDayOfMonth(1).toLocalDate();
        long between = ChronoUnit.DAYS.between(start, end);
        final int lengthOfMonth = YearMonth.from(start).lengthOfMonth();
        final int current = YearMonth.from(end).lengthOfMonth();
        int total = current + lengthOfMonth;
        if (between < lengthOfMonth) {
            state = 0;
        } else if (between >= lengthOfMonth && between < total) {
            state = 1;
        } else {
            state = -1;
        }
        return state;
    }

    public static int isContinuousPeriod(long diff) {
        int state;
        if (diff > 1) {
            state = -1;
        } else if (diff == 1) {
            state = 1;
        } else {
            state = 0;
        }
        return state;
    }
}

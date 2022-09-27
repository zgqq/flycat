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
package com.github.flycat.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtils {



    // 2019/10/12
    public static String formatToPathDate(Date date) {
        return DateFormatUtils.format(date, "yyyy/MM/dd");
    }

    public static Date parseISO8601DateTime(String source) throws ParseException {
        return DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.parse(source);
    }

    public static Date parseISO8601Date(String source) throws ParseException {
        return DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.parse(source);
    }

    public static String toISO8601DateFormat(Date date) {
        return DateFormatUtils.ISO_8601_EXTENDED_DATE_FORMAT.format(date);
    }

    public static String toISO8601DateTimeFormat(Date date) {
        return DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(date);
    }

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

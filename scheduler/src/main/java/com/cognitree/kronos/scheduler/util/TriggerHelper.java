/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cognitree.kronos.scheduler.util;

import com.cognitree.kronos.scheduler.model.CalendarIntervalSchedule;
import com.cognitree.kronos.scheduler.model.CronSchedule;
import com.cognitree.kronos.scheduler.model.DailyTimeIntervalSchedule;
import com.cognitree.kronos.scheduler.model.Schedule;
import com.cognitree.kronos.scheduler.model.SimpleSchedule;
import com.cognitree.kronos.scheduler.model.WorkflowTrigger;
import org.quartz.CalendarIntervalScheduleBuilder;
import org.quartz.CalendarIntervalTrigger;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.DailyTimeIntervalScheduleBuilder;
import org.quartz.DailyTimeIntervalTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.ScheduleBuilder;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TimeOfDay;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import static java.util.TimeZone.getTimeZone;
import static org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.DailyTimeIntervalScheduleBuilder.dailyTimeIntervalSchedule;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

public class TriggerHelper {

    public static Trigger buildTrigger(WorkflowTrigger workflowTrigger) throws ParseException {
        return buildTrigger(workflowTrigger, null, null, null);
    }

    public static Trigger buildTrigger(WorkflowTrigger workflowTrigger, JobDataMap jobDataMap,
                                       TriggerKey triggerKey, JobKey jobKey) throws ParseException {
        final ScheduleBuilder scheduleBuilder = buildSchedulerBuilder(workflowTrigger.getSchedule());
        TriggerBuilder triggerBuilder = newTrigger()
                .withSchedule(scheduleBuilder)
                .startNow()
                .forJob(jobKey)
                .withIdentity(triggerKey);

        if (jobDataMap != null) {
            triggerBuilder.usingJobData(jobDataMap);
        }

        // Set Start Date
        if (workflowTrigger.getStartAt() != null) {
            triggerBuilder.startAt(new Date(workflowTrigger.getStartAt()));
        }

        // Set End Date
        if (workflowTrigger.getEndAt() != null) {
            triggerBuilder.endAt(new Date(workflowTrigger.getEndAt()));
        }

        return triggerBuilder.build();
    }

    private static ScheduleBuilder buildSchedulerBuilder(Schedule schedule) throws ParseException {
        ScheduleBuilder scheduleBuilder = null;
        switch (schedule.getType()) {
            case cron:
                scheduleBuilder = buildCronScheduleBuilder((CronSchedule) schedule);
                break;
            case simple:
                scheduleBuilder = buildSimpleScheduleBuilder((SimpleSchedule) schedule);
                break;
            case daily_time:
                scheduleBuilder = buildDailyTimeScheduleBuilder((DailyTimeIntervalSchedule) schedule);
                break;
            case calendar:
                scheduleBuilder = buildCalendarTimeScheduleBuilder((CalendarIntervalSchedule) schedule);
                break;
        }
        return scheduleBuilder;
    }

    private static ScheduleBuilder buildSimpleScheduleBuilder(SimpleSchedule simpleSchedule) {
        SimpleScheduleBuilder scheduleBuilder = simpleSchedule();
        if (simpleSchedule.isRepeatForever()) {
            scheduleBuilder.repeatForever();
        }

        scheduleBuilder.withIntervalInMilliseconds(simpleSchedule.getRepeatIntervalInMs());
        if (simpleSchedule.getRepeatCount() > 0) {
            scheduleBuilder.withRepeatCount(simpleSchedule.getRepeatCount());
        }

        switch (simpleSchedule.getMisfireInstruction()) {
            case SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW:
                scheduleBuilder.withMisfireHandlingInstructionFireNow();
                break;
            case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_EXISTING_REPEAT_COUNT:
                scheduleBuilder.withMisfireHandlingInstructionNowWithExistingCount();
                break;
            case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NOW_WITH_REMAINING_REPEAT_COUNT:
                scheduleBuilder.withMisfireHandlingInstructionNowWithRemainingCount();
                break;
            case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_REMAINING_COUNT:
                scheduleBuilder.withMisfireHandlingInstructionNextWithRemainingCount();
                break;
            case SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT:
                scheduleBuilder.withMisfireHandlingInstructionNextWithExistingCount();
                break;
        }

        return scheduleBuilder;
    }

    private static ScheduleBuilder buildCronScheduleBuilder(CronSchedule cronSchedule) throws ParseException {
        CronExpression cronExpression = new CronExpression(cronSchedule.getCronExpression());
        CronScheduleBuilder scheduleBuilder = cronSchedule(cronExpression);
        switch (cronSchedule.getMisfireInstruction()) {
            case CronTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                break;
            case CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                break;
            case CronTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                break;
        }
        if (cronSchedule.getTimezone() != null) {
            scheduleBuilder.inTimeZone(getTimeZone(cronSchedule.getTimezone()));
        }
        return scheduleBuilder;
    }

    private static ScheduleBuilder buildDailyTimeScheduleBuilder(DailyTimeIntervalSchedule dailyTimeIntervalSchedule) {
        DailyTimeIntervalScheduleBuilder scheduleBuilder = dailyTimeIntervalSchedule();
        switch (dailyTimeIntervalSchedule.getMisfireInstruction()) {
            case DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                break;
            case DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                break;
            case DailyTimeIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                break;
        }

        scheduleBuilder.onDaysOfTheWeek(dailyTimeIntervalSchedule.getDaysOfWeek());
        if (dailyTimeIntervalSchedule.getRepeatCount() > 0) {
            scheduleBuilder.withRepeatCount(dailyTimeIntervalSchedule.getRepeatCount());
        }
        scheduleBuilder.withInterval(dailyTimeIntervalSchedule.getRepeatInterval(),
                dailyTimeIntervalSchedule.getRepeatIntervalUnit());

        final TimeOfDay startTimeOfDay = getTimeOfDay(dailyTimeIntervalSchedule.getStartTimeOfDay(),
                dailyTimeIntervalSchedule.getTimezone());
        scheduleBuilder.startingDailyAt(startTimeOfDay);
        final TimeOfDay endTimeOfDay = getTimeOfDay(dailyTimeIntervalSchedule.getEndTimeOfDay(),
                dailyTimeIntervalSchedule.getTimezone());
        scheduleBuilder.endingDailyAt(endTimeOfDay);
        return scheduleBuilder;
    }

    /**
     * convert time of day to a specific timezone
     */
    private static TimeOfDay getTimeOfDay(DailyTimeIntervalSchedule.TimeOfDay timeOfDay, String timezoneId) {
        final Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, timeOfDay.getHour());
        cal.set(Calendar.MINUTE, timeOfDay.getMinute());
        cal.set(Calendar.SECOND, timeOfDay.getSecond());
        cal.clear(Calendar.MILLISECOND);
        final TimeZone timeZone = timezoneId == null || timezoneId.isEmpty()
                ? TimeZone.getDefault() : TimeZone.getTimeZone(timezoneId);
        final Calendar timezoneCal = new GregorianCalendar(timeZone);
        timezoneCal.setTimeInMillis(cal.getTimeInMillis());
        return new TimeOfDay(timezoneCal.get(Calendar.HOUR), timezoneCal.get(Calendar.MINUTE), timezoneCal.get(Calendar.SECOND));
    }

    private static ScheduleBuilder buildCalendarTimeScheduleBuilder(CalendarIntervalSchedule calendarIntervalSchedule) {
        CalendarIntervalScheduleBuilder scheduleBuilder = calendarIntervalSchedule();
        switch (calendarIntervalSchedule.getMisfireInstruction()) {
            case CalendarIntervalTrigger.MISFIRE_INSTRUCTION_FIRE_ONCE_NOW:
                scheduleBuilder.withMisfireHandlingInstructionFireAndProceed();
                break;
            case CalendarIntervalTrigger.MISFIRE_INSTRUCTION_DO_NOTHING:
                scheduleBuilder.withMisfireHandlingInstructionDoNothing();
                break;
            case CalendarIntervalTrigger.MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY:
                scheduleBuilder.withMisfireHandlingInstructionIgnoreMisfires();
                break;
        }
        scheduleBuilder.withInterval(calendarIntervalSchedule.getRepeatInterval(),
                calendarIntervalSchedule.getRepeatIntervalUnit());

        if (calendarIntervalSchedule.getTimezone() != null) {
            scheduleBuilder.inTimeZone(TimeZone.getTimeZone(calendarIntervalSchedule.getTimezone()));
        }
        scheduleBuilder.preserveHourOfDayAcrossDaylightSavings(calendarIntervalSchedule.isPreserveHourOfDayAcrossDaylightSavings());
        scheduleBuilder.skipDayIfHourDoesNotExist(calendarIntervalSchedule.isSkipDayIfHourDoesNotExist());
        return scheduleBuilder;
    }
}

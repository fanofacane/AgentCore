package com.sky.AgentCore.dto.config;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/** 重复配置值对象 根据不同的重复类型存储相应的配置信息 */
@Data
public class RepeatConfig {

    /** 执行时间 */
    private LocalDateTime executeDateTime;

    /** 每周重复时的星期几列表 (1-7, 1表示周一) */
    private List<Integer> weekdays;

    /** 每月重复时的日期 (1-31) */
    private Integer monthDay;

    /** 自定义重复的间隔数 */
    private Integer interval;

    /** 自定义重复的时间单位 (DAYS, WEEKS, MONTHS) */
    private String timeUnit;

    /** 自定义重复的执行时间 */
    private String executeTime;

    /** 自定义重复的截止日期 */
    private LocalDateTime endDateTime;

    public RepeatConfig() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RepeatConfig that = (RepeatConfig) o;
        return Objects.equals(executeDateTime, that.executeDateTime) && Objects.equals(weekdays, that.weekdays)
                && Objects.equals(monthDay, that.monthDay) && Objects.equals(interval, that.interval)
                && Objects.equals(timeUnit, that.timeUnit) && Objects.equals(executeTime, that.executeTime)
                && Objects.equals(endDateTime, that.endDateTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(executeDateTime, weekdays, monthDay, interval, timeUnit, executeTime, endDateTime);
    }

    @Override
    public String toString() {
        return "RepeatConfig{" + "executeDateTime=" + executeDateTime + ", weekdays=" + weekdays + ", monthDay="
                + monthDay + ", interval=" + interval + ", timeUnit='" + timeUnit + '\'' + ", executeTime='"
                + executeTime + '\'' + ", endDateTime=" + endDateTime + '}';
    }
}

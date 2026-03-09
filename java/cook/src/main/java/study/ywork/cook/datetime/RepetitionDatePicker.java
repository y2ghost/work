package study.ywork.cook.datetime;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

// 获取每月特定日期示例，例如每月第二个周四14:00
public class RepetitionDatePicker {
    /** 特殊常量表示月的最后一周 */
    public static final int LAST = -1;
    private DayOfWeek dayOfWeek;
    private int weekOfMonth;
    private LocalTime hourOfDay;

    /**
     * 配置周期性日期时间，比如每月第三周的周四12:00
     * 
     * @param weekOfMonth 每月第几周
     * @param dayOfWeek   每周第几天
     * @param hourOfDay   当天时间
     */
    public RepetitionDatePicker(int weekOfMonth, DayOfWeek dayOfWeek, LocalTime hourOfDay) {
        super();
        this.weekOfMonth = weekOfMonth;
        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay;

        if (weekOfMonth == LAST) {
            return;
        }

        if (weekOfMonth < 1 || weekOfMonth > 5) {
            throw new IllegalArgumentException("每月最多4周或者LAST");
        }
    }

    public RepetitionDatePicker(int weekOfMonth, DayOfWeek dayOfWeek) {
        this(weekOfMonth, dayOfWeek, LocalTime.of(12, 00));
    }

    // 获取将来第几个周期日期
    public LocalDate getEventLocalDate(int meetingsAway) {
        LocalDate now = LocalDate.now();
        LocalDate thisMeeting = (weekOfMonth != LAST)
            ? now.with(TemporalAdjusters.dayOfWeekInMonth(weekOfMonth, dayOfWeek))
            : now.with(TemporalAdjusters.lastInMonth(dayOfWeek));

        // 是否已经过了周期日期时间，过了，从下个月计算
        if (thisMeeting.isBefore(now)) {
            meetingsAway++;
        }

        if (meetingsAway > 0) {
            thisMeeting = thisMeeting.plusMonths(meetingsAway)
                .with(TemporalAdjusters.dayOfWeekInMonth(weekOfMonth, dayOfWeek));
        }

        return thisMeeting;
    }

    // 获取将来第几个周期日期时间
    public LocalDateTime getEventLocalDateTime(int meetingsAway) {
        return LocalDateTime.of(getEventLocalDate(meetingsAway), hourOfDay);
    }
}

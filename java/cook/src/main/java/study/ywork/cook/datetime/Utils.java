package study.ywork.cook.datetime;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Utils {
    private static Clock clock = Clock.systemDefaultZone();

    public static void main(String[] args) {
        currentDateTime();
        dateFormatter();
        dateDiff();
        dateAdd();
        meetingDateTime();
    }

    private static void currentDateTime() {
        LocalDate dateNow = LocalDate.now();
        System.out.println(dateNow);
        LocalTime timeNow = LocalTime.now();
        System.out.println(timeNow);
        LocalDateTime dateTimeNow = LocalDateTime.now(clock);
        System.out.println(dateTimeNow);
    }

    private static void dateFormatter() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy/LL/dd");
        System.out.println(df.format(LocalDate.now()));
        System.out.println(LocalDate.parse("2022/05/30", df));
        DateTimeFormatter nTZ = DateTimeFormatter.ofPattern("d MMMM, yyyy h:mm a");
        System.out.println(ZonedDateTime.now().format(nTZ));

        // 时间戳转日期
        Instant epochSec = Instant.ofEpochSecond(1000000000L);
        ZoneId zId = ZoneId.systemDefault();
        ZonedDateTime then = ZonedDateTime.ofInstant(epochSec, zId);
        System.out.println("1000000000L 秒对应日期 " + then);

        // 日期转时间戳
        long epochSecond = ZonedDateTime.now().toInstant().getEpochSecond();
        System.out.println("当前时间戳: " + epochSecond);

        ZonedDateTime here = ZonedDateTime.now();
        ZonedDateTime there = here.withZoneSameInstant(ZoneId.of("Europe/Berlin"));
        System.out.printf("当前地区日期: %s, 欧洲柏林时区: %s%n", here, there);
    }

    private static void dateDiff() {
        LocalDate endOf20thCentury = LocalDate.of(2000, 12, 31);
        LocalDate now = LocalDate.now();
        Period diff = Period.between(endOf20thCentury, now);
        long days = ChronoUnit.DAYS.between(endOf20thCentury, now);
        System.out.printf("%s比%s大%s, %d天%n", now, endOf20thCentury, diff, days);
        System.out.printf("%d年%d月%d日%n", diff.getYears(), diff.getMonths(), diff.getDays());
    }
    
    private static void dateAdd() {
        LocalDate now =  LocalDate.now();
        Period p = Period.ofDays(700);
        LocalDate then1 = now.plus(p);
        LocalDate then2 = now.plusDays(700);
        System.out.printf("%s后700天, 示例1:%s, 示例2:%s%n", now, then1, then2);
    }
    
    private static void meetingDateTime() {
        RepetitionDatePicker picker = new RepetitionDatePicker(3,
            DayOfWeek.FRIDAY,LocalTime.of(14, 30));
        DateTimeFormatter dfm = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        for (int i=0; i<=2; i++) {
            LocalDateTime dt = picker.getEventLocalDateTime(i);
            System.out.println(dt.format(dfm));
        }
    }
}

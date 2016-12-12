package org.deadio;

import java.time.LocalDate;
import java.time.Period;

/**
 * Created by yoni on 12/12/16.
 */
public class Utils {
    public static String getDifferenceBetweenDates(LocalDate dateFrom, LocalDate dateTo){
        Period period = Period.between(dateFrom, dateTo);

        return String.format("You have %d years, %d months and %d days remaining. Use them well.", period.getYears(), period.getMonths(), period.getDays());
    }
}

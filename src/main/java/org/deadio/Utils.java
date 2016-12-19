package org.deadio;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

/**
 * Created by yoni on 12/12/16.
 */
public class Utils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");;

    public static String getFinalMessage(LocalDate dateFrom, LocalDate dateTo){
        Period period = Period.between(dateFrom, dateTo);

        return String.format("You have %d years, %d months and %d days remaining. Use them well.", period.getYears(), period.getMonths(), period.getDays());
    }

    public static String getFinalMessage(LocalDate birthday, int numYearsRemaining){
        LocalDate deathDate = birthday.plusYears(numYearsRemaining);

        return String.format("You will die on %s. You have %d years remaining. Use them well.", deathDate.format(formatter), numYearsRemaining);
    }
}
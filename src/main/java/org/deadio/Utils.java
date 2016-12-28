package org.deadio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yoni on 12/12/16.
 */
public class Utils {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");;
    private static final List<String> countries = new ArrayList<>();
    private static final Map<String, Integer> numbersToDigits = new HashMap<String, Integer>(){{
            put("one", 1);
            put("two", 2);
            put("three", 3);
            put("four", 4);
            put("five", 5);
            put("six", 6);
            put("seven", 7);
            put("eight", 8);
            put("nine", 9);
            put("ten", 10);
            put("eleven", 11);
            put("twelve", 12);
            put("thirteen", 13);
            put("fourteen", 14);
            put("fifteen", 15);
            put("sixteen", 16);
            put("seventeen", 17);
            put("eighteen", 18);
            put("nineteen", 19);
            put("twenty", 20);
            put("twenty one", 21);
            put("twenty two", 22);
            put("twenty three", 23);
            put("twenty four", 24);
            put("twenty five", 25);
            put("twenty six", 26);
            put("twenty seven", 27);
            put("twenty eight", 28);
            put("twenty nine", 29);
            put("thirty", 30);
            put("thirty one", 31);
            put("thirty two", 32);
            put("thirty three", 33);
            put("thirty four", 34);
            put("thirty five", 35);
            put("thirty six", 36);
            put("thirty seven", 37);
            put("thirty eight", 38);
            put("thirty nine", 39);
            put("forty", 40);
            put("forty one", 41);
            put("forty two", 42);
            put("forty three", 43);
            put("forty four", 44);
            put("forty five", 45);
            put("forty six", 46);
            put("forty seven", 47);
            put("forty eight", 48);
            put("forty nine", 49);
            put("fifty", 50);
            put("fifty one", 51);
            put("fifty two", 52);
            put("fifty three", 53);
            put("fifty four", 54);
            put("fifty five", 55);
            put("fifty six", 56);
            put("fifty seven", 57);
            put("fifty eight", 58);
            put("fifty nine", 59);
            put("sixty", 60);
            put("sixty one", 61);
            put("sixty two", 62);
            put("sixty three", 63);
            put("sixty four", 64);
            put("sixty five", 65);
            put("sixty six", 66);
            put("sixty seven", 67);
            put("sixty eight", 68);
            put("sixty nine", 69);
            put("seventy", 70);
            put("seventy one", 71);
            put("seventy two", 72);
            put("seventy three", 73);
            put("seventy four", 74);
            put("seventy five", 75);
            put("seventy six", 76);
            put("seventy seven", 77);
            put("seventy eight", 78);
            put("seventy nine", 79);
            put("eighty", 80);
            put("eighty one", 81);
            put("eighty two", 82);
            put("eighty three", 83);
            put("eighty four", 84);
            put("eighty five", 85);
            put("eighty six", 86);
            put("eighty seven", 87);
            put("eighty eight", 88);
            put("eighty nine", 89);
            put("ninety", 90);
            put("ninety one", 91);
            put("ninety two", 92);
            put("ninety three", 93);
            put("ninety four", 94);
            put("ninety five", 95);
            put("ninety six", 96);
            put("ninety seven", 97);
            put("ninety eight", 98);
            put("ninety nine", 99);
            put("hundred", 100);
        }};

    public static String getFinalMessage(LocalDate dateFrom, LocalDate dateTo){
        Period period = Period.between(dateFrom, dateTo);

        return String.format("You have %d years, %d months and %d days remaining. Use them well.", period.getYears(), period.getMonths(), period.getDays());
    }

    public static String getFinalMessage(LocalDate birthday, int numYearsRemaining){
        LocalDate deathDate = birthday.plusYears(numYearsRemaining);

        return String.format("You will die on %s. You have %d years remaining. Use them well.", deathDate.format(formatter), numYearsRemaining);
    }

    public static String getFinalMessage(double lifeExpectancy){
        int numYearsRemaining = (int) lifeExpectancy;

        return String.format("You have %d years remaining. Use them well.", numYearsRemaining);
    }

    public static String verifyCountry(String inputText) throws IOException {
        if (countries.isEmpty()){
            String countryFilepath = Utils.class.getClassLoader().getResource("country_codes.csv").getFile();
            Stream<String> lines = Files.lines(Paths.get(countryFilepath));
            List<String> countryList = lines
                    .map(line -> line.split(",")[0])
                    .collect(Collectors.toList());
            lines.close();
            countries.addAll(countryList);
        }
        Optional<String> countryOptional = countries.stream().filter(country -> inputText.toLowerCase().contains(country.toLowerCase())).findFirst();

        return countryOptional.isPresent() ? countryOptional.get() : null;
    }

    public static String verifyAge(String inputText){
        Optional<String> numberOptional = numbersToDigits.keySet().stream().filter(number -> inputText.toLowerCase().contains(number)).findFirst();

        return numberOptional.isPresent() ? numberOptional.get() : null;
    }

    public static int numberToDigits(String numberString){
        return numbersToDigits.get(numberString);
    }

    public static String verifyGender(String inputText){
        if (inputText.toLowerCase().contains("male") || inputText.toLowerCase().contains("mail"))
            return "male";
        if (inputText.toLowerCase().contains("female"))
            return "female";

        return null;
    }

    public static void main(String[] args){
        Utils.verifyAge("i am 23 years old");
    }
}
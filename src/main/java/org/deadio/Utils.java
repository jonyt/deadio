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

// TODO:
//  * Create main class (runs loop, plays sound file (see http://stackoverflow.com/a/11514812) and listens for results) (Main, Query)
//  * Get mortality data from http://apps.who.int/gho/data/view.main.60040?lang=en, use expectation of life at age x (Crawler)
//  * Query mortality data and provide death estimate (LifeExpectancyInfo - getLifeExpectancy(gender, country, age))
//  * Read it out (TextToSpeech translate(text))


//

/**
 * Afghanistan
 Albania
 Algeria
 Andorra
 Angola
 Antigua and Barbuda
 Argentina
 Armenia
 Aruba
 Australia
 Austria
 Azerbaijan
 Bahamas, The
 Bahrain
 Bangladesh
 Barbados
 Belarus
 Belgium
 Belize
 Benin
 Bhutan
 Bolivia
 Bosnia and Herzegovina
 Botswana
 Brazil
 Brunei
 Bulgaria
 Burkina Faso
 Burma
 Burundi
 Cambodia
 Cameroon
 Canada
 Cabo Verde
 Central African Republic
 Chad
 Chile
 China
 Colombia
 Comoros
 Congo, Democratic Republic of the
 Congo, Republic of the
 Costa Rica
 Cote d'Ivoire
 Croatia
 Cuba
 Curacao
 Cyprus
 Czechia
 Denmark
 Djibouti
 Dominica
 Dominican Republic
 Timor-Leste
 Ecuador
 Egypt
 El Salvador
 Equatorial Guinea
 Eritrea
 Estonia
 Ethiopia
 Fiji
 Finland
 France
 Gabon
 Gambia, The
 Georgia
 Germany
 Ghana
 Greece
 Grenada
 Guatemala
 Guinea
 Guinea-Bissau
 Guyana
 Haiti
 Holy See
 Honduras
 Hong Kong
 Hungary
 Iceland
 India
 Indonesia
 Iran
 Iraq
 Ireland
 Israel
 Italy
 Jamaica
 Japan
 Jordan
 Kazakhstan
 Kenya
 Kiribati
 Korea, North
 Korea, South
 Kosovo
 Kuwait
 Kyrgyzstan
 Laos
 Latvia
 Lebanon
 Lesotho
 Liberia
 Libya
 Liechtenstein
 Lithuania
 Luxembourg
 Macau
 Macedonia
 Madagascar
 Malawi
 Malaysia
 Maldives
 Mali
 Malta
 Marshall Islands
 Mauritania
 Mauritius
 Mexico
 Micronesia
 Moldova
 Monaco
 Mongolia
 Montenegro
 Morocco
 Mozambique
 Namibia
 Nauru
 Nepal
 Netherlands
 New Zealand
 Nicaragua
 Niger
 Nigeria
 North Korea
 Norway
 Oman
 Pakistan
 Palau
 Palestinian Territories
 Panama
 Papua New Guinea
 Paraguay
 Peru
 Philippines
 Poland
 Portugal
 Qatar
 Romania
 Russia
 Rwanda
 Saint Kitts and Nevis
 Saint Lucia
 Saint Vincent and the Grenadines
 Samoa
 San Marino
 Sao Tome and Principe
 Saudi Arabia
 Senegal
 Serbia
 Seychelles
 Sierra Leone
 Singapore
 Sint Maarten
 Slovakia
 Slovenia
 Solomon Islands
 Somalia
 South Africa
 South Korea
 South Sudan
 Spain
 Sri Lanka
 Sudan
 Suriname
 Swaziland
 Sweden
 Switzerland
 Syria
 Taiwan
 Tajikistan
 Tanzania
 Thailand
 Timor-Leste
 Togo
 Tonga
 Trinidad and Tobago
 Tunisia
 Turkey
 Turkmenistan
 Tuvalu
 Uganda
 Ukraine
 United Arab Emirates
 United Kingdom
 Uruguay
 Uzbekistan
 Vanuatu
 Venezuela
 Vietnam
 Yemen
 Zambia
 Zimbabwe
 */

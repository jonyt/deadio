package org.deadio;

import org.deadio.crawling.Crawler;
import org.deadio.crawling.Observation;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.deadio.crawling.Crawler.getMapFromCSV;

/**
 * Created by yoni on 18/12/16.
 */
public class Database implements AutoCloseable {
    private final Connection connection;

    public Database(String dbFilename) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilename);
    }

    public double getLifeExpectancy(String country, String gender, int age) throws SQLException {
        Double lifeExpectancy = null;
        try (Statement statement = connection.createStatement()){
            String query = String.format(
                    "SELECT lifeExpectancy FROM data WHERE country = \"%s\" AND gender = \"%s\" AND %d >= minAge AND %d <= maxAge",
                    country, gender, age
            );
            ResultSet rs = statement.executeQuery(query);
            while(rs.next())
                lifeExpectancy = rs.getDouble(0);
        }

        if (lifeExpectancy == null)
            throw new IllegalArgumentException("Could not find life expectancy for country " + country + ", gender " + gender + ", age " + age);

        return lifeExpectancy;
    }

    public void createTable() throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate("DROP TABLE IF EXISTS data");
            statement.executeUpdate("CREATE TABLE data (country string, gender string, minAge integer, maxAge integer, lifeExpectancy double)");
        }
    }

    public void insertData(List<Observation> observations, Map<String, String> codesToCountries) throws SQLException {
        try (Statement statement = connection.createStatement()){
            for (int i = 0; i < observations.size(); i++) {
                Observation observation = observations.get(i);
                String country = codesToCountries.get(observation.getCountryCode());
                statement.executeUpdate(
                        String.format(
                                "INSERT INTO data VALUES(\"%s\", \"%s\", %d, %d, %f)",
                                country,
                                observation.getGender(),
                                observation.getMinAge(),
                                observation.getMaxAge(),
                                observation.getLifeExpectancy()
                        )
                );
            }
        }
        checkInsertions();
    }

    private void checkInsertions() throws SQLException {
        try (Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM data");
            while(rs.next())
                System.out.println("Inserted " + rs.getInt(1) + " lines");
        }
    }

    @Override
    public void close() throws Exception {
        try
        {
            if(connection != null)
                connection.close();
        }
        catch(SQLException e)
        {
            // connection close failed.
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        File countryCodesFile = new File(Crawler.class.getClassLoader().getResource("country_codes.csv").getFile());
        Map<String, String> countryCodes = getMapFromCSV(countryCodesFile.getAbsolutePath());
        Crawler crawler = new Crawler("/tmp/cache");
        try (Database database = new Database("lifeExpectancy.db")){
            database.createTable();
            for (Map.Entry<String, String> codeToCountry: countryCodes.entrySet()) {
                System.out.println("Inserting data for " + codeToCountry.getValue());
                List<Observation> observations = crawler.getData(codeToCountry.getKey(), true);
                if (observations.isEmpty())
                    continue;
                int maxYear = observations.stream().mapToInt(Observation::getYear).max().getAsInt();
                List<Observation> latestObservations = observations
                                                            .stream()
                                                            .filter(observation -> observation.getYear() == maxYear)
                                                            .collect(Collectors.toList());
                database.insertData(latestObservations, countryCodes);
            }
        }
    }
}

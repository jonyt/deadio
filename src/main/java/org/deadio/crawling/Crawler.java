package org.deadio.crawling;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yoni on 16/12/16.
 */
public class Crawler {
    private final String cacheDir;

    public Crawler(String cacheDir){
        this.cacheDir = cacheDir;
        new File(cacheDir).mkdirs();
    }

    public List<Observation> getData(String countryCode, boolean shouldParse) throws Exception {
        Path cachedResponseFilePath = Paths.get(cacheDir, generateFilename(countryCode));
        String xml;
        if (cachedResponseFilePath.toFile().exists()){
            System.out.println("File exists");
            xml = new String(Files.readAllBytes(cachedResponseFilePath));
        } else {
            String url = generateUrl(countryCode);
            System.out.println("Downloading [" + url + "]");
            HttpResponse<String> response = Unirest.get(url).asString();
            if (response.getStatus() != 200)
                throw new Exception("Request unsuccessful: " + response.getStatusText());
            xml = response.getBody();
            try (PrintWriter writer = new PrintWriter(cachedResponseFilePath.toFile())){
                writer.write(xml);
            }
        }

        if (shouldParse)
            return parseResponse(countryCode, xml);
        else
            return null;
    }

    private String generateFilename(String countryCode){
        return countryCode + ".xml";
    }

    private String generateUrl(String countryCode){
        return String.format(
                "http://apps.who.int/gho/athena/data/GHO/LIFE_0000000035?filter=COUNTRY:%s&x-sideaxis=GHO;AGEGROUP&x-topaxis=YEAR;SEX",
                countryCode.toUpperCase()
        );
    }

    public List<Observation> parseResponse(String countryCode, String responseBody) throws ParserConfigurationException, IOException, SAXException, XPathExpressionException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        InputStream responseStream = new ByteArrayInputStream(responseBody.getBytes(StandardCharsets.UTF_8));
        Document doc = builder.parse(responseStream);

        XPath xpath = XPathFactory.newInstance().newXPath();
        XPathExpression observationXpath = xpath.compile("/GHO/Data/Observation");
        XPathExpression yearNodeXpath = xpath.compile("./Dim[@Category='YEAR']");
        XPathExpression sexNodeXpath = xpath.compile("./Dim[@Category='SEX']");
        XPathExpression ageGroupNodeXpath = xpath.compile("./Dim[@Category='AGEGROUP']");
        XPathExpression valueNodeXpath = xpath.compile("./Value/Display/text()");

        Object result = observationXpath.evaluate(doc, XPathConstants.NODESET);
        NodeList observationNodes = (NodeList) result;
        List<Observation> observations = new ArrayList<>();
        for (int i = 0; i < observationNodes.getLength(); i++) {
            Node currentNode = observationNodes.item(i);
            String yearString = getCodeAttributeValueFromNode(currentNode, yearNodeXpath);
            String sex = getCodeAttributeValueFromNode(currentNode, sexNodeXpath);
            String ageGroup = getCodeAttributeValueFromNode(currentNode, ageGroupNodeXpath);
            int year = Integer.parseInt(yearString);
            String gender = sexToGender(sex);
            AgeRange ageRange = new AgeRange(ageGroup);
            double value = getValueFromNode(currentNode, valueNodeXpath);
            Observation observation = new Observation(countryCode, year, ageRange.from, ageRange.to, gender, value);
            observations.add(observation);
        }

        return observations;
    }

    private String getCodeAttributeValueFromNode(Node node, XPathExpression xPathExpression) throws XPathExpressionException {
        Object result = xPathExpression.evaluate(node, XPathConstants.NODE);
        Node foundNode = (Node) result;

        return foundNode.getAttributes().getNamedItem("Code").getNodeValue();
    }

    private String sexToGender(String sex) throws IllegalArgumentException {
        if (sex.equals("MLE")){
            return "male";
        } else if (sex.equals("FMLE")){
            return "female";
        } else {
            throw new IllegalArgumentException("Unknown gender: " + sex);
        }
    }

    private double getValueFromNode(Node node, XPathExpression xPathExpression) throws XPathExpressionException {
        Object result = xPathExpression.evaluate(node, XPathConstants.NODE);
        Node foundNode = (Node) result;
        String value = foundNode.getNodeValue();

        return Double.parseDouble(value);
    }

    private class AgeRange {
        private final int from;
        private final int to;

        public AgeRange(String rangeString){
            if (rangeString.contains("PLUS")) {
                from = Integer.parseInt(rangeString.replace("AGE", "").replace("PLUS", ""));
                to = Integer.MAX_VALUE;
            } else if (rangeString.equals("AGELT1")) {
                from = 0;
                to = 1;
            } else {
                String[] ages = rangeString.replace("AGE", "").split("-");
                from = Integer.parseInt(ages[0]);
                to = Integer.parseInt(ages[1]);
            }
        }
    }

    public static Map<String, String> getMapFromCSV(final String filePath) throws IOException{
        Stream<String> lines = Files.lines(Paths.get(filePath));
        Map<String, String> resultMap = lines
                                            .map(line -> line.split(","))
                                            .collect(Collectors.toMap(line -> line[1], line -> line[0]));
        lines.close();

        return resultMap;
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        File countryCodesFile = new File(Crawler.class.getClassLoader().getResource("country_codes.csv").getFile());
        Map<String, String> countryCodes = getMapFromCSV(countryCodesFile.getAbsolutePath());
        Crawler crawler = new Crawler("/tmp/cache");
        countryCodes.keySet().forEach(code -> {
            try {
                System.out.println("Getting data for code " + code);
                crawler.getData(code, false);
            } catch (Exception e) {
                System.err.println("Error on code " + code);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}

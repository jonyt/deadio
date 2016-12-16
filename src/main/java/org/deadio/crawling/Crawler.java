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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoni on 16/12/16.
 */
public class Crawler {
    public List<Observation> getData(String countryCode) throws Exception {
        String url = generateUrl(countryCode);
        HttpResponse<String> response = Unirest.get(url).asString();
        if (response.getStatus() != 200)
            throw new Exception("Request unsuccessful: " + response.getStatusText());

        return parseResponse(countryCode, response.getBody());
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

    public static void main(String[] args) throws ParserConfigurationException, SAXException, XPathExpressionException, IOException {
        Path path = Paths.get("/tmp/garb.xml");
        String xml = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        Crawler crawler = new Crawler();
        List<Observation> observations = crawler.parseResponse("rrr", xml);
        String h = "rr";
    }
}

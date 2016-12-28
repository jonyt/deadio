package org.deadio;

import org.deadio.recognizers.BingSpeechRecognizer;
import org.deadio.tts.MaryTTS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by yoni on 19/12/16.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
               System.out.println("Goodbye!");
            }
        });

        String azureKey = System.getenv("AZURE-KEY");
        String deviceUuid = System.getenv("DEVICE-UUID");

        MaryTTS textToSpeech = new MaryTTS();
//        SphinxSpeechRecognizer sphinxSpeechRecognizer = new SphinxSpeechRecognizer();
        BingSpeechRecognizer bingSpeechRecognizer = new BingSpeechRecognizer(azureKey, deviceUuid);
        String speechFilepath = "/tmp/speech_recording.wav";
        Recorder recorder = new Recorder(speechFilepath);
        Database database = new Database("lifeExpectancy.db");

        while (true){
            String country;
            String ageString;
            String gender;

            String text = null;
            while (true){
                textToSpeech.speak("From which country are you?");
                recorder.record();
                text = bingSpeechRecognizer.recognize(new File(speechFilepath));
                logger.debug("Received string {}", text);
                text = Utils.verifyCountry(text);
                if (text == null)
                    textToSpeech.speak("Sorry, I didn't understand that");
                else
                    break;
            }
            country = text;
            logger.info("Recognized country: {}", country);

            // TODO: Enable restart of process
            while (true){
                textToSpeech.speak("How old are you?");
                recorder.record();
                text = bingSpeechRecognizer.recognize(new File(speechFilepath));
                logger.debug("Received string {}", text);
                text = Utils.verifyAge(text);
                if (text == null)
                    textToSpeech.speak("Sorry, I didn't understand that");
                else
                    break;
            }
            ageString = text;
            logger.info("Recognized age: {}", ageString);

            while (true){
                textToSpeech.speak("Are you male or female?");
                recorder.record();
                text = bingSpeechRecognizer.recognize(new File(speechFilepath));
                logger.debug("Received string {}", text);
                text = Utils.verifyGender(text);
                if (text == null)
                    textToSpeech.speak("Sorry, I didn't understand that");
                else
                    break;
            }
            gender = text;
            logger.info("Recognized gender: {}", gender);

            int age = Utils.numberToDigits(ageString);

            double lifeExpectancy = database.getLifeExpectancy(country, gender, age);
            textToSpeech.speak(Utils.getFinalMessage(lifeExpectancy));
            Thread.sleep(10000);
        }
    }
}
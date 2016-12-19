package org.deadio;

import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import org.deadio.recognizers.SphinxSpeechRecognizer;
import org.deadio.tts.MaryTTS;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

/**
 * Created by yoni on 19/12/16.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException, MaryConfigurationException, LineUnavailableException, IOException, SynthesisException {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
               System.out.println("Goodbye!");
            }
        });

        MaryTTS textToSpeech = new MaryTTS();
        SphinxSpeechRecognizer sphinxSpeechRecognizer = new SphinxSpeechRecognizer();
        String speechFilepath = "/tmp/speech_recording.wav";
        Recorder recorder = new Recorder(speechFilepath);

        while (true){
            textToSpeech.speak("Shall we begin?", null);
            recorder.record();
            String text = sphinxSpeechRecognizer.recognize(new File(speechFilepath));
            System.out.println("************************\n" + text + "\n**********************");
            Thread.sleep(1000);
        }
    }
}
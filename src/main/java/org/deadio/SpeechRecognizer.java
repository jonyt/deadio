package org.deadio;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoni on 12/12/16.
 */
public class SpeechRecognizer {
    private final Configuration sphinxConfiguration = new edu.cmu.sphinx.api.Configuration();;

    public SpeechRecognizer(){
        sphinxConfiguration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
        sphinxConfiguration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
        sphinxConfiguration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
    }

    public String recognize(File audioFilepath) throws IOException {
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(sphinxConfiguration);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            try (InputStream inputStream = new FileInputStream(audioFilepath)){
                recognizer.startRecognition(inputStream);
                SpeechResult result;
                while ((result = recognizer.getResult()) != null) {
                    List<WordResult> words = result.getWords();
                    stringBuilder.append( words.stream().map(word -> word.getWord().toString()).collect(Collectors.joining(" ")) );
                }
            }
        } finally {
            recognizer.stopRecognition();
        }

        return stringBuilder.toString();
    }
}

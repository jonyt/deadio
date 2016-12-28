package org.deadio.recognizers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This will listen, using a microphone, to the key phrase "let's go dead io" and return true if it recognizes it.
 * See https://wolfpaulus.com/journal/embedded/raspberrypi2-sr/ for instructions
 * See http://stackoverflow.com/questions/17132192/pocketsphinx-continuous-failed-to-open-audio-device if pocketsphinx fails with "Failed to open audio device(/dev/dsp)"
 * Basically all that needs to be done is install pulseaudio, libpulse-dev, osspd
 * Created by yoni on 28/12/16.
 */
public class PocketSphinxRecognizer {
    private static final Logger logger = LoggerFactory.getLogger(PocketSphinxRecognizer.class);
    private static final String KEY_PHRASE = "LET'S GO DEAD I O";
    private static final String MODEL_DIRECTORY = "/usr/local/share/pocketsphinx/model/en-us/en-us";
    private static final String DATA_FILES_DIRECTORY = "pocketsphinx";
    private static final String DICTIONARY_FILE = "4022.dic";
    private static final String LM_FILE = "4022.lm";

    /**
     * Verifies pocketsphinx_continuous is installed and the en-US model is in the expected directory.
     * @throws Exception
     */
    public PocketSphinxRecognizer() throws Exception {
        List<String> commandLineArgs = new ArrayList<String>(){{
            add("which");
            add("pocketsphinx_continuous");
        }};
        runProcess(commandLineArgs, (bufferedReader) -> {
            String line = bufferedReader.readLine();
                if (line == null)
                    throw new Exception("pocketsphinx is not installed");
        }, "Checking for pocketsphinx installation", "Error while checking for pocketsphinx installation");

        File modelDirectory = new File(MODEL_DIRECTORY);
        if (!modelDirectory.exists() || !modelDirectory.isDirectory())
            throw new Exception("Could not find model directory in " + MODEL_DIRECTORY);
    }

    /**
     * Blocks until key phrase is detected.
     * Issues pocketsphinx_continuous -hmm /usr/local/share/pocketsphinx/model/en-us/en-us -lm 4022.lm -dict 4022.dic -samprate 16000/8000/48000 -inmic yes
     *
     */
    public void waitForKeyPhrase() throws Exception {
        List<String> commandLineArgs = new ArrayList<>();
        commandLineArgs.add("pocketsphinx_continuous");
        commandLineArgs.add("-hmm");
        commandLineArgs.add(MODEL_DIRECTORY);
        commandLineArgs.add("-lm");
        commandLineArgs.add(LM_FILE);
        commandLineArgs.add("-dict");
        commandLineArgs.add(DICTIONARY_FILE);
        commandLineArgs.add("-samprate");
        commandLineArgs.add("16000/8000/48000");
        commandLineArgs.add("-inmic");
        commandLineArgs.add("yes");

        runProcess(commandLineArgs, (bufferedReader) -> {
            String line;
            while((line = bufferedReader.readLine()) != null && !line.contains(KEY_PHRASE))
                System.out.println(line);
        }, "Starting to listen for key phrase.", "Error while listening for key phrase");
    }

    private void runProcess(
            List<String> commandLineArgs,
            CheckedFunction<BufferedReader> outputHandler,
            String preRunMessage,
            String errorMessage
        ) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(commandLineArgs);
        String dataFilesDirectoryPath = PocketSphinxRecognizer.class.getClassLoader().getResource(DATA_FILES_DIRECTORY).getFile();
        processBuilder.directory(new File(dataFilesDirectoryPath));
        processBuilder.redirectErrorStream(true);

        Process process = null;
        IOException errorDuringExecution = null;

        logger.info(preRunMessage);

        try {
            process = processBuilder.start();
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()))){
                outputHandler.apply(bufferedReader);
            }
        } catch (IOException e) {
            errorDuringExecution = e;
            logger.error(errorMessage, e);
        } finally {
            if(process != null) {
                process.destroy();
            }
            if(errorDuringExecution != null) {
                throw errorDuringExecution;
            }
        }
    }

    @FunctionalInterface
    public interface CheckedFunction<T> {
        void apply(T t) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        PocketSphinxRecognizer recognizer = new PocketSphinxRecognizer();
        recognizer.waitForKeyPhrase();

        logger.info("Key phrase found!");
    }
}

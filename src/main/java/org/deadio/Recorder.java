package org.deadio;

import org.deadio.recognizers.SphinxSpeechRecognizer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoni on 12/12/16.
 * See http://digitalcardboard.com/blog/2009/08/25/the-sox-of-silence/ for some good advice on Sox.
 * Also, this works: /usr/bin/sox --bits 16 --encoding unsigned-integer --rate 16000 --channels 1 --default-device --endian little --buffer 16000 /tmp/recording.wav silence 1 0.1 10% 1 1.1 10%
 */
public class Recorder {
    private final String pathToSox = "/usr/bin/sox";
    private final String encoding = "unsigned-integer";
    private final int bits = 16;
    private final int sampleRateKHz = 16;
    private final int numChannels = 1;
    private final int numSecondsToRecord = 5;
    private final String noiseLevelPercentage = "10%";
    private final double leadingSilenceDetection = 0.3;
    private final double trailingSilenceDetection = 1.3;
    private final String outputFilepath = "/tmp/recording.wav";
    private final List<String> arguments = new ArrayList<>();

    public Recorder(){
        arguments.add(pathToSox);
        arguments.add("--bits");
        arguments.add(String.valueOf(bits));
        arguments.add("--encoding");
        arguments.add(encoding);
        arguments.add("--rate");
        arguments.add(String.valueOf(sampleRateKHz * 1000));
        arguments.add("--channels");
        arguments.add(String.valueOf(numChannels));
        arguments.add("--clobber");
        arguments.add("--default-device");
        arguments.add("--endian");
        arguments.add("little");
        arguments.add("--buffer");
        arguments.add(String.valueOf(sampleRateKHz * 1000 * numChannels * numSecondsToRecord));
        arguments.add(outputFilepath);
        arguments.add("silence");
        arguments.add("1");
        arguments.add(String.valueOf(leadingSilenceDetection));
        arguments.add(noiseLevelPercentage);
        arguments.add("1");
        arguments.add(String.valueOf(trailingSilenceDetection));
        arguments.add(noiseLevelPercentage);
    }

    public void record() throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder(arguments);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        IOException errorDuringExecution = null;

        System.out.println("Issuing:\n" + String.join(" ", processBuilder.command()));

        try {
            process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;
            while((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            errorDuringExecution = e;
            System.err.println(String.format("Error while running Sox. %s", e.getMessage()));
        } finally {
            if(process != null) {
                process.destroy();
            }
            if(errorDuringExecution != null) {
                throw errorDuringExecution;
            }
        }
    }

    public static void main(String[] args) throws IOException {
        SphinxSpeechRecognizer speechRecognizer = new SphinxSpeechRecognizer();

        Recorder recorder = new Recorder();
        recorder.record();

        String result = speechRecognizer.recognize(new File("/tmp/recording.wav"));
        System.out.println("Recognized: " + result);
    }
}

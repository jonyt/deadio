package org.deadio;

import ie.corballis.sox.WrongParametersException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yoni on 12/12/16.
 */
public class Recorder {
    private final String pathToSox = "/usr/bin/sox";
    private final String encoding = "unsigned-integer";
    private final int bits = 16;
    private final int sampleRateKHz = 16;
    private final int numChannels = 1;
    private final int numSecondsToRecord = 5;
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
        arguments.add("trim");
        arguments.add("0");
        arguments.add(String.valueOf(numSecondsToRecord));
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

    public static void main(String[] args) throws WrongParametersException, IOException {
        SpeechRecognizer speechRecognizer = new SpeechRecognizer();

        Recorder recorder = new Recorder();
        recorder.record();

        String result = speechRecognizer.recognize(new File("/tmp/recording.wav"));
        System.out.println("Recognized: " + result);





        //sox -b 32 -e unsigned-integer -r 96k -c 2 -d --clobber --buffer $((96000*2*3)) /tmp/soxrecording.wav trim 0 10
//        Sox sox = new Sox("/usr/bin/sox");
//        sox
//            .sampleRate(16000)
//            .encoding(SoXEncoding.UNSIGNED_INTEGER)
//            .bits(32)
//            .fileType(AudioFileFormat.WAV)
//            .outputFile("output.wav")
//            .argument("--default-device")
//            .execute();
    }
}

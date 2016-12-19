package org.deadio.tts;

import marytts.LocalMaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;
import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.MaryAudioUtils;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by yoni on 19/12/16.
 */
public class MaryTTS {
    private LocalMaryInterface mary = null;
    private static final int BUFFER_SIZE = 128000;

    public MaryTTS() throws MaryConfigurationException {
        try {
            mary = new LocalMaryInterface();
            mary.setVoice(Voice.getVoice(Locale.US, Voice.FEMALE).getName());
        } catch (MaryConfigurationException e) {
            System.err.println("Could not initialize MaryTTS interface: " + e.getMessage());
            throw e;
        }
    }

    public void speak(String inputText, String outputFilepath) throws SynthesisException, IOException, LineUnavailableException {
        try (AudioInputStream audio = mary.generateAudio(inputText)){
            playSound(audio);

            if (outputFilepath != null)
                writeAudioToFile(audio, outputFilepath);
        }
    }

    private void writeAudioToFile(AudioInputStream audioInputStream, String filepath){
        double[] samples = MaryAudioUtils.getSamplesAsDoubleArray(audioInputStream);
        try {
            MaryAudioUtils.writeWavFile(samples, filepath, audioInputStream.getFormat());
            System.out.println("Output written to " + filepath);
        } catch (IOException e) {
            System.err.println("Could not write to file: " + filepath + "\n" + e.getMessage());
        }
    }

    private void playSound(AudioInputStream audioInputStream) throws LineUnavailableException {
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try (SourceDataLine sourceLine = (SourceDataLine) AudioSystem.getLine(info)){
            sourceLine.open(audioFormat);
            sourceLine.start();

            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];
            while (nBytesRead != -1) {
                try {
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (nBytesRead >= 0) {
                    @SuppressWarnings("unused")
                    int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                }
            }

            sourceLine.drain();
        }
    }

    public static void main(String[] args) throws MaryConfigurationException, LineUnavailableException, IOException, SynthesisException {
        new MaryTTS().speak("Welcome to the world of speech synthesis!", null);
    }
}

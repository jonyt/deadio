package org.deadio.recognizers;

import java.io.File;

/**
 * Created by yoni on 15/12/16.
 */
public interface SpeechRecognizer {
    String recognize(File audioFilepath) throws Exception;
}

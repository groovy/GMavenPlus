package org.codehaus.gmavenplus.mojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.maven.plugin.logging.Log;

public class GroovycLogger implements Runnable {

    private final InputStream inputStream;
    private final Log log;
    private final LogTarget level;

    public GroovycLogger(InputStream inputStream, Log log, LogTarget level) {
        this.inputStream = inputStream;
        this.log = log;
        this.level = level;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                if (this.level == LogTarget.ERROR) {
                    this.log.error(line);

                } else {
                    this.log.info(line);
                }
            }
        } catch (IOException ioException) {
            this.log.error(ioException);
        }
    }

    public enum LogTarget {
        INFO,
        ERROR;
    }
}

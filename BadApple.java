import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class BadApple {
    static Thread thread1 = new Thread() {

        public void run() {
                String filename = "BadApple.wav"; // fill in file name here

                int EXTERNAL_BUFFER_SIZE = 524288;

                File soundFile = new File(filename);

                if (!soundFile.exists()) {
                    System.err.println("Wave file not found: " + filename);
                    return;
                }

                AudioInputStream audioInputStream = null;
                try {
                    audioInputStream = AudioSystem.getAudioInputStream(soundFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                AudioFormat format = audioInputStream.getFormat();

                SourceDataLine auline = null;

                // Describe a desired line
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                try {
                    auline = (SourceDataLine) AudioSystem.getLine(info);
                    // Opens the line with the specified format,
                    // causing the line to acquire any required
                    // system resources and become operational.
                    auline.open(format);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                // Allows a line to engage in data I/O
                auline.start();

                int nBytesRead = 0;
                byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

                try {
                    while (nBytesRead != -1) {
                        nBytesRead = audioInputStream.read(abData, 0, abData.length);
                        if (nBytesRead >= 0) {
                            // Writes audio data to the mixer via this source data line
                            // NOTE : A mixer is an audio device with one or more lines
                            auline.write(abData, 0, nBytesRead);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                } finally {
                    // Drains queued data from the line
                    // by continuing data I/O until the
                    // data line's internal buffer has been emptied
                    auline.drain();

                    // Closes the line, indicating that any system
                    // resources in use by the line can be released
                    auline.close();
                }
        }
    };

    static Thread thread2 = new Thread() {
        public void run() {
            try {
                FileReader reader = new FileReader("asciiart3.txt");
                Scanner input = new Scanner(reader);
                // String s = "[frame]";
                while (input.hasNext()) {
                    String img = input.next();
                    if (img.contains("[frame]")) {
                        Sleep();
                        Sleep();
                        System.out.print(String.format("\033[2J"));
                    } else {
                        System.out.println(img);
                    }
                }

                reader.close();
                input.close();
            } catch (Exception e) {
                // TODO: handle exception
            }

        }
    };


    public static void Sleep() throws InterruptedException {
        try {
            //Thread.sleep(37);
            Thread.sleep(18);
            
        } catch (InterruptedException e) {
            // Perform your exception handling
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        thread2.start();
        thread1.start();
        thread1.join();
        thread2.join();
    }
}
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class multithreadclass{  
    public static void main(String[] args) {

        String[] parts = args[0].split("::", 2);
        String name = parts[0], desc = parts[1];
        Mixer.Info myinfo = Arrays.stream(AudioSystem.getMixerInfo())
                            .filter(info -> info.getName().equals(name) && info.getDescription().equals(desc))
                            .findFirst()
                            .orElseThrow();
       try { //picks system default microphone
            Mixer mixer = AudioSystem.getMixer(myinfo);
            AudioFormat format = new AudioFormat(44100f, 16, 2, true, false);
            
            TargetDataLine micLine = AudioSystem.getTargetDataLine(format);
            micLine.open(format);
            micLine.start();
        
            
            SourceDataLine speakerLine = AudioSystem.getSourceDataLine(format, myinfo);
            speakerLine.open(format);
            speakerLine.start();
        
            byte[] buffer = new byte[4096];
            
        
            while (true) {
                int bytesRead = micLine.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    speakerLine.write(buffer, 0, bytesRead);
                }
            }
        } catch (Exception e) {
            //System.out.println(e);
        }
    }
}

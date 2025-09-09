import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Threaded extends Thread{ //used just for playlist mode!!
    private String homefolder;
    private int info;
    Threaded(String homefolder, int info){
        this.homefolder = homefolder;
        this.info = info;
    }

    public void run(){
        while(1==1){
            String[] fileList = filesindirectory(homefolder);
            for(int i = 0; i<fileList.length; i++){
                try {
                    File file = new File(homefolder+ "\\" + fileList[i]); 
                    AudioInputStream audiostream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip(AudioSystem.getMixerInfo()[info]);
                    clip.open(audiostream);
                    clip.start();
                    AudioFormat format = audiostream.getFormat();
                    long frames = audiostream.getFrameLength();
                    double durationInSeconds = (frames+0.0) / format.getFrameRate();
                    //System.out.println("file n." + i + " 's duration is " + durationInSeconds);
                    Thread.sleep(Double.valueOf(durationInSeconds*1000).longValue());
                    clip.stop();
                } catch (Exception e) {
                    System.out.println("Error " + e );
                }
                
            }
        }
    }
    public static String[] filesindirectory(String directoryPath) {

    File dir = new File(directoryPath);

    Collection<String> files  =new ArrayList<String>();

    if(dir.isDirectory()){
        File[] listFiles = dir.listFiles();

        for(File file : listFiles){
            if(file.isFile()) {
                files.add(file.getName());
            }
        }
    }

        return files.toArray(new String[]{});
    }
}

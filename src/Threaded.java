import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Threaded extends Thread{ //used for playlist mode
    private String homefolder;
    private int info;
    private SharedResource sharedsource;
    Threaded(String homefolder, int info, SharedResource sharedResource){
        this.homefolder = homefolder;
        this.info = info;
        this.sharedsource = sharedResource;
    }
    
    public void run(){
        Clip clip = null;
        Clip clip2 = null;
        while(1==1){
            String[] fileList = filesindirectory(homefolder); 
            for(int i = 0; i<fileList.length&&sharedsource.getFlag() == false; i++){
                try {
                    File file = new File(homefolder+ "/" + fileList[i]); 
                    AudioInputStream audiostream = AudioSystem.getAudioInputStream(file);
                    AudioInputStream audiostream2 = AudioSystem.getAudioInputStream(file);
                    clip = AudioSystem.getClip(AudioSystem.getMixerInfo()[info]);
                    clip2 = AudioSystem.getClip();
                    clip.open(audiostream);
                    clip2.open(audiostream2);
                    clip.start();
                    clip2.start();
                    AudioFormat format = audiostream2.getFormat();
                    long frames = audiostream2.getFrameLength();
                    double durationInSeconds = (frames+0.0) / format.getFrameRate();
                    //Thread.sleep(Double.valueOf(durationInSeconds*1000).longValue()); //old way of waiting
                    for(long y = 0; y<Double.valueOf(durationInSeconds*1000).longValue()&&sharedsource.getFlag() == false;y++){
                        Thread.sleep(1);
                        System.out.println("y has reached value " + y + " out of " + Double.valueOf(durationInSeconds*1000).longValue());
                    }
                    clip.stop();
                    clip2.stop();
                } catch (Exception e) {
                    System.out.println("Error " + e );
                }
                
            }
            clip.stop();
            clip2.stop();
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
class SharedResource{
    private volatile boolean QuitPlaylistMode = false;
    public void SetBoolTrue(){
        QuitPlaylistMode = true;
        //System.out.println("Variable was set true");
    }
    public void SetBoolFalse(){
        QuitPlaylistMode = false;
    }
    public boolean getFlag() {
        return QuitPlaylistMode;
    }
}

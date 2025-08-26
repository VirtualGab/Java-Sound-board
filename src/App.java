/*
-----------------------------------------------------------------------------------------------
                    _____  ________   ___   ___                     ______            _____
    \\    //    ||  |   |  ---||---   | |   | |     /\      ||     //-----    /\     |     |
     \\  //     ||  |__/      ||      | |   | |    //\\     ||     ||  ___   //\\    |____/
      \\//      ||  | \       ||      | |___| |   //__\\    ||     ||  | |  //__\\   |     \
       \/       ||  |  \      ||      |_______|  //    \\   ||___  \\__|_| //    \\  |_____/
-----------------------------------------------------------------------------------------------
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.io.*;
import javax.sound.sampled.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
public class App {
    static int[] compatiblemixers = new int[64]; //does anyone ever get to this many mixers?
    static String soundspath;
    public static void main(String[] args) throws Exception {
        Preferences loaded = new Preferences("", 0, true);
        Scanner myscanner = new Scanner(System.in);
        int info = 0;
        String homefolder = "";
        
        try (FileInputStream fis = new FileInputStream("prefs.bin");
            ObjectInputStream ois = new ObjectInputStream(fis)) {
            loaded = (Preferences) ois.readObject();
            //System.out.println("loaded read correctly from the file");
        } catch (Exception e) {
            System.out.println("First app boot");
        }
        if(loaded.firsttime){
            info = Selectmixerinfo(myscanner);
            homefolder = GetFileName();
        } else {
            info = loaded.selectedmixer;
            homefolder = loaded.thesoundspath;
            System.out.println("the sound folder is " + loaded.thesoundspath + " and the selected mixer is n. " + loaded.selectedmixer);
        }
        loaded.firsttime = false;
        loaded.thesoundspath = homefolder;
        loaded.selectedmixer = info;
        System.out.println("select action:\n1 - soundboard mode\n2 - playlist mode\n3 - edit selected folder\n4 - edit sound output device\n5 - How do I setup a VAD (virtual audio device)?\n6 - Quit application");
        int option = 0;
        try {
            option = myscanner.nextInt();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Insert a number please or an error will occur, crashing the application");
            myscanner.next();
            option = myscanner.nextInt();
        }
        
        switch(option){
            case 1:System.out.println("soundboard mode"); soundboardmode(myscanner, homefolder, info); break;
            case 2: SaveSettings(myscanner, loaded); System.out.println("To exit from playlist mode you must close the terminal window.\nDifferently from soundboard mode this mode will play all files in the directory you have provided");
            PlaylistMode(homefolder, info);
            break; 
            case 3: homefolder = GetFileName(); break;
            case 4: info = Selectmixerinfo(myscanner); break;
            case 5: System.out.println("This guide is sure to work on windows Idk for mac/linux. There are two ways to setup a VAD. Either:\n1. Download drivers from the internet; or\n2. Install from steam \"soundpad DEMO\" (and then uninstall it, if you want). That will create a new audio device, \"Steam Streaming Microphone\"\nThe VAD (or Steam Streaming Microphone) is a special Audio Device that lets you\n play audio through it and use it as a microphone.\nThen select the VAD as output device in this app once you restart it\n(if this explanation of VAD isn't accurate idc because my objective was to keep it simple)");myscanner.nextLine();myscanner.nextLine();break;
            default: System.out.println("Quit");
        }
        
        SaveSettings(myscanner, loaded);     
    }
    static void PlaylistMode(String homefolder, int info){
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
    static void soundboardmode(Scanner myscanner, String homefolder, int info){
        int answer = 0;
        do {
            String[] fileList = filesindirectory(homefolder); //elencare tutti i file per poi scegliere cosa riprodurre
            System.out.println("Select audio track to play (only .wav files!!).\nInsert 0 to quit the application");
            for(int i = 0; i<fileList.length;i++){
                System.out.println(i+1 + " - " + fileList[i]);
            }
            answer = myscanner.nextInt();
            if(answer!=0){
                try { //play sound
                    File file = new File(homefolder+ "\\" + fileList[answer-1]); 
                    AudioInputStream audiostream = AudioSystem.getAudioInputStream(file);
                    Clip clip = AudioSystem.getClip(AudioSystem.getMixerInfo()[info]);
                    clip.open(audiostream);
                    clip.start();
                    System.out.println("type \"stop\" to stop the audio that is currently playing and play another");
                    myscanner.nextLine();
                    myscanner.nextLine();
                    clip.stop();

                } catch (Throwable t) {
                    System.out.println("error " + t);
                }
            }
            
        } while (answer!=0);
        
    }
    static void SaveSettings(Scanner myscanner, Preferences loaded){
        myscanner.close();   
        try (FileOutputStream fos = new FileOutputStream("prefs.bin");
            ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(loaded);
            //System.out.println("Preferences written successfully");
        }catch (Exception e) {
            System.out.println("error " +e +" while saving preferences");
        }
    }
    public static int Selectmixerinfo(Scanner myscanner){
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
        System.out.println("Select a VAD (virtual audio device) to play in with soundboard");

        Compatible_Mixers();
        for(int i = 0; i<mixerInfo.length; i++){
            for(int h = 0; h<compatiblemixers.length; h++){
                if(compatiblemixers[h]==i&&i!=0){
                    System.out.println("numero " + i + " " + mixerInfo[i]); 
                }
            }
        }
        int answer = myscanner.nextInt(); 
        Mixer.Info info = mixerInfo[answer]; //Edit this number to select output 0 = Default

        System.out.println(String.format("Name [%s]\n", info.getName()));
        System.out.println(info.getDescription());
        return answer;
    }
    static void SetSoundspath(){
        System.out.println("Select a folder from which to play its sounds. Click cancel or the X to quit the application.");
        soundspath = GetFileName();
        System.out.println(soundspath);
        if(soundspath.equals(null))
            System.exit(0);
    }
    public static String GetFileName(){
        String filepath = "";

        JFrame frame = new JFrame("Click on this window");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true);
        // Make the frame always on top
        frame.setAlwaysOnTop(true);

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("C:/"));
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(chooser);
        if(result == JFileChooser.APPROVE_OPTION){
            File selectedFile = chooser.getSelectedFile();
            filepath = selectedFile.getAbsolutePath();
        }
        frame.dispose();
        return filepath;
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
    static void Compatible_Mixers(){
        Mixer.Info[] allMixers = AudioSystem.getMixerInfo();          
        File file = new File("pan.wav");    //this audio will never be heard, needed to verify compatible drivers, and without it the jar would be just 5 kilobytes!
        AudioInputStream audiostream;
        try {
            audiostream = AudioSystem.getAudioInputStream(file);
        } catch (Exception e) {
            audiostream = null;
        }
        int i = 0;
        int index = 0;
        for (Mixer.Info mi : allMixers) {
            i++;
            try {
                Clip clip = AudioSystem.getClip(mi);
                clip.open(audiostream);
                //System.out.println("this mixer is compatible: " + i);
                compatiblemixers[index] = i;
                index++;
            } catch (Exception e) {
                //Handle exception
            }
        } 
    } 
    
}

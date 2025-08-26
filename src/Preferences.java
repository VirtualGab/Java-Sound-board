import java.io.Serializable;
public class Preferences implements Serializable{
    public String thesoundspath;
    public int selectedmixer;
    public boolean firsttime = true;
    public Preferences(String soundspath, int mixer, boolean isfirsttime){
        this.thesoundspath = soundspath;
        this.selectedmixer = mixer;
        this.firsttime = isfirsttime;
    }
}

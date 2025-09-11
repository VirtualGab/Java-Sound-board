import java.io.Serializable;

public class Preferences implements Serializable{
    public String thesoundspath;
    public int selectedmixer;
    public boolean firsttime = true;
    public boolean recordvoice = false;
    public String savedmixername = null;
    public Preferences(String soundspath, int mixer, boolean isfirsttime, boolean recordvoice, String mixername){
        this.thesoundspath = soundspath;
        this.selectedmixer = mixer;
        this.firsttime = isfirsttime;
        this.recordvoice = recordvoice;
        this.savedmixername = mixername;
    }
}

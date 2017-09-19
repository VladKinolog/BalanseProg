package sample;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "preferences")
public class PrefListWrapper {

    private List preferen;

    @XmlElement (name = "pref")
    public List getPreferen() {
        return preferen;
    }

    public void setPreferen(List preferen) {
        this.preferen = preferen;
    }
}

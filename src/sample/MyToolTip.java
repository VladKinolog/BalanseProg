package sample;

import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;

public class MyToolTip extends Tooltip{

    MyToolTip (String setText, int font){
//        Tooltip tooltip = new Tooltip(setText);
        //tooltip.setText(setText);
        this.setText(setText);
        this.setFont(new Font(font));

    }
}

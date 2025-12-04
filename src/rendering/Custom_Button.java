package rendering;

import java.awt.Color;
import java.awt.Cursor;


import javax.swing.JButton;



public class Custom_Button extends JButton {


    public Custom_Button(String text ){
        super(text);
        this.setOpaque(true);
        this.setBackground(new Color(150,150,150) );
        this.setForeground(Color.white);


        this.setBorderPainted(false);      // Enlève la bordure lineaire
        this.setContentAreaFilled(true);
        this.setFocusPainted(false);

       
        
        
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

    }
    
}

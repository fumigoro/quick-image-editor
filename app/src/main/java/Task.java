package quick.image.editor;

import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;



public class Task{
    public Dimension rangeS;
    public Dimension rangeE;
    public int type;
    public JLabel label;
    public JButton deleteBtn;
    public JPanel panel;
    public boolean active = false;
    
    // public int id;
    Task(int rangeSW,int rangeSH,int rangeEW,int rangeEH,int type){
        rangeS = new Dimension(rangeSW,rangeSH);
        rangeE = new Dimension(rangeEW,rangeEH);
        if(rangeSW>rangeEW){
            rangeS.width = rangeEW;
            rangeE.width = rangeSW;
        }
        if(rangeSH>rangeEH){
            rangeS.height = rangeEH;
            rangeE.height = rangeSH;
        }

        label = new JLabel();
        deleteBtn = new JButton("削除");
        deleteBtn.setMargin(new Insets(0, 0, 0, 0));

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(230,30));
        panel.add(label,BorderLayout.WEST);
        panel.add(deleteBtn,BorderLayout.EAST);
        panel.setVisible(true);

        deleteBtn.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                panel.setVisible(false);
                System.out.println("delete");
            }
        });
    // System.out.println("完了");
        active = true;
    }

    public String getRangeString(){
        return "("+String.valueOf(rangeS.width)+","+String.valueOf(rangeS.height)+"),("+String.valueOf(rangeE.width)+","+String.valueOf(rangeE.height)+")";
    }

    public JPanel getGUI(){
        return panel;
    }


}
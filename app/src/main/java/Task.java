package quick.image.editor;

import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;



public class Task{
    public int type;
    public JLabel label;
    public JButton deleteBtn;
    public JPanel panel;
    public boolean active = false;

    //加工範囲の支点,縦横幅、加工の種類
    public int x,y,width,height;
    String type1 = "トリミング";
    String type2 = "ぼかし";
    /**
     * type
     * 1:トリミング
     * 2:ぼかし
     */
    
    // Task(int rangeSW,int rangeSH,int rangeEW,int rangeEH,int type){
    Task(){

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

    // public String getRangeS(){
    //     return "("+String.valueOf(rangeS.width)+","+String.valueOf(rangeS.height)+"),("+String.valueOf(rangeE.width)+","+String.valueOf(rangeE.height)+")";
    // }
    public String getRangeS(){
        return "("+String.valueOf(x)+","+String.valueOf(y)+"),("+String.valueOf(x+width)+","+String.valueOf(y+height)+")";
    }

    public JPanel getGUI(){
        return panel;
    }

    public void setType(int t) {
        this.type = t;
    }
    public void setRange(int x,int y,int w,int h) {

        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        // System.out.print(x);
        // System.out.print(",");
        // System.out.print(y);
        // System.out.print(",");
        // System.out.print(w);
        // System.out.print(",");
        // System.out.println(h);
    }


    public String getTypeS() {
        switch(type){
            case 1:
                return type1;
            case 2:
                return type2;
            default:
                // throw new Exception("Processing type is null.");
                return null;

        }
    }

    //加工を行うメソッド
    public BufferedImage run(BufferedImage img){
        // if(ximg.getWidth());
        // img.getHeight();
        switch(type){
            case 1://トリミング
                System.out.println("トリミング");


                if(x+width>img.getWidth()-1){
                    System.out.printf("W:%d => %d\n",width,img.getWidth()-x);
                    width = img.getWidth()-x;
                }
                if(y+height>img.getHeight()-1){
                    System.out.printf("H:%d => %d\n",height,img.getHeight()-y);
                    height = img.getHeight()-y;
                }
                // System.out.printf("%d,%d,%d,%d\n",x,y,x+width,y+height,mg.getHeight());
                img = img.getSubimage(x,y,width,height);
                return img;
            case 2://ぼかし
                System.out.println("ぼかし");
                return img;
            default:
                // throw new Exception("Processing type is null.");
                return null;
        }

    }

    /**加工に必要なパラメーターが揃っているか確認するメソッド */
    public boolean isReady(){
        if(!(x>=0) || !(y>=0) || !(width>=0) || !(height>=0)){
            return false;
        }
        if(!(type>=1)||!(type<=2)){
            return false;
        }
        return true;

    }

}
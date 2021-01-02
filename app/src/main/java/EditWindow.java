package quick.image.editor;


import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;



public class EditWindow extends JFrame{
    
    JFrame editFrame = new JFrame("Edit");
    int hoge=11112;
    EditWindow(){
        // System.out.println
        createEditWindow();
        // editFrame.drawImage(getClipboardImage(),0,0,null);
        JLabel picLabel = new JLabel(new ImageIcon(getClipboardImage()));
        editFrame.add(picLabel);
    }

    private void createEditWindow(){
        editFrame.setVisible(true);
    }

    public int getHoge(){
        return hoge;
    }

    /**
     * クリップボードから画像を取得する
     * 
     * @return BufferedImage 画像がない場合はnull
     */
    private BufferedImage getClipboardImage(){
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable data = clip.getContents(null);
        //取得したものが画像データである場合
        try {
            //buferedImageにキャストして代入
            BufferedImage img = (BufferedImage) clip.getData(DataFlavor.imageFlavor);
            return img;
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
    }

    // /**
    //  * インターフェイスWindowListenerによって実装が矯正されるメソッド
    //  * ウィンドウが閉じられたときに処理
    //  */
    // public void windowOpened(WindowEvent e){
    //     /* 使わない */
    // }
    
    // public void windowClosing(WindowEvent e){
    //     /* 使わない */
    // }
    
    // public void windowClosed(WindowEvent e){
    //     /* 処理したい内容をここに記述する */
    //     editFrame.setVisible(false);
    // }
    
    // public void windowIconified(WindowEvent e){
    //     /* 使わない */
    // }
    
    // public void windowDeiconified(WindowEvent e){
    //     /* 使わない */
    // }
    
    // public void windowActivated(WindowEvent e){
    //     /* 使わない */
    // }
    
    // public void windowDeactivated(WindowEvent e){
    //     /* 使わない */
    // }
}

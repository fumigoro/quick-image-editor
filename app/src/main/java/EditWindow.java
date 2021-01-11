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

public class EditWindow extends JFrame {
    // 画像エリアのサイズ
    final int CANVAS_W = 1060;
    final int CANVAS_H = 600;
    //左のUIエリアの幅
    final int MENU_W = 300;
    //画像
    BufferedImage image;
    //現在クリップボードに画像があるかのフラグ
    boolean isImage;


    JFrame editFrame;
    JPanel panel_image ,panel_side;


    EditWindow() {
        createEditWindow();
        
    }

    /**
     * ウィンドウを作成し、各種UIを作成する
     */
    private void createEditWindow() {
        //ウィンドウ作成
        editFrame = new JFrame("Edit");
        //位置とサイズを指定
        editFrame.setBounds(10, 10, CANVAS_W + MENU_W, CANVAS_H+50);
        panel_image = new JPanel();
        panel_image.setLayout(new BorderLayout());
        
        panel_side = new JPanel();
        panel_side.setPreferredSize(new Dimension(300, CANVAS_H+50));

        //クリップボードから画像取得してフレーム内に表示
        image = getClipboardImage();
        if(image!=null){
        JLabel picLabel = new JLabel(new ImageIcon(image));
        picLabel.setLayout(new BorderLayout());
        panel_image.add(picLabel,BorderLayout.CENTER);

        editFrame.add(panel_image,BorderLayout.WEST);
        editFrame.add(panel_side,BorderLayout.EAST);
        //可視化
        editFrame.setVisible(true);
        }
    }


    /**
     * クリップボードから画像を取得する
     * 
     * @return BufferedImage 画像がない場合はnull
     */
    private BufferedImage getClipboardImage() {
        //クリップボードの中身を取得
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable data = clip.getContents(null);
        // 取得したものが画像データである場合
        try {
            // buferedImageにキャストして代入
            BufferedImage img = (BufferedImage) clip.getData(DataFlavor.imageFlavor);
            //縮小後の縦横サイズを保持する変数
            int width = img.getWidth();
            int height = img.getHeight();
            // 用意している画像表示枠(imageW*imageH)を上回る場合に縮小する
            if (img.getHeight() > CANVAS_H) {
                height = CANVAS_H;
                width = (int) (height * img.getWidth() / img.getHeight());
            }
            if (img.getWidth() > CANVAS_W) {
                width = CANVAS_W;
                height = (int) (width * img.getHeight() / img.getWidth());
            }

            //画像がウィンドウに収まるように縮小
            //縮小を行っているgetScaledInstanceImageメソッドは戻り値がImageなので、Graphicsを使ってBufferedImageへの変換も行う
            BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bimg.createGraphics();
            g.drawImage(img.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING), 0, 0, null);
            g.dispose();
            isImage = true;
            return bimg;
        } catch (Exception e) {
            //エラーハンドリング(主にclip.getData()に対するクリップボードの中身が画像でない場合の例外処理)
            System.out.println("クリップボードの中身が画像ではありません");
            // e.printStackTrace();
            isImage=false;
            return null;
        }
    }

    //クリップボードに画像があるか確かめるメソッド
    public boolean isImage(){
        return isImage;
    }

    // /**
    // * インターフェイスWindowListenerによって実装が矯正されるメソッド
    // * ウィンドウが閉じられたときに処理
    // */
    // public void windowOpened(WindowEvent e){
    // /* 使わない */
    // }

    // public void windowClosing(WindowEvent e){
    // /* 使わない */
    // }

    // public void windowClosed(WindowEvent e){
    // /* 処理したい内容をここに記述する */
    // editFrame.setVisible(false);
    // }

    // public void windowIconified(WindowEvent e){
    // /* 使わない */
    // }

    // public void windowDeiconified(WindowEvent e){
    // /* 使わない */
    // }

    // public void windowActivated(WindowEvent e){
    // /* 使わない */
    // }

    // public void windowDeactivated(WindowEvent e){
    // /* 使わない */
    // }
}

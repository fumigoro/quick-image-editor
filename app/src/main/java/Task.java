package quick.image.editor;

import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class Task {
    public int type;
    public JLabel label;
    public JButton deleteBtn;
    public JPanel panel;
    public boolean active = false;

    // 加工範囲の支点,縦横幅、加工の種類
    int x, y, width, height;
    String type1 = "トリミング";
    String type2 = "ぼかし";
    // ぼかしの強度
    /** grad_size*2+1 でぼかしの畳込みを行う画素の横幅(=縦)になる*/
    int grad_size = 1;

    /**
     * type 1:トリミング 2:ぼかし
     */

    // Task(int rangeSW,int rangeSH,int rangeEW,int rangeEH,int type){
    Task() {

        label = new JLabel();
        deleteBtn = new JButton("削除");
        deleteBtn.setMargin(new Insets(0, 0, 0, 0));

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(230, 30));
        panel.add(label, BorderLayout.WEST);
        panel.add(deleteBtn, BorderLayout.EAST);
        panel.setVisible(true);

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(false);
                active = false;// このタスクを無効化
                System.out.println("delete");
            }
        });
        // System.out.println("完了");
        active = true;
    }

    // public String getRangeS(){
    // return
    // "("+String.valueOf(rangeS.width)+","+String.valueOf(rangeS.height)+"),("+String.valueOf(rangeE.width)+","+String.valueOf(rangeE.height)+")";
    // }
    public String getRangeS() {
        return "(" + String.valueOf(x) + "," + String.valueOf(y) + "),(" + String.valueOf(x + width) + ","
                + String.valueOf(y + height) + ")";
    }

    public JPanel getGUI() {
        return panel;
    }

    public void setType(int t) {
        this.type = t;
    }

    public void setRange(int x, int y, int w, int h) {

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
        switch (type) {
            case 1:
                return type1;
            case 2:
                return type2;
            default:
                // throw new Exception("Processing type is null.");
                return null;

        }
    }

    // 加工を行うメソッド
    public BufferedImage run(BufferedImage img) {
        // if(ximg.getWidth());
        // img.getHeight();
        switch (type) {
            case 1:// トリミング
                System.out.println("トリミング");

                if (x + width > img.getWidth() - 1) {
                    System.out.printf("W:%d => %d\n", width, img.getWidth() - x);
                    width = img.getWidth() - x;
                }
                if (y + height > img.getHeight() - 1) {
                    System.out.printf("H:%d => %d\n", height, img.getHeight() - y);
                    height = img.getHeight() - y;
                }
                // System.out.printf("%d,%d,%d,%d\n",x,y,x+width,y+height,mg.getHeight());
                img = img.getSubimage(x, y, width, height);
                return img;
            case 2:// ぼかし
                System.out.println("ぼかし");
                img = gradation(img);

                return img;
            default:
                // throw new Exception("Processing type is null.");
                return null;
        }

    }

    /** 加工に必要なパラメーターが揃っているか確認するメソッド */
    public boolean isReady() {
        if (!(x >= 0) || !(y >= 0) || !(width >= 0) || !(height >= 0)) {
            return false;
        }
        if (!(type >= 1) || !(type <= 2)) {
            return false;
        }
        return true;

    }

    private BufferedImage gradation(BufferedImage img) {
        // 画像の色の持ち方をチェック
        // if (BufferedImage.TYPE_3BYTE_BGR != img.getType()) {
        // System.out.println("対応していないカラーモデル");
        // System.out.println(img.getType());
        // System.out.println(BufferedImage.TYPE_3BYTE_BGR);

        // return null;
        // }

        // ぼかし処理
        // int grad_dx, grad_dy;
        // int grad_width, grad_height;
        int color, r, g, b;
        int sumr, sumg, sumb;
        int p;
        int newcolor;
        BufferedImage newimg = null;

        // 画像サイズの取得
        final int IMG_WIDTH = img.getWidth();
        final int IMG_HEIGHT = img.getHeight();

        // 指定範囲が画像サイズを超えていないか確認
        if (x + width > img.getWidth() - 1) {
            System.out.printf("W:%d => %d\n", width, img.getWidth() - x);
            width = img.getWidth() - x;
        }
        if (y + height > img.getHeight() - 1) {
            System.out.printf("H:%d => %d\n", height, img.getHeight() - y);
            height = img.getHeight() - y;
        }

        // 新しい画像を作成
        // 元の画像と同じ状態の画像を作成
        try {
            newimg = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, img.getType());
            Graphics2D g_newimg = newimg.createGraphics();
            g_newimg.drawImage(img, 0, 0, null);
            g_newimg.dispose();
        } catch (Exception e) {
            // 画像作成に失敗したときの処理
            e.printStackTrace();
            return null;
        }

        // ぼかし処理
        System.out.printf("%d,%d => %d,%d\n", this.x, this.y, this.x + this.width, this.y + this.height);
        for (int grad_y = this.y; grad_y < this.y + this.height; grad_y++) {
            for (int grad_x = this.x; grad_x < this.x + this.width; grad_x++) {
                if ((0 == grad_x) || (0 == grad_y) || (grad_x == (IMG_WIDTH - 1)) || (grad_y == (IMG_HEIGHT - 1))) {
                    // (grad_x,grad_y)が画像の外枠の場合、元の画像の色を取得
                    newcolor = img.getRGB(grad_x, grad_y);
                } else {
                    // ９画素の平均値を計算
                    sumr = 0; // Ｒ値の合計
                    sumg = 0; // Ｇ値の合計
                    sumb = 0; // Ｂ値の合計
                    for (int grad_dy = -1; grad_dy <= 1; grad_dy++) {
                        for (int grad_dx = -1; grad_dx <= 1; grad_dx++) {
                            // (grad_x,grad_y)の色を取得
                            color = img.getRGB(grad_x + grad_dx, grad_y + grad_dy);

                            // 色をr,g,bに分解
                            r = (color >> 16) & 0xff;
                            g = (color >> 8) & 0xff;
                            b = color & 0xff;

                            //
                            sumr += r;
                            sumg += g;
                            sumb += b;
                        }
                    }
                    sumr /= 9; // Ｒ値の平均
                    sumg /= 9; // Ｇ値の平均
                    sumb /= 9; // Ｂ値の平均

                    // r,g,bの色を合成
                    newcolor = (sumr << 16) + (sumg << 8) + sumb;
                }

                // 新しい色を(grad_x,grad_y)に設定
                newimg.setRGB(grad_x, grad_y, newcolor);
            }
        }
        return newimg;
    }

}
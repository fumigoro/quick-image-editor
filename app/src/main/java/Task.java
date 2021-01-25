package quick.image.editor;

import java.awt.Dimension;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.Math;

public class Task{
    public int type;
    public JLabel label;
    public JButton deleteBtn;
    public JPanel panel;
    public boolean active = false;
    // public JProgressBar pb;


    // 加工範囲の支点,縦横幅、加工の種類
    int x, y, width, height;
    String type1 = "トリミング";
    String type2 = "ぼかし";
    // ぼかしの強度
    /** grad_size*2+1 でぼかしの畳込みを行う画素の横幅(=縦)になる */
    int grad_size = 3;
    final int GRAD_SIZE_MAX = 20;

    /**
     * type 1:トリミング 2:ぼかし
     */

    // Task(int rangeSW,int rangeSH,int rangeEW,int rangeEH,int type){
    Task() {

        // pb = new JProgressBar();
        // pb.setValue(0);
        // pb.setPreferredSize(new Dimension(220, 20));

        this.label = new JLabel();
        this.deleteBtn = new JButton("削除");
        deleteBtn.setMargin(new Insets(0, 0, 0, 0));

        this.panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(230, 30));
        panel.add(this.label, BorderLayout.WEST);
        panel.add(this.deleteBtn, BorderLayout.EAST);
        // panel.add(this.pb, BorderLayout.SOUTH);
        panel.setVisible(true);

        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                panel.setVisible(false);
                active = false;// このタスクを無効化
                // System.out.println("delete");
            }
        });
        // System.out.println("完了");
        this.active = true;
    }

    // public String getRangeS(){
    // return
    // "("+String.valueOf(rangeS.width)+","+String.valueOf(rangeS.height)+"),("+String.valueOf(rangeE.width)+","+String.valueOf(rangeE.height)+")";
    // }
    public String getRangeS() {
        return "(" + String.valueOf(this.x) + "," + String.valueOf(this.y) + ")=>("
                + String.valueOf(this.x + this.width) + "," + String.valueOf(this.y + this.height) + ")";
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
        switch (this.type) {
            case 1:
                return this.type1;
            case 2:
                return this.type2;
            default:
                // throw new Exception("Processing type is null.");
                return null;

        }
    }

    // 加工を行うメソッド
    public BufferedImage run(BufferedImage img) {
        // if(ximg.getWidth());
        // img.getHeight();
        switch (this.type) {
            case 1:// トリミング
                   // System.out.println("トリミング");

                if (this.x + this.width > img.getWidth() - 1) {
                    // System.out.printf("W:%d => %d\n", this.width, img.getWidth() - this.x);
                    this.width = img.getWidth() - x;
                }
                if (this.y + this.height > img.getHeight() - 1) {
                    // System.out.printf("H:%d => %d\n", this.height, img.getHeight() - this.y);
                    this.height = img.getHeight() - y;
                }
                // System.out.printf("%d,%d,%d,%d\n",x,y,x+width,y+height,mg.getHeight());
                img = img.getSubimage(this.x, this.y, this.width, this.height);
                return img;
            case 2:// ぼかし
                   // System.out.println("ぼかし");
                img = gradation(img);

                return img;
            default:
                // throw new Exception("Processing type is null.");
                return null;
        }

    }

    // ぼかし具合を0~100で表した値を受け取り、セットする
    public void setGradSize(int size) {
        // 0~100で来るので最大値の割合として換算し代入
        // System.out.printf("this.grad_size:%d\n",this.grad_size);
        this.grad_size = (int) ((double) this.GRAD_SIZE_MAX * ((double) size / 100.0));
        // System.out.printf("this.grad_size:%d\n",this.grad_size);
        // System.out.printf("size:%d\n",size);

    }

    public String getGradSizeAsString() {
        return String.valueOf(this.grad_size);
    }

    /** 加工に必要なパラメーターが揃っているか確認するメソッド */
    public boolean isReady() {
        if (!(this.x >= 0) || !(this.y >= 0) || !(this.width >= 0) || !(this.height >= 0)) {
            return false;
        }
        if (!(this.type >= 1) || !(this.type <= 2)) {
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
        if (this.x + this.width > img.getWidth() - 1) {
            // System.out.printf("W:%d => %d\n", this.width, img.getWidth() - this.x);
            this.width = img.getWidth() - this.x;
        }
        if (this.y + this.height > img.getHeight() - 1) {
            // System.out.printf("H:%d => %d\n", this.height, img.getHeight() - this.y);
            this.height = img.getHeight() - this.y;
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
        // System.out.printf("%d,%d => %d,%d\n", this.x, this.y, this.x + this.width,
        // this.y + this.height);
        double progress_tmp1 = 0;

        // pb.setValue(0);
        for (int grad_y = this.y; grad_y < this.y + this.height; grad_y++) {
            progress_tmp1 = ((double) (grad_y - this.y) / (double) (this.height) * 100.0);
            // pb.setValue((int) progress_tmp1);
            label.setText(String.valueOf(progress_tmp1));
            System.out.println((int) progress_tmp1);
            
                for (int grad_x = this.x; grad_x < this.x + this.width; grad_x++) {
                    // if (((grad_x - grad_size < 0) || (grad_y - grad_size < 0)) || (grad_x +
                    // grad_size >= IMG_WIDTH) || (grad_y + grad_size >= IMG_HEIGHT)) {
                    // 畳み込みの範囲が画像から飛び出す場合、元の画像の色をそのまま取得
                    // newcolor = img.getRGB(grad_x, grad_y);

                    // (grad_size*2+1)^2 画素の平均値を計算
                    sumr = 0; // Ｒ値の合計
                    sumg = 0; // Ｇ値の合計
                    int grad_dx2;
                    int grad_dy2;
                    sumb = 0; // Ｂ値の合計

                    for (int grad_dy = -1 * grad_size; grad_dy <= grad_size; grad_dy++) {
                        for (int grad_dx = -1 * grad_size; grad_dx <= grad_size; grad_dx++) {
                            grad_dx2 = grad_dx;
                            grad_dy2 = grad_dy;
                            // 左端からはみ出る時
                            if (grad_x + grad_dx < 0) {
                                grad_dx2 = grad_dx * (-1);
                            }
                            // 右端からはみ出るとき
                            if (grad_x + grad_dx >= IMG_WIDTH) {
                                grad_dx2 = grad_dx * (-1);
                            }
                            // 上からはみ出る時
                            if (grad_y + grad_dy < 0) {
                                grad_dy2 = grad_dy * (-1);
                            }
                            // 下からはみ出る時
                            if (grad_y + grad_dy >= IMG_HEIGHT) {
                                grad_dy2 = grad_dy * (-1);
                            }
                            // (grad_x,grad_y)の色を取得
                            color = img.getRGB(grad_x + grad_dx2, grad_y + grad_dy2);

                            // 色をr,g,bに分解
                            r = (color >> 16) & 0xff;
                            g = (color >> 8) & 0xff;
                            b = color & 0xff;

                            //
                            sumr += r;
                            sumg += g;
                            sumb += b;
                            // System.out.println((double)(+(grad_dx+grad_size)*(grad_dy+grad_size))/(double)(this.height*this.width*grad_size));
                            // pb.setValue((int)((double)(grad_x*grad_y*grad_size+(grad_dx+grad_size)*(grad_dy+grad_size)*100)/(double)(this.height*this.width*grad_size)));
                        }
                    }
                    sumr /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｒ値の平均
                    sumg /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｇ値の平均
                    sumb /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｂ値の平均

                    // r,g,bの色を合成
                    newcolor = (sumr << 16) + (sumg << 8) + sumb;

                    // } else {
                    // // (grad_size*2+1)^2 画素の平均値を計算
                    // sumr = 0; // Ｒ値の合計
                    // sumg = 0; // Ｇ値の合計
                    // sumb = 0; // Ｂ値の合計
                    // for (int grad_dy = -1*grad_size; grad_dy <= grad_size; grad_dy++) {
                    // for (int grad_dx = -1*grad_size; grad_dx <= grad_size; grad_dx++) {
                    // // (grad_x,grad_y)の色を取得
                    // color = img.getRGB(grad_x + grad_dx, grad_y + grad_dy);

                    // // 色をr,g,bに分解
                    // r = (color >> 16) & 0xff;
                    // g = (color >> 8) & 0xff;
                    // b = color & 0xff;

                    // //
                    // sumr += r;
                    // sumg += g;
                    // sumb += b;
                    // }
                    // }
                    // sumr /= (int) Math.pow(grad_size*2+1,2); // Ｒ値の平均
                    // sumg /= (int) Math.pow(grad_size*2+1,2); // Ｇ値の平均
                    // sumb /= (int) Math.pow(grad_size*2+1,2); // Ｂ値の平均

                    // // r,g,bの色を合成
                    // newcolor = (sumr << 16) + (sumg << 8) + sumb;
                    // }

                    // 新しい色を(grad_x,grad_y)に設定
                    newimg.setRGB(grad_x, grad_y, newcolor);
                    }

        }
        // pb.setValue(100);
        return newimg;
    }

}
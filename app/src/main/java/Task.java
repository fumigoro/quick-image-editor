package quick.image.editor;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.image.*;
import java.lang.Math;

import javax.swing.*;

public class Task {
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
    public static final int GRAD_SIZE_MAX = 20;

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

    /**
     * 加工範囲を表す文字列を返すメソッド
     * 
     * @return String 加工範囲(x1,y1)=>(x2,y2) x1,y1とx2,y2の2点で張られる長方形が加工範囲
     */
    public String getRangeS() {
        return "(" + String.valueOf(this.x) + "," + String.valueOf(this.y) + ")=>("
                + String.valueOf(this.x + this.width) + "," + String.valueOf(this.y + this.height) + ")";
    }

    /**
     * メイン画面に表示する加工内容を示すGUI要素を返すメソッド
     * 
     * @return JPannel 加工範囲を示すラベル、削除ボタンが入ったJPanel
     * 
     */
    public JPanel getGUI() {
        return panel;
    }

    /**
     * 加工の種類(トリミング,ぼかし)を設定するメソッド
     * 
     * @param t 加工内容を示す数字 1:トリミング 2:ぼかし
     */
    public void setType(int t) {
        this.type = t;
    }

    /**
     * 受け取った数字から加工範囲を設定するメソッド
     * 
     * @param x 始点のx座標
     * @param y 始点のy座標
     * @param w 加工範囲の幅
     * @param h 高さ
     */
    public void setRange(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
    }

    /**
     * 加工内容を示す文字列を返すメソッド
     * 
     * @return type 'トリミング'または'ぼかし' 加工内容未設定の場合はnull
     */
    public String getTypeS() {
        switch (this.type) {
            case 1:
                return this.type1;
            case 2:
                return this.type2;
            default:
                return null;
        }
    }

    // 設定したパラメーターに基づき加工を行うメソッド
    /**
     * 
     * @param img 加工する画像
     * @return 加工後の画像
     */
    public BufferedImage run(BufferedImage img) {

        // 加工内容を設定する画面では画像がウィンドウサイズを超える場合、縮小して表示しており設定される加工範囲の座標も縮小後の画像に基づいたスケールとなる
        // それをもとのスケールに戻した際に多少の誤差が発生し加工範囲が画像のサイズを超える場合の修正処理
        if (this.x + this.width > img.getWidth() - 1) {
            this.width = img.getWidth() - x;
        }
        if (this.y + this.height > img.getHeight() - 1) {
            this.height = img.getHeight() - y;
        }
        switch (this.type) {
            case 1:// トリミング
                   // System.out.println("トリミング");
                img = img.getSubimage(this.x, this.y, this.width, this.height);
                return img;
            case 2:// ぼかし
                   // System.out.println("ぼかし");
                img = gradation(img);
                return img;
            default:
                return null;
        }

    }

    // ぼかし具合を0~100で表した値を受け取り、セットする
    /**
     * 
     * @param size ぼかしの程度(0~100)
     */
    public void setGradSize(int size) {
        // 0~100で来るので最大値の割合として換算し代入
        this.grad_size = (int) ((double) this.GRAD_SIZE_MAX * ((double) size / 100.0));
    }

    /**
     * ぼかしのサイズを文字列型で返すメソッド GUIに表示する際に使用する
     * 
     * @return ぼかしのサイズ
     */
    public String getGradSizeAsString() {
        return String.valueOf(this.grad_size);
    }

    /**
     * 加工に必要なパラメーターが揃っているか確認するメソッド
     * 
     * @return 揃っているか否かの真理値
     */
    public boolean isReady() {
        // 加工範囲が未設定の場合falseを返す
        if (!(this.x >= 0) || !(this.y >= 0) || !(this.width >= 0) || !(this.height >= 0)) {
            return false;
        }
        // 加工の種類が未設定の場合falseを返す
        if (!(this.type >= 1) || !(this.type <= 2)) {
            return false;
        }
        // 両方設定済みならtrue
        return true;

    }

    /**
     * ぼかし処理を行うメソッド
     * 
     * @param img 加工する画像
     * @return 加工後の画像
     */
    private BufferedImage gradation(BufferedImage img) {

        int color, r, g, b; // 各画素の加工前の色情報(0~255)を保持
        int sumr, sumg, sumb; // 畳み込み範囲の全画素の各色の合計値
        int newcolor; // 各画素の加工後の色情報を保持
        BufferedImage newimg = null; // 加工後の画像

        // 画像サイズの取得
        final int IMG_WIDTH = img.getWidth();
        final int IMG_HEIGHT = img.getHeight();

        try {
            // 新しい画像を作成しnewImgへ
            // 一旦元の画像と同じ状態の画像を作成
            newimg = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, img.getType());
            Graphics2D g_newimg = newimg.createGraphics();
            g_newimg.drawImage(img, 0, 0, null);
            g_newimg.dispose();
        } catch (Exception e) {
            // 画像作成に失敗したときの処理
            e.printStackTrace();
            return null;
        }

        // ぼかし処理の完了割合(実装予定のプログレスバー用)
        double progress_tmp1 = 0;

        // pb.setValue(0);
        for (int grad_y = this.y; grad_y < this.y + this.height; grad_y++) {
            // 進捗割合を計算
            progress_tmp1 = ((double) (grad_y - this.y) / (double) (this.height) * 100.0);
            // pb.setValue((int) progress_tmp1);
            System.out.println((int) progress_tmp1);// 加工の進捗割合を表示

            for (int grad_x = this.x; grad_x < this.x + this.width; grad_x++) {

                sumr = 0; // Ｒ値の合計
                sumg = 0; // Ｇ値の合計
                int grad_dx2;
                int grad_dy2;
                sumb = 0; // Ｂ値の合計

                for (int grad_dy = -1 * grad_size; grad_dy <= grad_size; grad_dy++) {
                    for (int grad_dx = -1 * grad_size; grad_dx <= grad_size; grad_dx++) {
                        // 畳み込み範囲がはみ出る場合に参照する画素の座標(grad_x,grad_y)を修正するが、
                        // for文のインクリメントで使用しており上書きしたくないため、調整後用の変数を別途用意
                        grad_dx2 = grad_dx;
                        grad_dy2 = grad_dy;
                        // 畳み込みの範囲が画像から飛び出す場合、画像の端の線に対して線対称な位置の画素を用いて計算
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
                        // (grad_x + grad_dx2, grad_y + grad_dy2)の色を取得
                        color = img.getRGB(grad_x + grad_dx2, grad_y + grad_dy2);

                        // 色をr,g,bに分解(シフト演算)
                        r = (color >> 16) & 0xff;
                        g = (color >> 8) & 0xff;
                        b = color & 0xff;

                        // 合計計算用変数に加算
                        sumr += r;
                        sumg += g;
                        sumb += b;
                    }
                }

                // 畳み込み範囲の画素の平均値を色ごとに計算
                // MEMO:Math.powは累乗計算
                sumr /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｒの平均
                sumg /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｇの平均
                sumb /= (int) Math.pow(grad_size * 2 + 1, 2); // Ｂの平均

                // r,g,bの色を合成(シフト演算)
                newcolor = (sumr << 16) + (sumg << 8) + sumb;

                // カラーモデルが4BYTE_ABGRの場合、右から7,8ビット目のアルファ値を255にして透明にならないよう対策
                if (BufferedImage.TYPE_4BYTE_ABGR == img.getType()) {
                    newcolor += 0xff000000;
                }

                // 新しい色を座標(grad_x,grad_y)の画素に設定
                newimg.setRGB(grad_x, grad_y, newcolor);
            }
        }
        // pb.setValue(100);
        return newimg;
    }
}

package quick.image.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.*;
import javax.swing.JSlider;
import javax.swing.border.*;
import javax.swing.event.*;

public class EditWindow extends JFrame {
    // 画像エリアのサイズ
    final int CANVAS_W = 1500;
    final int CANVAS_H = 800;
    // 左のUIエリアの幅
    final int MENU_W = 300;
    // 画像
    BufferedImage image;
    // 現在クリップボードに画像があるかのフラグ
    boolean isImage;
    // 読み込んだ画像のサイズ
    int scaledWidth;
    int scaledHeight;
    // 縮小した倍率
    double scale;
    // ぼかしのサイズ
    int sliderValue = 50;

    // 自分自身がインスタンス化されているかかのフラグ
    // 2つめのコンストラクタによって編集ウィンドウが立ち上げられていないのに裏でインスタンス化する場合があるため、
    // どちらのコンストラクタでインスタンス化されたかの判別に使用
    boolean isInstantiated;

    // UI部品
    EditCanvas canvas;
    JFrame editFrame;
    JPanel panel_image, panel_side;
    JButton btn_addTrim, btn_addGrad;
    JLabel picLabel;
    JSlider grad_slider;
    JLabel sliderLabel;

    /** 編集ウィンドウが立ち上げられたときに呼ばれるコンストラクタ */
    EditWindow() {
        createEditWindow();
        isInstantiated = true;
    }

    /** MainWindow起動時にリスナー設定で必要なため、UI表示を行わずインスタンス化するときに使う特別なコンストラクタ */
    EditWindow(boolean dummy) {
        isInstantiated = false;
    }

    /**
     * ウィンドウを作成し、各種UIを作成する
     */
    private void createEditWindow() {
        // ウィンドウ作成
        editFrame = new JFrame("タスクの追加");
        // 位置とサイズを指定
        editFrame.setBounds(50, 50, CANVAS_W + MENU_W, CANVAS_H + 100);
        panel_image = new JPanel();
        panel_image.setLayout(new BorderLayout());
        panel_image.setPreferredSize(new Dimension(CANVAS_W, CANVAS_H + 50));

        panel_image.setBackground(Color.WHITE);

        panel_side = new JPanel();
        panel_side.setPreferredSize(new Dimension(MENU_W, CANVAS_H + 50));

        JPanel panel_side1 = new JPanel();
        Border lineBorder_1 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_1 = BorderFactory.createTitledBorder(lineBorder_1, "トリミング");
        panel_side1.setBorder(titledBorder_1);

        btn_addTrim = new JButton("トリミング");
        btn_addTrim.setBackground(new Color(255, 255, 255));
        btn_addTrim.setPreferredSize(new Dimension(MENU_W - 50, 50));
        panel_side1.add(btn_addTrim);

        JPanel panel_side2 = new JPanel();
        panel_side2.setLayout(new BorderLayout());
        Border lineBorder_2 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_2 = BorderFactory.createTitledBorder(lineBorder_2, "ぼかし");
        panel_side2.setBorder(titledBorder_2);

        btn_addGrad = new JButton("ぼかし");
        btn_addGrad.setBackground(new Color(255, 255, 255));
        btn_addGrad.setPreferredSize(new Dimension(MENU_W - 50, 50));
        panel_side2.add(btn_addGrad, BorderLayout.NORTH);

        grad_slider = new JSlider();
        panel_side2.add(grad_slider, BorderLayout.CENTER);
        sliderLabel = new JLabel();
        sliderLabel.setText(String.valueOf(sliderValue) + "% : "
                + String.valueOf((int) ((double) Task.GRAD_SIZE_MAX * ((double) sliderValue / 100.0)) * 2 + 1) + "px");

        panel_side2.add(sliderLabel, BorderLayout.SOUTH);

        panel_side.add(panel_side1);
        panel_side.add(panel_side2);

        // クリップボードから画像取得してフレーム内に表示
        image = getClipboardImage();

        // 画像があるか確認
        if (image != null) {
            // EditCanvasのインスタンスを生成
            canvas = new EditCanvas(scaledWidth, scaledHeight, scale, image);
            editFrame.getContentPane().add(panel_image);
            panel_image.add(canvas, BorderLayout.CENTER);

            // トリミングボタン(toggle)のリスナー設定
            btn_addTrim.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.task.setType(1);// 加工種類をトリミングに設定
                    /**
                     * canvas.mode 1:マウスドラッグ中 -1:マウスドラッグ待機状態(ドラッグする前)
                     */
                    // ドラッグ中でない場合
                    if (canvas.mode != 1) {
                        // ドラッグ待機状態へ
                        canvas.mode = -1;
                        // ボタンを青色に
                        btn_addTrim.setBackground(new Color(82, 165, 255));
                        // 再描画
                        canvas.repaint();
                    } else {
                        // ドラッグ中の場合
                        // ドラッグ可能状態を終了
                        canvas.mode = 0;
                        // ボタンを白に
                        btn_addTrim.setBackground(new Color(255, 255, 255));
                    }
                }
            });

            // ぼかしボタンのリスナー設定
            btn_addGrad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.task.setType(2);// 加工種類をぼかしに設定
                    /**
                     * canvas.mode 1:マウスドラッグ中 -1:マウスドラッグ待機状態(ドラッグする前)
                     */
                    // ドラッグ中でない場合
                    if (canvas.mode != 1) {
                        // ドラッグ待機状態へ
                        canvas.mode = -1;
                        // ボタンを青色に
                        btn_addGrad.setBackground(new Color(82, 165, 255));
                        // 再描画
                        canvas.repaint();
                    } else {
                        // ドラッグ中の場合
                        // ドラッグ可能状態を終了
                        canvas.mode = 0;
                        // ボタンを白に
                        btn_addGrad.setBackground(new Color(255, 255, 255));
                    }
                }
            });

            // ぼかしサイズのスライダーのリスナー設定
            grad_slider.addChangeListener(new ChangeListener() {
                /**
                 * スライダーが操作された時の呼ばれるメソッド スライダーの値を変数に格納し、GUIにも表示する
                 * 
                 * @param e
                 */
                public void stateChanged(ChangeEvent e) {
                    // スライダーの値を変数に格納
                    sliderValue = grad_slider.getValue();
                    // GUIにも表示
                    sliderLabel.setText(String.valueOf(sliderValue) + "% : "
                            + String.valueOf(
                                    (int) ((double) Task.GRAD_SIZE_MAX * ((double) sliderValue / 100.0)) * 2 + 1)
                            + "px");

                }
            });

            editFrame.add(panel_image, BorderLayout.WEST);
            editFrame.add(panel_side, BorderLayout.EAST);
            // 可視化
            editFrame.setVisible(true);
        }

    }

    /**
     * クリップボードから画像を取得する
     * 
     * @return BufferedImage 画像がない場合はnull
     */
    private BufferedImage getClipboardImage() {
        // クリップボードの中身を取得
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable data = clip.getContents(null);
        // 取得したものが画像データである場合
        try {
            // buferedImageにキャストして代入
            BufferedImage img = (BufferedImage) clip.getData(DataFlavor.imageFlavor);
            // System.out.printf("編集画面立ち上げ時画像(縮小前):%d,%d\n", img.getWidth(),
            // img.getHeight());

            // 縮小後の縦横サイズを保持する変数
            scaledWidth = img.getWidth();
            scaledHeight = img.getHeight();
            scale = 1.0;
            // 用意している画像表示枠(imageW*imageH)を上回る場合に縮小する
            if (img.getHeight() > CANVAS_H) {
                scaledHeight = CANVAS_H;
                scaledWidth = (int) (scaledHeight * img.getWidth() / img.getHeight());
                scale = ((double) scaledHeight / (double) img.getHeight());
            }
            if (img.getWidth() > CANVAS_W) {
                scaledWidth = CANVAS_W;
                scaledHeight = (int) (scaledWidth * img.getHeight() / img.getWidth());
                scale = ((double) scaledWidth / (double) img.getWidth());
            }

            // 画像がウィンドウに収まるように縮小
            // 縮小を行っているgetScaledInstanceImageメソッドは戻り値がImageなので、Graphicsを使ってBufferedImageへの変換も行う
            BufferedImage bimg = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bimg.createGraphics();
            g.drawImage(img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
            g.dispose();
            // 画像が用意できたのでフラグを上げる
            isImage = true;
            return bimg;
        } catch (Exception e) {
            // エラーハンドリング(主にclip.getData()に対するクリップボードの中身が画像でない場合の例外処理)
            // System.out.println("クリップボードの中身が画像ではありません");
            // クリップボードの中身が画像でない場合でもこのメソッドが呼ばれ、このExceptionは想定内のためprintStackTrace()せずに処理を続行
            // e.printStackTrace();
            isImage = false;
            return null;
        }
    }

    // 画像を取り込めたか(クリップボードに画像があったか)確かめるメソッド
    /**
     * 
     * @return 画像があるか否かの真理値
     */
    public boolean isImage() {
        return isImage;
    }

    /**
     * 設定されたTaskオブジェクトを返す
     */
    public Task getTask() {
        // ダミーで作られた非表示のウィンドウではないか、画像をとりこめているかを確認
        if (isInstantiated && this.isImage()) {
            // ぼかしの程度を決めるスライダーの値(0~100)をセット
            canvas.task.setGradSize(this.sliderValue);
            return canvas.task;
        } else {
            return null;
        }
    }

}

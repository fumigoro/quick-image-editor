package quick.image.editor;

import java.awt.*;
import javax.swing.*;

import javax.swing.JSlider;
import javax.swing.border.*;
import javax.swing.event.*;


import quick.image.editor.Task;

import java.awt.event.*;
import java.awt.image.*;
import java.awt.Toolkit;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

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
    int sliderValue;

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

    /**編集ウィンドウが立ち上げられたときに呼ばれるコンストラクタ */
    EditWindow() {
        createEditWindow();
        isInstantiated = true;
    }
    /**MainWindow起動時にリスナー設定で必要なため、UI表示を行わずインスタンス化するときに使う特別なコンストラクタ */
    EditWindow(boolean dummy) {
        isInstantiated = false;
    }

    /**
     * ウィンドウを作成し、各種UIを作成する
     */
    private void createEditWindow() {
        // ウィンドウ作成
        editFrame = new JFrame("Edit");
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
        panel_side2.add(btn_addGrad,BorderLayout.NORTH);

        grad_slider = new JSlider();
        panel_side2.add(grad_slider,BorderLayout.CENTER);
        sliderLabel = new JLabel(String.valueOf(grad_slider.getValue()));
        panel_side2.add(sliderLabel,BorderLayout.SOUTH);

        panel_side.add(panel_side1);
        panel_side.add(panel_side2);

        // クリップボードから画像取得してフレーム内に表示
        image = getClipboardImage();

        if (image != null) {
            System.out.printf("編集画面立ち上げ時画像(縮小あと):%d,%d\n", image.getWidth(), image.getHeight());
            // System.out.printf("編集画面立ち上げ時画像(縮小あと):%d,%d\n",scaledWidth,scaledHeight);
            // EditCanvasのインスタンスを生成
            canvas = new EditCanvas(scaledWidth, scaledHeight, scale, image);
            // JPanel pane = new JPanel();
            editFrame.getContentPane().add(panel_image);
            panel_image.add(canvas, BorderLayout.CENTER);

            // トリミングボタンのリスナー設定
            btn_addTrim.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.task.setType(1);// 加工種類をトリミングに設定
                    System.out.println("set:1");
                    if (canvas.mode != 1) {
                        canvas.mode = -1;
                        btn_addTrim.setBackground(new Color(82, 165, 255));
                        canvas.repaint();
                    } else {
                        canvas.mode = 0;
                        btn_addTrim.setBackground(new Color(255, 255, 255));
                    }
                }
            });

            // ぼかしボタンのリスナー設定
            btn_addGrad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canvas.task.setType(2);// 加工種類をぼかしに設定
                    System.out.println("set:2");
                    if (canvas.mode != 1) {
                        canvas.mode = -1;
                        btn_addGrad.setBackground(new Color(82, 165, 255));
                        canvas.repaint();
                    } else {
                        canvas.mode = 0;
                        btn_addGrad.setBackground(new Color(255, 255, 255));
                    }
                }
            });

            //ぼかしサイズのスライダーのリスナー設定
            grad_slider.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e){
                    sliderValue = grad_slider.getValue();
                    sliderLabel.setText(String.valueOf(sliderValue));
                }
            });

            // picLabel = new JLabel(new ImageIcon(image));
            // picLabel.setLayout(new BorderLayout());
            // panel_image.add(picLabel, BorderLayout.CENTER);

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
            System.out.printf("編集画面立ち上げ時画像(縮小前):%d,%d\n", img.getWidth(), img.getHeight());

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
            System.out.println((double) scaledWidth / (double) img.getWidth());
            System.out.println(1.0 / 20.0);

            // 画像がウィンドウに収まるように縮小
            // 縮小を行っているgetScaledInstanceImageメソッドは戻り値がImageなので、Graphicsを使ってBufferedImageへの変換も行う
            BufferedImage bimg = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = bimg.createGraphics();
            g.drawImage(img.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_AREA_AVERAGING), 0, 0, null);
            g.dispose();
            isImage = true;
            return bimg;
        } catch (Exception e) {
            // エラーハンドリング(主にclip.getData()に対するクリップボードの中身が画像でない場合の例外処理)
            // System.out.println("クリップボードの中身が画像ではありません");
            // e.printStackTrace();
            isImage = false;
            return null;
        }
    }

    // クリップボードに画像があるか確かめるメソッド
    public boolean isImage() {
        return isImage;
    }

    /**
     * 設定されたTaskオブジェクトを返す
     */
    public Task getTask() {
        if (isInstantiated && this.isImage()) {
            //ぼかしの程度を決めるスライダーの値(0~100)をセット
            canvas.task.setGradSize(this.sliderValue);
            return canvas.task;
        } else {
            return null;
        }

    }

}

// キャンバスクラス
class EditCanvas extends Canvas implements MouseListener, MouseMotionListener {

    // 描画内容を保持するBufferedImage
    BufferedImage cImage = null;
    // 加工する画像
    BufferedImage image = null;
    // cImageに描画するためのインスタンス
    // private Graphics2D g2d;
    Graphics g2d;
    // 線の開始座標・終了座標
    // private int x, y, xx, yy;
    // 線の色
    Color c = Color.black;
    // 描画モードＯＲ消しゴムモード
    public int mode;
    // 画像のサイズ
    int width, height;
    // マウスポインターの現在位置座標
    int x, y;
    // ドラッグ開始座標
    int px, py;
    // マウスをドラッグして描く四角の縦横
    int ow, oh;
    // マウスをドラッグして描く四角の支点
    int sx, sy;
    // 加工内容を表すデータ
    public Task task = new Task();
    // 表示している画像の縮小倍率
    double scale;

    EditCanvas(int w, int h, double scale, BufferedImage image) {
        this.image = image;
        width = w;
        height = h;
        this.scale = scale;
        // 座標を初期化
        x = -1;
        y = -1;
        px = -1;
        py = -1;
        mode = 0;
        ow = 0;
        oh = 0;
        // リスナー設定
        addMouseListener(this);
        addMouseMotionListener(this);
        // キャンバスの背景を白に設定

        setBackground(new Color(238, 238, 238));

        // 描画内容を保持するBufferedImageを生成
        cImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2d = cImage.getGraphics();
        // BufferedImageの背景も白にする
        // g2d.setColor(Color.GREEN);
        // g2d.fillRect(0, 0, 200, 200);
        g2d.drawImage(image, 0, 0, null);
        // 描画
        repaint();
    }

    public void paint(Graphics g) {
        // System.out.println("paint");
        // System.out.println(mode);

        g2d.drawImage(image, 0, 0, null);
        switch (mode) {
            //マウスドラッグ中
            case 1:
                if (x < px) {
                    sx = x;
                } else {
                    sx = px;
                }
                if (y < py) {
                    sy = y;
                } else {
                    sy = py;
                }
                // System.out.println(ow);
                g2d.setColor(new Color(21, 97, 178, 80));
                g2d.fillRect(0, 0, width, sy);
                g2d.fillRect(0, sy, sx, oh);
                g2d.fillRect(sx + ow, sy, width - ow - sx, oh);
                g2d.fillRect(0, sy + oh, width, height - sy - oh);

                g2d.setColor(Color.ORANGE);
                g2d.drawRect(sx, sy, ow - 1, oh - 1);
                break;
            //ドラッグ開始前
            case -1:
                g2d.setColor(new Color(21, 97, 178, 80));
                g2d.fillRect(0, 0, width, height);
                mode = 1;
        }
        g.drawImage(cImage, 0, 0, null);
    }

    // フレームに何らかの更新が行われた時の処理
    @Override
    public void update(Graphics g) {
        paint(g); // 下記の paint を呼び出す
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = clamp(e.getX(), width);
        y = clamp(e.getY(), height);
        // System.out.println(px);
        ow = Math.abs(x - px);
        oh = Math.abs(y - py);
        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        x = clamp(e.getX(), width);
        y = clamp(e.getY(), height);
        px = clamp(e.getX(), width);
        py = clamp(e.getY(), height);

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // mode = 0;
        // 加工内容を表すデータ
        task.setRange(undoScale(sx), undoScale(sy), undoScale(ow), undoScale(oh));

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private int clamp(int n, int max) {
        if (n < 0) {
            return 0;
        }
        if (n > max) {
            return max;
        }
        return n;
    }

    private int undoScale(int n) {
        double n2 = n;
        int res = (int) (n2 / scale);
        return res;
    }
}
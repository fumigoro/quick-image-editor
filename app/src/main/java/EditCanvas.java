package quick.image.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.awt.Canvas;
import java.awt.Color;

import javax.swing.*;
import javax.swing.event.*;

/**
 * 加工内容設定画面で画像を表示するCANVAS
 */
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

    @Override
    public void paint(Graphics g) {
        // 読み込んだ画像を表示
        g2d.drawImage(image, 0, 0, null);
        switch (mode) {
            // マウスドラッグ中
            case 1:
                // マウスがドラッグされた方向に応じて適切な開始点座標(sx,sy)を設定
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
                // ドラッグ範囲の周りに半透明の色を描画しドラッグ範囲をわかりやすくする
                g2d.setColor(new Color(21, 97, 178, 80));
                g2d.fillRect(0, 0, width, sy);
                g2d.fillRect(0, sy, sx, oh);
                g2d.fillRect(sx + ow, sy, width - ow - sx, oh);
                g2d.fillRect(0, sy + oh, width, height - sy - oh);
                // ドラッグ範囲の枠線
                g2d.setColor(Color.ORANGE);
                g2d.drawRect(sx, sy, ow - 1, oh - 1);
                break;
            // ドラッグ開始前
            case -1:
                g2d.setColor(new Color(21, 97, 178, 80));
                // 全体を半透明の色で覆う
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

    /**
     * マウスドラッグ時
     * 
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {
        // マウスの現在座標を取得、画像の枠内に追わまる座標に修正
        x = clamp(e.getX(), width);
        y = clamp(e.getY(), height);
        // System.out.println(px);
        // ドラッグ範囲の縦横のサイズを計算
        // MEMO:Math.obsは絶対値
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

    /**
     * マウスドラッグ開始時
     * 
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // 現在の座標をx,px両方に設定
        x = clamp(e.getX(), width);
        y = clamp(e.getY(), height);
        px = clamp(e.getX(), width);
        py = clamp(e.getY(), height);

    }

    /**
     * マウスが離された時
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // 加工内容を表すデータを記録
        task.setRange(undoScale(sx), undoScale(sy), undoScale(ow), undoScale(oh));

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 整数nと最大値を受け取り、0<=n<=maxの場合はn自身を、そうでない場合は0またはmaxの値を返す。ある整数を特定の範囲内に抑え込むようなメソッド
     * 
     * @param n   修正対象の値
     * @param max 最大値
     * @return
     */
    private int clamp(int n, int max) {
        // 基準を下回る場合
        if (n < 0) {
            return 0;
        }
        // 基準を上回る場合
        if (n > max) {
            return max;
        }
        return n;
    }

    /**
     * 縮小した画像上での座標を縮小前のオリジナルサイズでの座標に変換するメソッド
     * 
     * @param n 変換したい座標
     * @return 変換後の座標
     */
    private int undoScale(int n) {
        double n2 = n;
        int res = (int) (n2 / scale);
        return res;
    }
}
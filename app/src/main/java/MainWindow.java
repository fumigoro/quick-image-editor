package quick.image.editor;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;
import java.io.*;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

// import com.google.gson.annotations.*;

// import com.google.gson.stream.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.Gson;

import quick.image.editor.EditWindow;

@SuppressWarnings("serial") // java.ioをimportしているが、シリアライズは必要ないので警告が出ないようにコレ書いておく
public class MainWindow extends JFrame {
    // UI部品
    JFrame mainFlame;
    JPanel panel_L, panel_R, panel_L1, panel_L2, panel_L3, panel_Rup, panel_R2, panel_taskListTitle;
    JButton btn_go, btn_addEditTask, btn, btn_presetSize1, btn_presetSize2;
    JLabel editTaskTitle;
    Border lineBorder;
    TitledBorder titledBorder;
    JTextField txt_fileName, txt_saveDir, txt_imageSizeW, txt_imageSizeH;
    JTextArea txtArea_message;
    JCheckBox cb_fixAspect;
    JMenuBar menubar;
    JMenu menu_1, menu_2, menu_3;
    JMenuItem menuitem1_1, menuitem1_2, menuitem1_3, menuitem3_resetCount, menuitem3_setDefaultDir;
    String format = "png";// 画像のファイル形式
    Gson gson;

    EditWindow editWindow;

    /**
     * その他変数
     */

    int imageCounter;// 出力した画像の枚数をカウントする
    // Setting recent = new Setting();
    JsonNode recent;
    // public static String path;
    // public static String fileName;
    // public static String format;

    /**
     * コンストラクタ
     */
    MainWindow() {

        roadSettingFile();

        // MainWindow mw = new MainWindow();
        // 各種UI部品をつくる
        createMainWindow();
        imageCounter = 0;


        /**
         * リスナーの設定 各部品ごとに個別のリスナー(匿名)クラスを使用し、その中で各処理のメソッドを呼ぶ。
         */
        // goボタン
        btn_go.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_go_Listener();
            }
        });

        // 連番リセットメニュー
        menuitem3_resetCount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                menu_resetCount_Listener();
            }
        });

        // 新規ボタン
        btn_addEditTask.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_addEditTask_Listener();
            }
        });

        // メインのウィンドウ
        // メインのウィンドウがフォーカスされたとき、編集ウィンドウのオブジェクトから加工内容のデータを受け取る
        mainFlame.addWindowListener(new WindowListener() {
            //ウィンドウがフォーカスされたとき
            public void windowActivated(WindowEvent e) {
                System.out.println("activate");
                // TODO 作成された加工タスクに関する情報をeditWindowから取得する
                // System.out.println(editWindow.getHoge());
            }
            //ウィンドウが閉じたとき
            public void windowClosed(WindowEvent e) {
                System.out.println("closed");
            }
            //ウィンドウがバツボタンなどで閉じられようとしたとき
            public void windowClosing(WindowEvent e) {
                System.out.println("closing");
            }
            // ウィンドウのフォーカスが外れたとき
            public void windowDeactivated(WindowEvent e) {
                System.out.println("deactivate");
            }
            //ウィンドウが最小化状態からもとに戻ったとき
            public void windowDeiconified(WindowEvent e) {
                System.out.println("deiconfied");
            }
            //ウィンドウが最小化されたとき
            public void windowIconified(WindowEvent e) {
                System.out.println("iconfied");
            }
            //ウィンドウが最初に開かれた(初めてvisibleになった)とき
            public void windowOpened(WindowEvent e) {
                System.out.println("opend");
            }
        });

    }

    // Goボタン押下時の処理
    private void btn_go_Listener() {
        System.out.println("go");
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable data = clip.getContents(null);
        // 画像データかどうか判定
        boolean imageflag = data.isDataFlavorSupported(DataFlavor.imageFlavor);
        System.out.println("isImage:" + imageflag);

        if (imageflag) {
            try {
                imageCounter++;
                BufferedImage img = (BufferedImage) clip.getData(DataFlavor.imageFlavor);
                // ファイルの場所と名前を作成
                String path = txt_saveDir.getText();
                String file = path + "\\" + txt_fileName.getText() + "_" + (String.valueOf(imageCounter)) + "."
                        + recent.get("format").asText();
                //作成したファイルを書き出し(保存)
                ImageIO.write(img, format, new File(file));
                System.out.println(file);
                System.out.println("saved");
                // txtArea_message
                // .append("保存完了：" + txt_fileName.getText() + "_" +
                // (String.valueOf(imageCounter)) + ".png\n");
                updateMessage("保存完了：" + txt_fileName.getText() + "_" + (String.valueOf(imageCounter)) + "."
                        + recent.get("format").asText());
            } catch (UnsupportedFlavorException e1) {
                e1.printStackTrace();
                updateMessage("保存に失敗");
                imageCounter--;
            } catch (IOException e2) {
                e2.printStackTrace();
                updateMessage("保存に失敗");
                imageCounter--;
            }
        } else {
            updateMessage("クリップボードに画像なし");
        }
    }

    // 新規ボタン押下時の処理
    private void btn_addEditTask_Listener() {
        editWindow = new EditWindow();
        if(editWindow.isImage()==false){
            updateMessage("クリップボードに画像なし");
        };
    }

    private void menu_resetCount_Listener() {
        imageCounter = 0;
        updateMessage("連番リセット");
    }

    private void createMainWindow() {
        mainFlame = new JFrame("QuickImageEditor");
        mainFlame.setLayout(new GridLayout(1, 2));
        // ウィンドウの位置とサイズを指定
        mainFlame.setBounds(100, 100, 600, 400);

        panel_L = new JPanel();
        panel_L.setLayout(new BorderLayout());

        panel_L.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel_L1 = new JPanel();

        btn_go = new JButton("Go");

        btn_addEditTask = new JButton("新規");
        panel_L1.setLayout(new GridLayout(1, 2));
        panel_L1.setPreferredSize(new Dimension(300, 50));
        panel_L1.add(btn_go);
        panel_L1.add(btn_addEditTask);
        panel_L.add("North", panel_L1);

        panel_L2 = new JPanel();
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, "加工タスク");
        panel_L2.setBorder(titledBorder);

        panel_L.add(panel_L2);
        mainFlame.add(panel_L);

        panel_R = new JPanel();
        panel_R.setLayout(new GridLayout(3, 1));
        panel_R.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));
        panel_R.setPreferredSize(new Dimension(300, 100));

        panel_R.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 5));

        JPanel panel_Rup = new JPanel();
        panel_Rup.setLayout(new GridLayout(2, 1));
        JPanel panel_R2 = new JPanel();
        Border lineBorder_R2 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_R2 = BorderFactory.createTitledBorder(lineBorder_R2, "ファイル名");
        panel_R2.setBorder(titledBorder_R2);
        panel_R2.setPreferredSize(new Dimension(300, 40));

        Calendar cal = Calendar.getInstance();

        String fn;
        if (recent.get("fileName").asText() == "") {// 設定ファイルの項目が空白の場合
            fn = String.valueOf(cal.get(Calendar.YEAR)) + String.valueOf(cal.get(Calendar.MONTH) + 1)
                    + cal.get(Calendar.DATE);
        } else {
            fn = recent.get("fileName").asText();
        }
        txt_fileName = new JTextField(fn, 15);
        JLabel label_fileName = new JLabel("_<番号>." + recent.get("format").asText());
        panel_R2.add(txt_fileName);
        panel_R2.add(label_fileName);
        panel_Rup.add(panel_R2);

        JPanel panel_R3 = new JPanel();
        Border lineBorder_R3 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_R3 = BorderFactory.createTitledBorder(lineBorder_R3, "保存場所");
        panel_R3.setBorder(titledBorder_R3);
        panel_R3.setPreferredSize(new Dimension(300, 40));
        panel_R3.setLayout(new FlowLayout());

        txt_saveDir = new JTextField(recent.get("path").asText(), 20);
        panel_R3.add(txt_saveDir);
        panel_Rup.add(panel_R3);
        panel_R.add(panel_Rup);

        JPanel panel_R4 = new JPanel();
        Border lineBorder_R4 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_R4 = BorderFactory.createTitledBorder(lineBorder_R4, "画像サイズ");
        panel_R4.setBorder(titledBorder_R4);
        panel_R4.setPreferredSize(new Dimension(300, 40));

        txt_imageSizeW = new JTextField(recent.get("size").get("width").asText(), 7);
        JLabel label_imageSize1 = new JLabel("px × ");
        txt_imageSizeH = new JTextField(recent.get("size").get("hight").asText(), 7);
        JLabel label_imageSize2 = new JLabel("px");
        panel_R4.add(txt_imageSizeW);
        panel_R4.add(label_imageSize1);
        panel_R4.add(txt_imageSizeH);
        panel_R4.add(label_imageSize2);

        btn_presetSize1 = new JButton(recent.get("size").get("presets").get(0).get("width").asText() + "×"
                + recent.get("size").get("presets").get(0).get("hight").asText());
        btn_presetSize2 = new JButton(recent.get("size").get("presets").get(1).get("width").asText() + "×"
                + recent.get("size").get("presets").get(1).get("hight").asText());
        cb_fixAspect = new JCheckBox("縦横比を維持", Boolean.valueOf(recent.get("size").get("fixAspectRatio").asText()));
        JCheckBox cb_notChanageSize = new JCheckBox("サイズ変更しない",
                Boolean.valueOf(recent.get("size").get("resize").asText()));
        // ボタン内部の余白を0にする
        btn_presetSize1.setMargin(new Insets(0, 0, 0, 0));
        btn_presetSize2.setMargin(new Insets(0, 0, 0, 0));

        panel_R4.add(btn_presetSize1);
        panel_R4.add(btn_presetSize2);
        panel_R4.add(cb_fixAspect);
        panel_R4.add(cb_notChanageSize);

        panel_R.add(panel_R4);

        JPanel panel_Rb = new JPanel();
        Border lineBorder_Rb = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_Rb = BorderFactory.createTitledBorder(lineBorder_Rb, "メッセージ");
        panel_Rb.setBorder(titledBorder_Rb);
        panel_Rb.setPreferredSize(new Dimension(300, 40));

        txtArea_message = new JTextArea(3, 24);
        txtArea_message.setEditable(false);
        panel_Rb.add(txtArea_message);
        panel_R.add(panel_Rb);

        mainFlame.add(panel_R);

        // メニューバー
        menubar = new JMenuBar();
        menu_1 = new JMenu("File");
        menu_2 = new JMenu("Edit");
        menu_3 = new JMenu("書き出し");

        menuitem1_1 = new JMenuItem("Open");
        menuitem1_2 = new JMenuItem("Exit");
        menuitem1_3 = new JMenuItem("保存場所を開く");
        menuitem3_resetCount = new JMenuItem("連番をリセット");
        menuitem3_setDefaultDir = new JMenuItem("現在の保存場所をデフォルトに設定");
        menu_1.add(menuitem1_1);
        menu_1.add(menuitem1_2);
        menu_1.add(menuitem1_3);

        menu_3.add(menuitem3_resetCount);
        menu_3.add(menuitem3_setDefaultDir);
        menubar.add(menu_1);
        menubar.add(menu_2);
        menubar.add(menu_3);
        mainFlame.setJMenuBar(menubar);

        // バツを押したときの終了処理
        mainFlame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFlame.setVisible(true);

    }

    private void updateMessage(String m) {
        int lines = 3;// テキストエリアの表示行数
        String str = txtArea_message.getText();
        String[] strs = str.split("\n");
        if (strs.length >= lines) {
            txtArea_message.setText("");// テキストエリアを空白に
            for (int i = lines - 1; i > 0; i--) {
                txtArea_message.append(strs[strs.length - i] + "\n");// 挿入前の行のうちまだ削除対象でないものを入れる
            }
        }
        txtArea_message.append(m + "\n");// 追加したいメッセージ
    }

    private void roadSettingFile() {
        /**
         * 外部ライブラリJacsonを用いてjsonファイルを読み込む
         *
         */
        FileReader fr;
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.recent = mapper.readTree(new File("src/main/resources/recent.json"));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

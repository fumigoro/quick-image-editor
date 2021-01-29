package quick.image.editor;

import java.awt.*;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.image.*;
import java.io.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.border.*;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

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
    JCheckBox cb_fixAspect, cb_resize;
    JMenuBar menubar;
    JMenu menu_1, menu_2, menu_3;
    JMenuItem menuitem1_1, menuitem1_2, menuitem1_3, menuitem3_resetCount, menuitem3_setDefaultDir,
            menuitem3_saveCongig;
    JProgressBar pb; // 今後のプログレスバー実装用

    String format;// 画像のファイル形式

    EditWindow editWindow; // 編集画面のインスタンスを入れる

    int countTasks = 0; // タスク一覧の数
    // タスク一覧りすと
    List<Task> taskList = new ArrayList<>();
    /**
     * その他変数
     */

    int imageCounter = 0;// 出力した画像の枚数をカウントする
    int lineCounter = 0;// メッセージ欄の行数をカウントする

    Settings setting;// 設定ファイルから読み込んだ設定を保存する

    // 設定ファイルsettings.jsonの場所を指定
    static final String SETTING_FILE_PATH = "src/main/resources/settings.json";
    // 前回使用時の設定などを保存するconfig.propertiesの場所
    static final String CONFIG_FILE_PATH = "src/main/resources/config.properties";
    // config.propertiesから読み込んだ情報を入れる
    Properties config = new Properties();

    /**
     * コンストラクタ
     */
    MainWindow() {
        // リスナー内で使用するため一旦editWindowをインスタンス化(GUIは表示されない)
        editWindow = new EditWindow(false);
        // 設定ファイルを読み込み
        final boolean isLoadedSuccessfully = loadSettingFile();
        if (!isLoadedSuccessfully) {
            // 読み込み失敗時は以降の処理を行わない
            return;
        }
        // 前回起動時の記録ファイルを読み込み
        loadConfig();
        // 前回起動時から連番を引き継ぐ設定のオンオフ確認、それに応じて設定
        if (setting.keepCount) {
            this.imageCounter = Integer.parseInt(config.getProperty("imageCounter"));
        } else {
            this.imageCounter = 0;
        }

        // 各種UI部品をつくる
        createMainWindow();
        /**
         * 各リスナーの設定 各部品ごとに個別のリスナー(匿名)クラスを使用し、その中で各処理のメソッドを呼ぶ。
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

        // サイズプリセットボタン1
        btn_presetSize1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_presetSize1_Listener();
            }
        });

        // サイズプリセットボタン2
        btn_presetSize2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btn_presetSize2_Listener();
            }
        });

        // メインのウィンドウ
        // メインのウィンドウがフォーカスされたとき、編集ウィンドウのオブジェクトから加工内容のデータを受け取る
        mainFlame.addWindowListener(new WindowListener() {
            // ウィンドウがフォーカスされたとき
            @Override
            public void windowActivated(WindowEvent e) {
                // 編集画面から加工内容を示すデータを取得する
                // リスナー設定のためにコンストラクタ内でダミーで作られたインスタンスの場合、getTask()からNullが返される。
                if (editWindow.getTask() != null) {
                    addTask(editWindow.getTask());
                    // System.out.println("addtask");
                }
            }

            // 以下使用していない
            // ウィンドウが閉じたとき
            @Override
            public void windowClosed(WindowEvent e) {
                // System.out.println("closed");
            }

            // ウィンドウがバツボタンなどで閉じられようとしたとき
            @Override
            public void windowClosing(WindowEvent e) {
                // System.out.println("closing");
            }

            // ウィンドウのフォーカスが外れたとき
            @Override
            public void windowDeactivated(WindowEvent e) {
                // System.out.println("deactivate");
            }

            // ウィンドウが最小化状態からもとに戻ったとき
            @Override
            public void windowDeiconified(WindowEvent e) {
                // System.out.println("deiconfied");
            }

            // ウィンドウが最小化されたとき
            @Override
            public void windowIconified(WindowEvent e) {
                // System.out.println("iconfied");
            }

            // ウィンドウが最初に開かれた(初めてvisibleになった)とき
            @Override
            public void windowOpened(WindowEvent e) {
                // System.out.println("opend");
            }
        });
        // configファイルに今の設定を保存
        saveConfig();

    }

    // プリセットボタン1の処理
    private void btn_presetSize1_Listener() {
        txt_imageSizeH.setText(String.valueOf(setting.presetSize1.height));
        txt_imageSizeW.setText(String.valueOf(setting.presetSize1.width));
    }

    // プリセットボタン2の処理
    private void btn_presetSize2_Listener() {
        txt_imageSizeH.setText(String.valueOf(setting.presetSize2.height));
        txt_imageSizeW.setText(String.valueOf(setting.presetSize2.width));
    }

    // Goボタン押下時の処理
    private void btn_go_Listener() {
        // GUI上の設定内容の読み込み
        // 読み込み失敗時はloadSettingFromGUI()からnullが帰る
        boolean loadSettingFromGUI = loadSettingFromGUI();
        if (!loadSettingFromGUI) {
            return;
        }

        // クリップボードよりデータ読み込み
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable data = clip.getContents(null);
        // 画像データかどうか判定
        boolean imageflag = data.isDataFlavorSupported(DataFlavor.imageFlavor);
        // System.out.println("isImage:" + imageflag);

        // 画像データの場合加工と書き出しを実施
        if (imageflag) {
            try {
                imageCounter++;
                // クリップボード上のデータを取得しBufferedImageへキャスト
                BufferedImage img = (BufferedImage) clip.getData(DataFlavor.imageFlavor);// 例外：UnsupportedFlavorException

                /**
                 * MEMO: トリミングによって画像サイズが変わると座標ベースの加工範囲が狂うため、トリミングは最後に行う
                 */
                // ぼかし
                for (int i = 0; i < taskList.size(); i++) {
                    Task tmpTask = taskList.get(i);
                    // タスクの有効性を確認
                    if (tmpTask.active) {
                        // 設定された加工内容がぼかしかを確認
                        if (tmpTask.type == 2) {

                            img = tmpTask.run(img);
                            System.out.println("完了");
                        }
                    }
                }

                // トリミング
                for (int i = 0; i < taskList.size(); i++) {
                    Task tmpTask = taskList.get(i);
                    // タスクの有効性を確認
                    if (tmpTask.active) {
                        // 設定された加工内容がトリミングかを確認
                        if (tmpTask.type == 1) {
                            // System.out.printf("読み込み時画像:%d,%d\n",img.getWidth(),img.getHeight());
                            img = tmpTask.run(img);
                            break;// トリミングは1回のみ
                        }
                    }
                }

                // リサイズするか否かの設定を確認
                if (setting.resize) {
                    // System.out.print("サイズ変更中...");
                    img = resizeImage(img);// リサイズ
                    // System.out.println("完了");
                }

                // ファイルの場所と名前を作成
                String path = txt_saveDir.getText();
                String file = path + "\\" + setting.fileName + "_" + (String.valueOf(imageCounter)) + "."
                        + setting.format;
                // 作成したファイルを書き出し(保存)
                ImageIO.write(img, setting.format, new File(file));// 例外：IOException

                // 完了を通知
                updateMessage("保存完了：" + setting.fileName + "_" + (String.valueOf(imageCounter)) + "." + setting.format);
            } catch (UnsupportedFlavorException e1) {
                e1.printStackTrace();
                updateMessage("サポートされていないファイル形式");
                imageCounter--;
            } catch (IOException e2) {
                e2.printStackTrace();
                updateMessage("保存に失敗");
                imageCounter--;
            }
            // 各種パラメーターの更新と保存
            config.setProperty("imageCounter", String.valueOf(imageCounter));
            // config.setProperty("day", String.valueOf(imageCounter));
            saveConfig();
        } else {
            // クリップボードに画像がなかった場合
            updateMessage("クリップボードに画像なし");
        }
    }

    // 加工タスク追加ボタン押下時の処理
    private void btn_addEditTask_Listener() {
        // 加工内容編集画面を表示
        editWindow = new EditWindow();
        // クリップボードに画像があるかチェック
        if (editWindow.isImage() == false) {
            // ない場合
            updateMessage("クリップボードに画像なし");
            // robotクラスでWin+Shift+Sキーを押しWindowsのスクリーンショット撮影機能を呼び出す
            try {
                Robot robot = new Robot();
                // キーの押下を開始
                robot.keyPress(KeyEvent.VK_WINDOWS);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_S);
                robot.delay(10);
                // キーの押下を終了
                robot.keyRelease(KeyEvent.VK_WINDOWS);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.keyRelease(KeyEvent.VK_S);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
    }

    // 連番リセットボタン押下時の処理
    private void menu_resetCount_Listener() {
        imageCounter = 0;
        updateMessage("連番リセット");
    }

    // UIの作成と表示
    private void createMainWindow() {
        mainFlame = new JFrame("Quick image editor");
        mainFlame.setLayout(new GridLayout(1, 2));
        // ウィンドウの位置とサイズを指定
        mainFlame.setBounds(10, 10, 600, 450);

        panel_L = new JPanel();
        panel_L.setLayout(new BorderLayout());

        panel_L.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel_L1 = new JPanel();

        btn_go = new JButton("Go");
        btn_go.setBackground(new Color(82, 165, 255));
        btn_go.setFont(new Font("Arial", Font.BOLD, 16));

        btn_addEditTask = new JButton("タスク追加");
        panel_L1.setLayout(new GridLayout(1, 2));
        panel_L1.setPreferredSize(new Dimension(300, 50));
        panel_L1.add(btn_go);
        panel_L1.add(btn_addEditTask);
        panel_L.add("North", panel_L1);

        panel_L2 = new JPanel();

        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder = BorderFactory.createTitledBorder(lineBorder, "加工タスク");
        panel_L2.setBorder(titledBorder);
        panel_L2.setLayout(new FlowLayout());
        panel_L.add("Center", panel_L2);

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

        String fn;
        if (setting.fileName == "") {// 設定ファイルのfileNameが空白の場合
            Date date = new Date(); // 今日の日付

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 日付を文字列化するときのフォーマットを指定
            fn = dateFormat.format(date);// 日付を文字列化
        } else {
            fn = setting.fileName;// 読み込んだ値に設定
        }
        txt_fileName = new JTextField(fn, 15);
        JLabel label_fileName = new JLabel("_<番号>." + setting.format);
        panel_R2.add(txt_fileName);
        panel_R2.add(label_fileName);
        panel_Rup.add(panel_R2);

        JPanel panel_R3 = new JPanel();
        Border lineBorder_R3 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_R3 = BorderFactory.createTitledBorder(lineBorder_R3, "保存場所");
        panel_R3.setBorder(titledBorder_R3);
        panel_R3.setPreferredSize(new Dimension(300, 40));
        panel_R3.setLayout(new FlowLayout());

        txt_saveDir = new JTextField(setting.path, 20);
        panel_R3.add(txt_saveDir);
        panel_Rup.add(panel_R3);
        panel_R.add(panel_Rup);

        JPanel panel_R4 = new JPanel();
        Border lineBorder_R4 = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_R4 = BorderFactory.createTitledBorder(lineBorder_R4, "画像サイズ");
        panel_R4.setBorder(titledBorder_R4);
        panel_R4.setPreferredSize(new Dimension(300, 40));

        txt_imageSizeW = new JTextField(String.valueOf((int) setting.defaultSize.getWidth()), 5);
        JLabel label_imageSize1 = new JLabel("px(W) × ");
        txt_imageSizeH = new JTextField(String.valueOf((int) setting.defaultSize.getHeight()), 5);
        JLabel label_imageSize2 = new JLabel("px(H)");
        panel_R4.add(txt_imageSizeW);
        panel_R4.add(label_imageSize1);
        panel_R4.add(txt_imageSizeH);
        panel_R4.add(label_imageSize2);

        btn_presetSize1 = new JButton(String.valueOf((int) setting.presetSize1.getWidth()) + "×"
                + String.valueOf((int) setting.presetSize1.getHeight()));
        btn_presetSize2 = new JButton(String.valueOf((int) setting.presetSize2.getWidth()) + "×"
                + String.valueOf((int) setting.presetSize2.getHeight()));
        cb_fixAspect = new JCheckBox("縦横比を維持", setting.fixAspectRatio);
        cb_resize = new JCheckBox("サイズ変更する", setting.resize);

        // ボタン内部の余白を0にする
        btn_presetSize1.setMargin(new Insets(0, 0, 0, 0));
        btn_presetSize2.setMargin(new Insets(0, 0, 0, 0));

        panel_R4.add(btn_presetSize1);
        panel_R4.add(btn_presetSize2);
        panel_R4.add(cb_fixAspect);
        panel_R4.add(cb_resize);

        panel_R.add(panel_R4);

        JPanel panel_Rb = new JPanel();
        Border lineBorder_Rb = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        TitledBorder titledBorder_Rb = BorderFactory.createTitledBorder(lineBorder_Rb, "メッセージ");
        panel_Rb.setBorder(titledBorder_Rb);
        panel_Rb.setPreferredSize(new Dimension(300, 40));

        txtArea_message = new JTextArea(6, 24);
        txtArea_message.setEditable(false);
        panel_Rb.add(txtArea_message);
        panel_R.add(panel_Rb);

        mainFlame.add(panel_R);

        // メニューバー
        menubar = new JMenuBar();
        // menu_1 = new JMenu("File");
        // menu_2 = new JMenu("Edit");
        menu_3 = new JMenu("書き出し設定");

        // menuitem1_1 = new JMenuItem("Open");
        // menuitem1_2 = new JMenuItem("Exit");
        // menuitem1_3 = new JMenuItem("保存場所を開く");
        menuitem3_resetCount = new JMenuItem("連番をリセット");
        menuitem3_saveCongig = new JMenuItem("現在の設定を保存");
        // menuitem3_setDefaultDir = new JMenuItem("現在の保存場所をデフォルトに設定");
        // menu_1.add(menuitem1_1);
        // menu_1.add(menuitem1_2);
        // menu_1.add(menuitem1_3);

        menu_3.add(menuitem3_resetCount);
        menu_3.add(menuitem3_saveCongig);
        // menu_3.add(menuitem3_setDefaultDir);
        // menubar.add(menu_1);
        // menubar.add(menu_2);
        menubar.add(menu_3);
        mainFlame.setJMenuBar(menubar);

        // バツを押したときの終了処理
        mainFlame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFlame.setVisible(true);

    }

    // ステータスメッセージ欄へメッセージを挿入する
    /**
     * テキストエリアの表示行数を超えた場合は古い行から順に削除する
     * 
     * @param m 挿入するメッセージ
     */
    private void updateMessage(String m) {
        final int LINES = 5;// テキストエリアの表示行数
        lineCounter++;// 行数カウントをすすめる
        String str = txtArea_message.getText();
        String[] strs = str.split("\n");
        if (strs.length >= LINES) {
            txtArea_message.setText("");// テキストエリアを空白に
            for (int i = LINES - 1; i > 0; i--) {
                txtArea_message.append(strs[strs.length - i] + "\n");// 挿入前の行のうちまだ削除対象でないものを入れる
            }
        }
        txtArea_message.append(String.valueOf(lineCounter) + ": " + m + "\n");// 追加したいメッセージ
    }

    /**
     * json形式の設定ファイルを読み込む
     * 
     * @return 読み込みが正常に完了したか否か
     */
    private boolean loadSettingFile() {
        /**
         * 外部ライブラリJacsonを用いてjsonファイルを読み込む 読み込んだ内容はSettings型のオブジェクトsettingのメンバ変数へ代入される
         */
        FileReader fr;
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.configure(Feature.ALLOW_COMMENTS, true);// (重要)これによりJOSNにコメントが入れれるようになる
            // 読み込んだJSONをSettingsクラスのインスタンスに変換
            this.setting = mapper.readValue(new File(SETTING_FILE_PATH), Settings.class);
            this.setting.init();
            // System.out.println("設定読み込み完了");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            // 読み込み失敗時
            System.out.println("失敗");
            // 読み込みに失敗した旨のダイアログを表示
            showErrorDialog(e, "設定ファイルの読み込みに失敗しました。");
            return false;
        }
    }

    /**
     * //発生した例外の内容をエラーダイアログに表示する
     * 
     * @param e       //Exception
     * @param message //例外の概要を表すメッセージ
     */
    private void showErrorDialog(Exception e, String message) {
        JOptionPane.showMessageDialog(null, message + "\n詳細:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * 画像のリサイズ(縮小)を行う
     * 
     * @param targetImage 縮小する画像
     * @return resizedImage 縮小後の画像
     */
    private BufferedImage resizeImage(BufferedImage targetImage) {

        // 縮小後の縦横サイズを保持する変数
        int width;
        int height;

        // オリジナルの画像の縦横比
        int asp_original = targetImage.getHeight() / targetImage.getWidth();
        // GUIで設定された画像サイズの縦横比
        int asp_setting = setting.primarySize.height / setting.primarySize.height;

        if (setting.fixAspectRatio) {// 縦横比を固定する場合
            // 指定の出力サイズsetting.defaultSizeを上回る場合に縮小する
            if (asp_original >= asp_setting && targetImage.getHeight() > setting.primarySize.height) {
                // 縦長画像で、タテのサイズが指定より大きい場合
                height = setting.primarySize.height;
                width = (int) (height * targetImage.getWidth() / targetImage.getHeight());
            } else if (asp_original <= asp_setting && targetImage.getWidth() > setting.primarySize.width) {
                // 横長画像で、横のサイズが指定より大きい場合
                width = setting.primarySize.width;
                height = (int) (width * targetImage.getHeight() / targetImage.getWidth());
            } else {
                // 縦横ともに指定サイズ内の場合
                width = targetImage.getWidth();
                height = targetImage.getHeight();
            }
        } else {// 縦横比を可変させて良い場合
            width = setting.primarySize.width;
            height = setting.primarySize.height;
        }
        // 画像縮小
        // 縮小を行っているgetScaledInstanceImageメソッドは戻り値がImageなので、Graphicsを使ってBufferedImageへの変換も行う
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        // resizedImageにグラフィックスを作り、
        Graphics2D g = resizedImage.createGraphics();
        // そこにgetScaledInstance()でできた縮小画像を貼る
        g.drawImage(targetImage.getScaledInstance(width, height, Image.SCALE_AREA_AVERAGING), 0, 0, null);
        g.dispose();
        return resizedImage;
    }

    /**
     * GUI上の入力欄の入力内容を取得しSettingオブジェクトに反映する
     * 
     * @return boolean 反映に成功したか否か
     */
    private boolean loadSettingFromGUI() {
        setting.path = txt_saveDir.getText();
        setting.fileName = txt_fileName.getText();
        // setting.format = ; //(未実装)フォーマットの入力欄
        setting.resize = cb_resize.isSelected();
        setting.fixAspectRatio = cb_fixAspect.isSelected();
        try {// 入力文字列をintへ変換
            setting.primarySize = new Dimension(Integer.parseInt(txt_imageSizeW.getText()),
                    Integer.parseInt(txt_imageSizeH.getText()));
        } catch (Exception e) {
            System.out.println("画像サイズが不正です");
            updateMessage("指定された画像サイズが不正です");
            return false;
        }
        return true;

    }

    /**
     * 追加された加工タスクをGUIに表示する
     */
    private void addTask(Task task) {
        // System.out.println("add");
        // 加工内容が設定されていない場合
        if (task.isReady() != true) {
            // System.out.println("加工内容未設定タスク");
            return;
        }
        if (!(task.active)) {
            // System.out.println("削除済みタスク");
            return;
        }
        // タスクを管理するリストに追加
        taskList.add(task);

        String labelText = "";
        switch (task.type) {
            case 1:// トリミング
                   // labelText = "トリミング "+tasks[countTasks].getRangeS();
                labelText = "トリミング " + taskList.get(countTasks).getRangeS();

                break;
            case 2:// ぼかし
                labelText = "ぼかし " + taskList.get(countTasks).getRangeS() + "  " + task.getGradSizeAsString();
                break;

            default:

        }

        panel_L2.add(taskList.get(countTasks).panel);
        // countTasksには最後に追加されたタスクのインデックス番号が入っている
        taskList.get(countTasks).label.setText(labelText);// 加工内容を示す文字列をラベルに挿入
        taskList.get(countTasks).panel.setVisible(true);// 加工内容と削除ボタンの入ったUIを表示する

        countTasks += 1;
    }

    /**
     * 次回起動時のために各種パラメーターを保存するメソッド
     * 
     */
    private void saveConfig() {

        FileOutputStream out = null;
        try {
            // 出力用のファイルを用意
            out = new FileOutputStream(CONFIG_FILE_PATH);
            // ファイルを出力
            config.store(out, "Config Properties");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // ファイルへの保存が正常にできたか確認
                if (out != null) {
                    // 閉じる
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 前回起動時のパラメーターを読み込むメソッド
     */
    private void loadConfig() {
        FileInputStream in = null;
        try {
            in = new FileInputStream(CONFIG_FILE_PATH);
            config.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

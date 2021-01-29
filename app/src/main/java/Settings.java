package quick.image.editor;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

//Settings.jsonから読み込んだ設定データを保持するクラス
public class Settings {
    // 各メンバ変数はsettings.jsonのキーと対応
    // 加工後の保存先のパス
    public String path;
    // 保存ファイル名(空白にすると今日の日付が入る)
    public String fileName;
    // 保存する画像のフォーマット
    public String format;
    // 画像のサイズを変更するかどうか
    public boolean resize;
    // 画像の縦横比を保持するかどうか
    public boolean fixAspectRatio;
    // 出力画像のサイズ
    public Dimension defaultSize;
    // 出力画像のサイズプリセット
    public Dimension presetSize1;
    public Dimension presetSize2;
    // 前回起動時から連番を引き継ぐかどうか
    public boolean keepCount;

    // 現在選択されているサイズが入る
    public Dimension primarySize;

    Settings() {
    }

    /**
     * Settings.jsonを読み込んだ後に実行するメソッド
     * 
     */
    public void init() {
        // fileNameが空白の場合今日の日付を入れる
        if (fileName == "") {
            Date date = new Date(); // 今日の日付
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");// 日付を文字列化するときのフォーマットを指定
            fileName = dateFormat.format(date);// 日付を文字列化してFileNameへ
        }
        // 保存場所が空白の場合
        if (path == "") {
            path = System.getProperty("user.home") + "\\Pictures\\img";
        }
        // primarySizeにdefaultSizeを入れる
        primarySize = new Dimension(defaultSize.width, defaultSize.height);
    }
}
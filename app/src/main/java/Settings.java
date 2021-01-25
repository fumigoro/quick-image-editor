package quick.image.editor;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Settings{
    public String path;
    public String fileName;
    public String format;
    public boolean resize;
    public boolean fixAspectRatio;
    public Dimension defaultSize;
    public Dimension primarySize;//現在選択されているサイズが入る
    public Dimension presetSize1;
    public Dimension presetSize2;
    public boolean keepCount;
    Settings(){
    }

    // public void setDay(){
    //     if(fileName==""){
    //         Date date = new Date(); // 今日の日付
    //         SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//日付を文字列化するときのフォーマットを指定
    //         fileName = dateFormat.format(date);//日付を文字列化してFileNameへ
    //     }
    // }

    public void init(){
        //fileNameが空白の場合今日の日付を入れる
        if(fileName==""){
            Date date = new Date(); // 今日の日付
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//日付を文字列化するときのフォーマットを指定
            fileName = dateFormat.format(date);//日付を文字列化してFileNameへ
        }
        //保存場所が空白の場合
        if(path==""){
            path = System.getProperty("user.home")+"\\Pictures\\img";
        }
        //primarySizeにdefaultSizeを入れる
        primarySize = new Dimension(defaultSize.width,defaultSize.height);
    }
}
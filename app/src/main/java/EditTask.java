package quick.image.editor;

import java.awt.image.*;

public class EditTask {
    //加工範囲の支点,縦横幅、加工の種類
    public int x,y,width,height,type;
    String type1 = "トリミング";
    String type2 = "ぼかし";
    /**
     * type
     * 1:トリミング
     * 2:ぼかし
     */
    EditTask(int x,int y,int w,int h,int t){
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        this.type = t;
    }
    EditTask(int x,int y,int w,int h){
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
    }
    EditTask(){

    }

    public void setType(int t) {
        this.type = t;
    }
    public void setRange(int x,int y,int w,int h) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        System.out.print(x);
        System.out.print(",");
        System.out.print(y);
        System.out.print(",");
        System.out.print(w);
        System.out.print(",");
        System.out.println(h);

        

    }


    public String getEditTypeS() {
        switch(type){
            case 1:
                return type1;
            case 2:
                return type2;
            default:
                // throw new Exception("Processing type is null.");
                return null;

        }
    }

    //加工を行うメソッド
    public BufferedImage run(BufferedImage img){
        // if(ximg.getWidth());
        // img.getHeight();
        switch(type){
            case 1://トリミング
                System.out.println("トリミング");
                img = img.getSubimage(x,y,width,height);
                return img;
            case 2://ぼかし
                System.out.println("ぼかし");
                return img;
            default:
                // throw new Exception("Processing type is null.");
                return null;
        }

    }

    /**加工に必要なパラメーターが揃っているか確認するメソッド */
    public boolean isReady(){
        if(!(x>0) || !(y>0) || !(width>0) || !(height>0)){
            return false;
        }
        if(!(type>=1)||!(type<=2)){
            return false;
        }
        return true;

    }

}

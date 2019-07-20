package radionoisecontroller.graphics.shapes;

import radionoisecontroller.graphics.Texture;
import static radionoisecontroller.global.*;

public final class TextLabel{
    
    private Image[] letras;
    
    private int x, y, width, heiht;
    
    public TextLabel(int x, int y, float scaleX, float scaleY) {
        letras = null;
        width = (int) (64*scaleX);
        heiht = (int) (64*scaleY);
        this.x = x;
        this.y = y;
    }
    
    public void setPercentaje(byte b){
        setPercentaje((int)(b & 0xFF));
    }
    public void setPercentaje(int p){
        if(p < 0) p = 0;
        else if(p > 100) p = 100;
        setText(p+"%");
    }
    
    public void setText(String text){
        letras = new Image[text.length()];
        for(int i = 0; i < text.length(); i++){
            switch(text.charAt(i)){
                case '0': letras[i] = new Image(texture_alpha_0, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '1': letras[i] = new Image(texture_alpha_1, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '2': letras[i] = new Image(texture_alpha_2, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '3': letras[i] = new Image(texture_alpha_3, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '4': letras[i] = new Image(texture_alpha_4, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '5': letras[i] = new Image(texture_alpha_5, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '6': letras[i] = new Image(texture_alpha_6, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '7': letras[i] = new Image(texture_alpha_7, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '8': letras[i] = new Image(texture_alpha_8, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '9': letras[i] = new Image(texture_alpha_9, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '.': letras[i] = new Image(texture_alpha_dot, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '-': letras[i] = new Image(texture_alpha_minus, x+(i*LETTER_SPACING), y, width, heiht); break;
                case '%': letras[i] = new Image(texture_alpha_percentaje, x+(i*LETTER_SPACING), y, width, heiht); break;
                case 'd': letras[i] = new Image(texture_alpha_dBm, x+(i*LETTER_SPACING), y, width, heiht); break;
            }
        }
    }
    
    public void draw(){
        if(letras == null) return;
        for(Image letra : letras)
            if(letra != null)
                letra.draw();
    }
    
}

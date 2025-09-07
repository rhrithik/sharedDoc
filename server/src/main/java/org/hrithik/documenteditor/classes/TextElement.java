package org.hrithik.documenteditor.classes;

public class TextElement implements DocumentElement{

    String text;

    public  TextElement(String text){
        this.text=text;
    }

    @Override
    public void render() {
        System.out.println("Rendered text: "+text);
    }
}

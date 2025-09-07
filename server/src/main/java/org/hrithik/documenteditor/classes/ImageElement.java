package org.hrithik.documenteditor.classes;

public class ImageElement implements DocumentElement{
    String imagePath;
    public ImageElement(String imagePath){
        this.imagePath = imagePath;
    }


    @Override
    public void render() {
        System.out.println("Rendered image: "+imagePath);
    }
}

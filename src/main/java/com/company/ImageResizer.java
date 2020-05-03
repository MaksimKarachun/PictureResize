package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ImageResizer implements Runnable {

    private File[] files;
    private int widthOfNewPicture;
    private String dstFolder;
    private int newWidth;


    public ImageResizer(File[] files, int widthOfNewPicture, String dstFolder) {
        this.files = files;
        this.widthOfNewPicture = widthOfNewPicture;
        this.dstFolder = dstFolder;
        newWidth = widthOfNewPicture * 3;
    }


    @Override
    public void run() {

        try{

            for(File file : files){

                BufferedImage image = ImageIO.read(file);
                if(image == null)
                    continue;

                //получение масшатаба уменьшения
                double scale = (double) newWidth / image.getWidth();
                int newHeight = (int) Math.round(image.getHeight() * scale);

                BufferedImage newImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);

                //координаты пикселей которые попадут в промежуточное изображение
                int x1, y1;

                for (int x = 0; x < newWidth; x++){

                    x1 = (int) (x / scale);

                    for(int y = 0; y < newHeight; y++){
                        y1 = (int) (y / scale);
                        int rgb = image.getRGB(x1, y1);
                        newImage.setRGB(x, y, rgb);
                    }
                }

                // размеры конечного изображения
                // widthOfNewPicture = 300;
                int newHeight1 = newHeight / 3;

                BufferedImage newImage1 = new BufferedImage(widthOfNewPicture , newHeight1, BufferedImage.TYPE_INT_RGB);

                //координаты пикселей конечного изображения
                int x2 = 0;
                int y2 = 0;

                for(int x = 0; x < newImage1.getWidth() * 3; x = x + 3){
                    for(int y = 0; y < newImage1.getHeight() * 3; y = y + 3){

                        //получение цветов пикселей по краям прямоугольника 3х3
                        Color color11 = new Color(newImage.getRGB(x, y));
                        Color color12 = new Color(newImage.getRGB(x + 2, y));
                        Color color21 = new Color(newImage.getRGB(x, y + 2));
                        Color color22 = new Color(newImage.getRGB(x + 2, y + 2));

                        //определение цвета пикселя в центре прямоугольника 3х3 методом билинейной интерполяции
                        int blue = bilinearInterpolation(color11.getBlue(), color12.getBlue(), color21.getBlue(), color22.getBlue());
                        int red = bilinearInterpolation(color11.getRed(), color12.getRed(), color21.getRed(), color22.getRed());
                        int green = bilinearInterpolation(color11.getGreen(), color12.getGreen(), color21.getGreen(), color22.getGreen());

                        Color color = new Color(red, green, blue);
                        newImage1.setRGB(x2, y2, color.getRGB());

                        y2++;
                    }
                    y2 = 0;
                    x2++;
                }
                File newFile1 = new File (dstFolder + "/" + file.getName());
                ImageIO.write(newImage1, "png", newFile1);

            }
        }
        catch (Exception e){
            e.getMessage();
        }

    }

    //в метод передаются значения цветов в углах квадрата 3х3
    private static int bilinearInterpolation(int Q11, int Q12, int  Q21, int Q22){
        int R1 = (Q11 + (Q12 - Q11) / 2);
        int R2 = (Q21 + (Q22 - Q21) / 2);
        return (R1 + (R2 - R1) / 2);
    }
}

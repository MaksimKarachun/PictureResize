package com.company;

import java.io.File;

/**
 * Уменьшение изображений происходит в два этапа:
 * 1. получение промежуточого изображения методом ближайшего соседа.
 * Размер целевого изображения в три раза больше размера конечного изображения.
 * 2. уменьшение промежуточного изображения до размеров конечного методом билинейной интерполяции.
 */

public class Main {

    public static void main(String[] args) {

        //определение количества доступных процессоров
        int processorsCount = Runtime.getRuntime().availableProcessors();

        //ширина уменьшенного изображения
        int widthOfNewPicture = 300;

        //ширина промежуточного изображения
        int newWidth = widthOfNewPicture * 3;

        //пути к директориям с изображениями
        String srcFolder = ".\\picture";
        String dstFolder = ".\\resizePicture";

        File srcDir = new File(srcFolder);
        File[] files = srcDir.listFiles();

        //количество файлов обрабатываемых в одном потоке
        int numFiles = files.length / processorsCount;

        //запуск потоков в зависимости от количества доступных процессоров
        for(int i = 0; i < processorsCount; i++) {

            File[] currentArray;
            if(i != processorsCount - 1) {
                currentArray = new File[numFiles];
                System.arraycopy(files, i * numFiles, currentArray, 0, numFiles);
            }
            else {
                currentArray = new File[files.length - i * numFiles];
                System.arraycopy(files, i * numFiles, currentArray, 0, files.length - i * numFiles);
            }
            ImageResizer resizer = new ImageResizer(currentArray, widthOfNewPicture, dstFolder);
            new Thread(resizer).start();
        }
    }

}

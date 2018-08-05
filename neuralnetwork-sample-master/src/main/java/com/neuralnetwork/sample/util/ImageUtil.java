package com.neuralnetwork.sample.util;
import com.neuralnetwork.sample.constant.Constant;
import com.neuralnetwork.sample.model.ImageModel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/*
Copyright [2017] [Pi Jing]

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/
public class ImageUtil {
    private static ImageUtil imageUtil = null;
    private int width = 28;
    private int height = 28;

    private ImageUtil(){}

    public static ImageUtil getInstance(){
        if(imageUtil == null){
            imageUtil = new ImageUtil();
        }
        return imageUtil;
    }

    //list all jpg file in train\test folder
    public List<String> getImageList(String type){
        File file = null;
        if(type.equals("train")) {
            file = new File("D:\\download\\Train1");}
        if(type == "test") {
            file = new File("D:\\download\\Test");}
        
        List<String> fileList = new ArrayList<String>();
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for(File fileItem:files){
//                if(fileItem.isFile() && fileItem.getAbsolutePath().endsWith(".png")){
//                    fileList.add(fileItem.getAbsolutePath());
//                }
                if(fileItem.isFile()){
                    fileList.add(fileItem.getAbsolutePath());
                }
            }
        }
        System.out.println("Read " + fileList.size() + " images for " + type + "ing");
        return fileList;
    }

    //create image model list to record(number and gray value matrix)
    public List<ImageModel> getImageModel(List<String> imageList){
        List<ImageModel> list = new ArrayList<ImageModel>();
        for(String item:imageList){
            try {
                BufferedImage bimage = ImageIO.read(new File(item));
                //resize to 28*28
                Image smallImage = bimage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
                BufferedImage bSmallImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
                Graphics graphics1 = bSmallImage.getGraphics();
                graphics1.drawImage(smallImage, 0, 0, null);
                graphics1.dispose();

                //get gray value
                int[] pixes = new int[width*height];
                double[] grayMatrix = new double[width*height];
                int index = -1;
                pixes = (int[])bSmallImage.getRaster().getDataElements(0,0,width,height,pixes);
                for(int i=0;i<width;i++){
                    for(int j=0;j<height;j++){
                        int rgb = pixes[i*width+j];
                        int r = (rgb & 0xff0000) >> 16;
                        int g = (rgb & 0xff00) >> 8;
                        int b = (rgb & 0xff);
                        double gray = Double.valueOf(r * 299 + g * 587 + b * 114 + 500)/255000.0;

                        grayMatrix[++index] = gray;
                    }
                }

                Integer digit = Integer.parseInt(new File(item).getName().split("_")[0]);
                ImageModel curModel = new ImageModel(grayMatrix, digit);
                list.add(curModel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    public double[] getGrayMatrixFromPanel(com.neuralnetwork.sample.ui.Canvas canvas, int[] outline){
        Dimension imageSize = canvas.getSize();
        BufferedImage image = new BufferedImage(imageSize.width,imageSize.height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        canvas.paint(graphics);
        graphics.dispose();

        //cut
        if(outline != null){
            if(outline[0] + outline[2] > canvas.getWidth()){
                outline[2] = canvas.getWidth()-outline[0];
            }
            if(outline[1] + outline[3] > canvas.getHeight()){
                outline[3] = canvas.getHeight()-outline[1];
            }
            image = image.getSubimage(outline[0],outline[1],outline[2],outline[3]);
        }
        //resize to 28*28
        Image smallImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage bSmallImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics graphics1 = bSmallImage.getGraphics();
        graphics1.drawImage(smallImage, 0, 0, null);
        graphics1.dispose();

        //get gray value
        int[] pixes = new int[width*height];
        double[] grayMatrix = new double[width*height];
        int index = -1;
        pixes = (int[])bSmallImage.getRaster().getDataElements(0,0,width,height,pixes);
        for(int i=0;i<width;i++){
            for(int j=0;j<height;j++){
                int rgb = pixes[i*width+j];
                int r = (rgb & 0xff0000) >> 16;
                int g = (rgb & 0xff00) >> 8;
                int b = (rgb & 0xff);
                double gray = Double.valueOf(r * 299 + g * 587 + b * 114 + 500)/255000.0;

                grayMatrix[++index] = gray;
            }
        }
        return grayMatrix;
    }

    public int[] transGrayToBinaryValue(double[] input){
        int[] binaryArray = new int[input.length];
        for(int i=0;i<input.length;i++){
            if(Double.compare(0.7, input[i]) >= 0){
                binaryArray[i] = 1;
            }else{
                binaryArray[i] = 0;
            }
        }
        return binaryArray;
    }
}

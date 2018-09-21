#include <iostream>  
#include "opencv2/imgproc/imgproc.hpp"  
#include "opencv2/highgui/highgui.hpp"  
#include <stdlib.h>  
#include <stdio.h>  
#include <opencv2/opencv.hpp>  

using namespace std;  
using namespace cv;  
#pragma comment(linker, "/subsystem:\"windows\" /entry:\"mainCRTStartup\"")  

const char* wnd_binary = "binary";  
const char* wnd_X = "vertical";  
const char* wnd_Y = "horizontal";   

extern "C" {
    void colorFilter(CvMat *inputImage, CvMat *&outputImage)  
    {  
        int i, j;  
        IplImage* image = cvCreateImage(cvGetSize(inputImage), 8, 3);  
        cvGetImage(inputImage, image);      
        IplImage* hsv = cvCreateImage( cvGetSize(image), 8, 3 );    
          
        cvCvtColor(image,hsv,CV_BGR2HSV);  
        int width = hsv->width;  
        int height = hsv->height;  
        for (i = 0; i < height; i++)  
            for (j = 0; j < width; j++)  
            {  
                CvScalar s_hsv = cvGet2D(hsv, i, j);//获取像素点为（j, i）点的HSV的值   
                /* 
                    opencv 的H范围是0~180，红色的H范围大概是(0~8)∪(160,180)  
                    S是饱和度，一般是大于一个值,S过低就是灰色（参考值S>80)， 
                    V是亮度，过低就是黑色，过高就是白色(参考值220>V>50)。 
                */  
                CvScalar s;  
                if (!((((s_hsv.val[0]>0)&&(s_hsv.val[0]<8)) || (s_hsv.val[0]>160)&&(s_hsv.val[0]<180))&& (s_hsv.val[2] > 80 && s_hsv.val[2] < 180))) 
                {  
                    s.val[0] =0;  
                    s.val[1]=0;  
                    s.val[2]=0;  
                    cvSet2D(hsv, i ,j, s);  
                }            
            }  
        outputImage = cvCreateMat( hsv->height, hsv->width, CV_8UC3 );  
        cvConvert(hsv, outputImage);  
        //cvNamedWindow("filter");  
        //cvShowImage("filter", hsv);  
        cvSaveImage("Filter.JPG", hsv);
        //waitKey(0);  
        cvReleaseImage(&hsv);  
    }  

    //图片旋转操作 
    void imrotate(Mat& img, Mat& newIm, double angle){
        int len = max(img.cols, img.rows);
        Point2f pt(len/2.,len/2.);
        Mat r = getRotationMatrix2D(pt,angle,1.0);
        warpAffine(img,newIm,r,Size(len,len));
        //better performance : 
        //Point2f pt(img.cols/2.,img.rows/2.);
        //Mat r = getRotationMatrix2D(pt,angle,1.0);
        //warpAffine(img,newIm,r,img.size());
    }


    int projection(char* name1, char* name2)  
    {
        
        char pic_name[100] = {0};
        IplImage *pSrcImage1 = cvLoadImage(name1, CV_LOAD_IMAGE_UNCHANGED);  
        CvMat temp;  
        CvMat* mat = cvGetMat(pSrcImage1, &temp); 
        CvMat* mat1 = NULL;
        colorFilter(mat, mat1) ;

        Mat origin_pic = Mat(mat, true); 
        Mat new_pic;

        Mat old_src = Mat(mat1, true); 
        Mat src;
        Mat src_gray,src_binary,paintX,paintY;  
        int min_sum = 999999;
        float min_i = 0.0,max_i = 0.0;
        Mat roi_img;

        for(float i=-4; i <4; i+=0.2){
            imrotate(old_src,src,i);
            Mat src_gray,src_binary;  
          
            cvtColor(src, src_gray, CV_RGB2GRAY);  
            //二值化图像  
            threshold(src_gray, src_binary, 60, 255, CV_THRESH_BINARY);
            //adaptiveThreshold(src_gray, src_binary, 255, ADAPTIVE_THRESH_GAUSSIAN_C, THRESH_BINARY_INV, 31, 10);  
            int* v = new int[src.cols];  
            int* h = new int[src.rows];  
            memset(v, 0, sizeof(int)*src.cols);  
            memset(h, 0, sizeof(int)*src.rows);  

            int x,y;  
            for( x=0; x<src_binary.cols; x++)  
            {         
                for(y=0; y<src_binary.rows; y++)  
                {  
                    uchar* myptr_v = src_binary.ptr<uchar>(y);        //逐行扫描，返回每行的指针  
                    if( myptr_v[x] == 255 )  
                      v[x]++;    
                }  
            }  
            for( x=0; x<src_binary.rows; x++)  
            {  
                uchar* myptr_h = src_binary.ptr<uchar>(x);  
                for(y=0; y<src_binary.cols; y++)  
                {  
                    if( myptr_h[y] == 255 )  
                        h[x]++;  
                }  
            }  

            int v_sum= 0, h_sum = 0;
            for (int j = 0; j < src.cols; j++)
                if (v[j] > 0)
                    v_sum++;
            for (int j = 0; j < src.rows; j++)
                if (h[j] > 0)
                    h_sum++;
            if (v_sum + h_sum < min_sum){
                printf("%f\t%d\n", i, v_sum + h_sum); 
                min_sum = v_sum + h_sum;
                min_i = i;
                max_i = i; 
            }
            else if (v_sum + h_sum == min_sum){
                printf("%f\t%d\n", i, v_sum + h_sum);
                max_i = i;     
            }
            else
                printf("%f\t%d~~~~~~~~~~~\n", i, v_sum + h_sum);
            free(v);
            free(h);
        }
        cout << min_i<<" "<< max_i<<endl;

        imrotate(old_src,src,(min_i + max_i)/2);
      
        paintX = Mat::zeros( src.rows, src.cols, CV_8UC1 );         
        paintY = Mat::zeros( src.rows, src.cols, CV_8UC1 );  
      
      
        cvtColor(src, src_gray, CV_RGB2GRAY);  
        //二值化图像  
        threshold(src_gray, src_binary, 60, 255, CV_THRESH_BINARY);
        int* v = new int[src.cols];  
        int* h = new int[src.rows];  
        memset(v, 0, src.cols*sizeof(int));  
        memset(h, 0, src.rows*sizeof(int));  

        int x,y;  
        for( x=0; x<src_binary.cols; x++)  
        {         
            for(y=0; y<src_binary.rows; y++)  
            {  
                uchar* myptr_v = src_binary.ptr<uchar>(y);        //逐行扫描，返回每行的指针  
                if( myptr_v[x] == 255 )  
                  v[x]++;    
            }  
        }  
        for( x=0; x<src_binary.cols; x++)  
        {  
            for(y=0; y<v[x]; y++)  
            {  
                uchar* myptr_x = paintX.ptr<uchar>(y);  
                myptr_x[x] = 255;  
            }  
        }  
        for( x=0; x<src_binary.rows; x++)  
        {  
            uchar* myptr_h = src_binary.ptr<uchar>(x);  
            for(y=0; y<src_binary.cols; y++)  
            {  
                if( myptr_h[y] == 255 )  
                    h[x]++;  
            }  
        }  
        for( x=0; x<src_binary.rows; x++)  
        {  
            uchar* myptr_y = paintY.ptr<uchar>(x);  
            for(y=0; y<h[x]; y++)  
            {  
                myptr_y[y] = 255;  
            }  
        }    

        int row1 = 0, row2 = 0, col1 = 0, col2 = 0;
        for (int j = 0; j < src.cols; j++)
            if (v[j] > 50){
                row1 = j;
                break;
            }
        for (int j = src.cols-1; j >= 0 ; j--)
            if (v[j] > 50){
                row2 = j; 
                break;      
            }
        for (int j = 0; j < src.rows; j++)
            if (h[j] > 50){
                col1 = j;
                break;
            }
        for (int j = src.rows-1; j >= 0 ; j--)
            if (h[j] > 50){
                col2 = j;
                break;
            }
        cout << row1<<" "<< row2<<" "<< col1<<" "<<col2<<" "<<endl;
        free(v);
        free(h);
        imwrite("Filter_rotate.jpg",src);
        imwrite("Filter_gray.jpg",src_gray);
        imwrite("Filter_binary.jpg",src_binary);
        imrotate(origin_pic,new_pic,(min_i + max_i)/2);
        int kuochong = 15;
        Rect rect(row1-(row2/kuochong-row1/kuochong), col1-(row2/kuochong-row1/kuochong), row2-row1+2*(row2/kuochong-row1/kuochong), col2-col1+2*(row2/kuochong-row1/kuochong));
        try{
            new_pic(rect).copyTo(roi_img);
            imwrite(name2,roi_img);
        }
        catch(...){
            cout<<"图片格式错误，请输入一张印章为中心，没有其他红色干扰的图片，且印章周围留有足够大的空隙"<<endl;
            return -1;
        } 
            /*
            namedWindow(wnd_binary, CV_WINDOW_AUTOSIZE);  
            namedWindow(wnd_X, CV_WINDOW_AUTOSIZE);  
            namedWindow(wnd_Y, CV_WINDOW_AUTOSIZE);  
            //显示图像  
            imshow(wnd_binary, src_binary);  
            imshow(wnd_X, paintX);  
            imshow(wnd_Y, paintY);  
            waitKey(0);
    */
        return 0;  
    }
}
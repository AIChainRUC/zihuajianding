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
                if (!((((s_hsv.val[0]>0)&&(s_hsv.val[0]<8)) || (s_hsv.val[0]>160)&&(s_hsv.val[0]<180))&&  s_hsv.val[1] > 80 &&   (s_hsv.val[2] > 130 && s_hsv.val[2] < 220))) 
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
        cvSaveImage("Filter.jpg", hsv);
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


    int rotate(char* im1, char* im2, char* im3)  
    {
        char pic_name[100] = {0};
        IplImage *pSrcImage1 = cvLoadImage(im1, CV_LOAD_IMAGE_UNCHANGED);  
        CvMat temp;  
        CvMat* mat = cvGetMat(pSrcImage1, &temp); 
        CvMat* mat1 = NULL;
        colorFilter(mat, mat1);

        Mat origin_pic = Mat(mat, true); 
        Mat new_pic;

        Mat old_src = Mat(mat1, true); 
        Mat src;
        Mat src_gray,src_binary,paintX,paintY;  
        cvtColor(old_src, src_gray, CV_RGB2GRAY);
        threshold(src_gray, src_binary, 60, 255, CV_THRESH_BINARY);
        imwrite("Filter_binary.jpg",src_binary);


        paintX = Mat::zeros( src_binary.rows, src_binary.cols, CV_8UC1 );         
        paintY = Mat::zeros( src_binary.rows, src_binary.cols, CV_8UC1 );  
        int* v = new int[src_binary.cols];  
        int* h = new int[src_binary.rows];  
        memset(v, 0, src_binary.cols*sizeof(int));  
        memset(h, 0, src_binary.rows*sizeof(int));  

        int x,y;  
        for( x=0; x<src_binary.cols; x++)  
        {         
            for(y=0; y<src_binary.rows; y++)  
            {  
                uchar* myptr_v = src_binary.ptr<uchar>(y);      
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
        for (int j = 0; j < src_binary.cols; j++)
            if (v[j] > 5){
                row1 = j;
                break;
            }
        for (int j = src_binary.cols-1; j >= 0 ; j--)
            if (v[j] > 5){
                row2 = j; 
                break;      
            }
        for (int j = 0; j < src_binary.rows; j++)
            if (h[j] > 5){
                col1 = j;
                break;
            }
        for (int j = src_binary.rows-1; j >= 0 ; j--)
            if (h[j] > 5){
                col2 = j;
                break;
            }
        cout << row1<<" "<< row2<<" "<< col1<<" "<<col2<<" "<<endl;
        Mat roi_img,roi_img_resize;
        Rect rect(row1, col1, row2 - row1, col2 - col1);
        //origin_pic(rect).copyTo(roi_img);
        //imwrite("qiege.jpg",roi_img);
        src_binary(rect).copyTo(roi_img);
        resize(roi_img, roi_img_resize, Size(500, 500), (0, 0), (0, 0), INTER_LINEAR);
        imwrite("qiege.jpg",roi_img_resize);

        Point center(roi_img_resize.cols/2,roi_img_resize.rows/2);
        int radius = 200;
        int radius1 = 100; 
        circle(roi_img_resize, center, radius, Scalar(0, 0, 0), 2, 8, 0); 
        Mat image1(roi_img_resize.rows, roi_img_resize.cols, roi_img_resize.type(), Scalar(0, 0, 0));  
        printf("%d,%d\n",center.x,center.y);
        for (int x = 0; x < roi_img_resize.cols; x++)  
        {  
            for (int y = 0; y < roi_img_resize.rows; y++)  
            {  
                int temp = ((x - center.x) * (x - center.x) + (y - center.y) *(y - center.y));  
                if (temp < (radius * radius) && temp > (radius1 * radius1))  
                {  
                    image1.at<uchar>(Point(x, y))= roi_img_resize.at<uchar>(Point(x, y));   
                }
            }  
        } 
        imwrite("zhezhao.jpg",image1);



        int min_sum = 999999;
        float min_i = 0.0,max_i = 0.0;

        for(float i=-10; i <10 ; i+=0.1){
            Mat src_binary1,src_binary;  
            imrotate(image1,src_binary,i);
            threshold(src_binary, src_binary1, 60, 255, CV_THRESH_BINARY);
            imwrite("66.jpg",src_binary1);
            //cout << src_binary1.cols<<src_binary1.rows<<endl;
            int* v = new int[src_binary1.cols];  
            int* h = new int[src_binary1.rows];  
            memset(v, 0, sizeof(int)*src_binary1.cols);  
            memset(h, 0, sizeof(int)*src_binary1.rows);  

            int x,y;  
            for( x=0; x<src_binary1.cols; x++)  
            {         
                for(y=0; y<src_binary1.rows; y++)  
                {  
                    uchar* myptr_v = src_binary1.ptr<uchar>(y);        //逐行扫描，返回每行的指针  
                    if( myptr_v[x] == 255 )  
                      v[x]++;    
                }  
            }  
            for( x=0; x<src_binary1.rows; x++)  
            {  
                uchar* myptr_h = src_binary1.ptr<uchar>(x);  
                for(y=0; y<src_binary1.cols; y++)  
                {  
                    if( myptr_h[y] == 255 )  
                        h[x]++;  
                }  
            }  

            int v_sum= 0, h_sum = 0;
            for (int j = 0; j < src_binary1.cols; j++)
                if (v[j] > 0)
                    v_sum++;
            for (int j = 0; j < src_binary1.rows; j++)
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
            //else
                //printf("%f\t%d~~~~~~~~~~~\n", i, v_sum + h_sum);
            free(v);
            free(h);
        }
        cout << min_i<<" "<< max_i<<endl;

        Mat Result_pic,new_image;
        origin_pic(rect).copyTo(roi_img);
        resize(roi_img, roi_img_resize, Size(500, 500), (0, 0), (0, 0), INTER_LINEAR);
        imrotate(roi_img_resize,Result_pic,(min_i + max_i)/2);
        imrotate(image1,new_image,(min_i + max_i)/2);
        
        int radius2 = 252; 
        Point center1(Result_pic.cols/2,Result_pic.rows/2);
        circle(Result_pic, center1, radius2, Scalar(0, 0, 0), 2, 8, 0); 
        Mat image2(Result_pic.rows, Result_pic.cols, Result_pic.type(), Scalar(0, 0, 0));  
        printf("%d,%d\n",center1.x,center1.y);
        for (int x = 0; x < Result_pic.cols; x++)  
        {  
            for (int y = 0; y < Result_pic.rows; y++)  
            {  
                int temp = ((x - center1.x) * (x - center1.x) + (y - center1.y) *(y - center1.y));  
                if (temp < (radius2 * radius2))  
                {  
                    image2.at<Vec3b>(Point(x, y))[0]= Result_pic.at<Vec3b>(Point(x, y))[0];
                    image2.at<Vec3b>(Point(x, y))[1]= Result_pic.at<Vec3b>(Point(x, y))[1]; 
                    image2.at<Vec3b>(Point(x, y))[2]= Result_pic.at<Vec3b>(Point(x, y))[2];  
                }
            }  
        }    
        imwrite(im3,new_image);
        imwrite(im2,image2);

        return 0;  
    }
}
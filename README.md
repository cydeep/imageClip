# imageClip
基于photoview的放大缩小的裁剪功能

裁剪效果图

![image](https://github.com/cydeep/ImageEdit/blob/master/app/src/main/res/drawable/image_clip.gif)

怎样配置：
在您gradle中

allprojects {

        repositories {
        
            maven { url 'https://jitpack.io' }
        
        }
    
}
    
dependencies {

     implementation 'com.github.cydeep:imageClip:v1.0.0'
     
}  
入口
ImageClipActivity.startImageClipActivity(context,path);

返回：
  String path = data.getStringExtra("path");

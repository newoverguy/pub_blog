@Grab('com.xlson.groovycsv:groovycsv:1.0')

import com.xlson.groovycsv.CsvParser
import java.util.zip.ZipOutputStream  
import java.util.zip.ZipEntry  
import java.nio.channels.FileChannel

/*
sample data
=======
userId,extid,imageUrl,date
newoverguy,ca941b07-fd75-4b0c,http://www.xvfxvf.net/test_1384G/IMG_1384610278384.png,2013-11-16 22:58:58.976
newoverguy,ca941b07-fd75-4b0c,http://www.xvfxvf.net/test_1384609y_PNG/IMG_1384609715770.png,2013-11-16 22:52:13.723
showmethe,b82782e8-8c95-4f5e,http://www.xvfxvf.net/test_137816NG/IMG_1378169987695.png,2013-09-03 10:06:48.953
takeout,7b5f0c76-f076-4ece,http://www.xvfxvf.net/test_138042VbawO_JPEG/image.jpg,2013-09-29 17:32:12.102
takeout,7b5f0c76-f076-4ece,http://www.xvfxvf.net/test_1380446kl_JPEG/image.jpg,2013-09-29 17:31:38.539
takeout,7b5f0c76-f076-4ece,http://www.xvfxvf.net/test_1378207WsK2_JPEG/image.jpg,2013-09-03 20:27:49.372
hamburg,7b5f0c76-f076-4ece,http://www.xvfxvf.net/test_15hLwwC_JPEG/image.jpg,2013-09-03 20:26:17.614
kimkimkim,d3a4de17-e222-45,http://www.xvfxvf.net/test_137932t_PNG/IMG_1379321953655.png,2013-09-16 18:00:41.928
==========
*/

def rootPath = '/Users/nhn/Downloads'
//def filePath = rootPath + '/test_images_sample.csv'
def filePath = rootPath + '/test_images.csv'
def fileReader = new FileReader(filePath)

def saveDir = rootPath + '/test_photos'

def csvDatas = new CsvParser().parse(fileReader, separator: ',')

def previousUserId
def currentFileSeq = 0;
def currentSaveImgPath;
def currentZipImgPath;

def loopCnt = 0;
def proccessUserCnt = 0;

for(data in csvDatas) {
    
    if(previousUserId != data.userId) {
        // 이전 사용자 이미지 zipping
        if(previousUserId != null) {
          def previousSaveImgPath = currentSaveImgPath;
          def previousZipImgPath = currentZipImgPath;
          zipping(previousZipImgPath + '/' + previousUserId + '.zip', previousSaveImgPath);
          
          println "zipping ==&gt;" + previousUserId;
        }
        
        // 사용자를 위한 리소스 준비.
        currentZipImgPath =  saveDir + '/' + data.extid
        currentSaveImgPath = saveDir + '/' + data.extid + '/images'
        
        def zipFileDir =  new File(currentZipImgPath);
        if(zipFileDir.exists()) {
           zipFileDir.delete() 
        }
        zipFileDir.mkdir()
        
        def imageFileDir =  new File(currentSaveImgPath);
        if(imageFileDir.exists()) {
           imageFileDir.delete() 
        }
        imageFileDir.mkdir()
        
        currentFileSeq = 0;
        
        println "proccessUserCnt ==&gt; " + ++proccessUserCnt;
    }
    
    currentFileSeq++;
    def fileName = data.userId + currentFileSeq + data.imageUrl[data.imageUrl.lastIndexOf('.')..data.imageUrl.size()-1];
    
    download(currentSaveImgPath + '/' + fileName, data.imageUrl)
    
    previousUserId = data.userId;
    
    println "loopCnt ==&gt; " + ++loopCnt;
}

zipping(currentZipImgPath + '/' + previousUserId + '.zip', currentSaveImgPath);

def download(filePath, address) {
    try {
        def file = new FileOutputStream(filePath)
        def out = new BufferedOutputStream(file)
        out &lt;&lt; new URL(address).openStream()
        out.close()
    } catch(e) {
        println e
    }
}


def zipping(zipFilePath, baseDir) {
    new AntBuilder().zip(
       destfile: zipFilePath,
       basedir: baseDir
   )
}

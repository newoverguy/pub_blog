@Grab('com.xlson.groovycsv:groovycsv:1.0')

import com.xlson.groovycsv.CsvParser
import java.util.zip.ZipOutputStream  
import java.util.zip.ZipEntry  
import java.nio.channels.FileChannel

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

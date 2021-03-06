package com.codefactory.devtest.library;

/**
 * Created by kudlaty on 13/10/2016.
 */

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class UnzipLib {

    /**
     * extract ZIP file into extractFolder: for native library .so it's a "/lib/ folder inside app data folder.
     * @param zipFile
     * @param extractFolder
     * @throws Exception
     */
    public static void extracLib(String zipFile, String extractFolder) throws Exception{
        BufferedOutputStream dest = null;
        BufferedInputStream input = null;
        try{
            int BUFFER = 2048;
            File file = new File(zipFile);
            ZipFile zip = new ZipFile(file);
//			String newPath = extractFolder;

            File dir = new File(extractFolder);
            if(!dir.exists()) {
                throw new Exception(extractFolder+ "NOT EXIST!");
//				dir.mkdir();
            }

            Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

            while(zipFileEntries.hasMoreElements()){

                ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
                String currentEntry = entry.getName();
                Log.d("UNZIP_LIB", "in ZIP found file: "+currentEntry);

                File destFile = new File(extractFolder,currentEntry);

                if(!entry.isDirectory()){
                    input = new BufferedInputStream(zip.getInputStream(entry));
                    int currentByte;
                    byte[] data = new byte[BUFFER];

                    //write file from zip to file in dest folder
                    FileOutputStream out = new FileOutputStream(destFile);
                    dest = new BufferedOutputStream(out, BUFFER);

                    //read and write until last byte is encountered
                    while((currentByte = input.read(data,0,BUFFER)) != -1) dest.write(data, 0, currentByte);
                    Log.d("UNZIP_LIB", "file unziped: "+destFile.getAbsolutePath());

                }
            }

            dest.flush();
            dest.close();
            input.close();
        }catch(Exception e){
            e.printStackTrace();
            throw new Exception("UNZIP library error: "+e.getMessage());
        }
//		finally{
//
//			try {
//				dest.flush();
//				dest.close();
//				input.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//
//		}
    }

}
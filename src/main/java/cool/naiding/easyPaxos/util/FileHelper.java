package cool.naiding.easyPaxos.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import com.google.gson.Gson;

public class FileHelper {

	public static void createFileIfNotExist(String addr) throws IOException {
		File file = new File(addr);
		if (!file.exists()) {
			file.createNewFile();
		}
	}
	
	public static String readFromFile(String filename) {
		StringBuilder sb = new StringBuilder();
		File file = new File(filename);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        		String line = null;
            while ((line = reader.readLine()) != null) {  
            		sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        return null;
	}
	
	public static <T> T readJsonFile(String filename, Class<T> classOfT) {
		String content = FileHelper.readFromFile(filename);
		if (content == null || content.length() == 0) {
			System.err.println("Read config file error - " + filename);
			return null;
		}
		return new Gson().fromJson(content, classOfT);
	}
	
	public static void writeToFile(String addr, String data, boolean append) {
		try {
			FileWriter fileWriter = new FileWriter(addr, append);
			fileWriter.write(data);
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file)
    {
        if (!file.isFile())
        {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try
        {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1)
            {
                digest.update(buffer, 0, len);
            }
            in.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }
}

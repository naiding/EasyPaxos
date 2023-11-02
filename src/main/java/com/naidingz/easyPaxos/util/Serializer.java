package com.naidingz.easyPaxos.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {
	public static byte[] serialize(Object object) {
		byte[] bytes = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(object);
			oos.flush();
			bytes = bos.toByteArray();
			oos.close();
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public static <T> T deserialize(byte[] byteArray, Class<T> classOfT) {
		T obj = null;
		try {
			ByteArrayInputStream bis = new ByteArrayInputStream(byteArray);
			ObjectInputStream ois = new ObjectInputStream(bis);
			obj = (T) ois.readObject();
			ois.close();
			bis.close();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return obj;
	}
}

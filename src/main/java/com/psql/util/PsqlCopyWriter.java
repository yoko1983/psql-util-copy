package com.psql.util;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.xml.bind.DatatypeConverter;

/**
 * This utility works equivalent to binary file creation with Postgresql's copy command.
 * 
 * @author yoko1983
 *
 */
public class PsqlCopyWriter {
	/**
	 * 署名
	 */
	private static byte[] SIGNATURE = 
		{(byte)0x50,(byte)0x47,(byte)0x43,(byte)0x4F,(byte)0x50,(byte)0x59,(byte)0x0A,(byte)0xFF,(byte)0x0D,(byte)0x0A,(byte)0x00};

	/**
	 * フラグフィールド
	 */
	private static byte[] FLAG_FIELD = 
		{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

	/**
	 * ヘッダー拡張領域長
	 */
	private static byte[] HEADER_EXTENSION_AREA_LENGTH = 
		{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};

	/**
	 * トレーラー
	 */
	private static byte[] TRAILER = 
		{(byte)0xFF,(byte)0xFF};

	/**
	 * NULL
	 */
	private static byte[] NULL = 
		{(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF};
	
	/**
	 * 空文字
	 */
	private static byte[] EMPTY_CHARACTER = 
		{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00};
	
	/**
	 * 最大カラム数
	 */
	private static int MAX_NUM_LENGTH = 32767; 

	/**
	 * ストリーム
	 */
	private BufferedOutputStream bos;
	
	/**
	 * ファイルをオープンする
	 * @param filePath - ファイルパス
	 * @throws IOException
	 */
	public void open(String filePath) throws IOException {
		bos = new BufferedOutputStream(new FileOutputStream(filePath));
		bos.write(SIGNATURE);
		bos.write(FLAG_FIELD);
		bos.write(HEADER_EXTENSION_AREA_LENGTH);
	}

	/**
	 * ファイルに書き込みを行う
	 * @param objs - オブジェクト型の配列
	 *                 文字列    ：String
	 *                 数値 2byte：Short/short
	 *                 数値 4byte：Integer/int
	 *                 数値 8byte：Long/long
	 *                 日付      ：LocalDate
	 *                 時間      ：LocalDateTime/LocalTime
	 *                 null      : null
	 * @throws IOException, IllegalArgumentException
	 */
	public void write(Object objs[]) throws IOException, IllegalArgumentException {
        	
    	int numLength =objs.length;
    	if(numLength >= MAX_NUM_LENGTH) {
    		throw new IllegalArgumentException("Maximum number of items specified.");
    		
    	}
    	bos.write(convertNumToHex(2, numLength));
    	
    	for(int i=0; i<numLength; i++) {
    		
    		//nullの場合
    		if(objs[i] == null) {
            	bos.write(NULL); 
    		}
    		//文字列の場合
    		else if(objs[i] instanceof String) {
    			if(objs[i].equals("")) {
                	bos.write(EMPTY_CHARACTER); 
    			} else {
        			byte[] item = ((String)objs[i]).getBytes();
                	bos.write(convertNumToHex(4, item.length));
                	bos.write(item); 
    			}
    		}
    		//日付の場合
    		else if(objs[i] instanceof LocalDate) {
    			LocalDate date = (LocalDate)objs[i];
    			//Postgresの仕様では紀元前4713以前も設定可能ではあるが、西暦開始以降を対象とする
    			//最大値はPostgresの仕様どおり。
    			if(date.isBefore(LocalDate.of(0001,01,01)) || 
    					date.isAfter(LocalDate.of(5874897,12,31))) {
    				throw new IllegalArgumentException("Unsupported date specified.");
    			}
    			//Postgresの仕様では2000/1/1以降を0,1,2...といったように0以降の数値で表現されている
    			//また、1999/12/31以前を-1,-2,-3...といったように-1以前の数値で表現されている
    			//このため、2000/1/1で減算することで、Postgresの日付として数値化する
    			long epocDay = date.toEpochDay()-LocalDate.of(2000,01,01).toEpochDay();
    			byte[] day = convertNumToHex(4, epocDay);
    			//1999/12/31以前はx'FFFFFFFFXXXXXXXX'となるため、x'FFFFFFFF'をトリミング
    			if(date.isBefore(LocalDate.of(2000,01,01))) {
    				byte[] day_ = new byte[4];
    				System.arraycopy(day, 4, day_, 0, 4);
    				day = day_;
    			}
            	bos.write(convertNumToHex(4, 4));
            	bos.write(day); 
    		}
    		//時間の場合
    		else if(objs[i] instanceof LocalTime) {
    			LocalTime time = (LocalTime)objs[i];
    			long secondOfDay = time.toSecondOfDay();
            	bos.write(convertNumToHex(4, 8));
            	bos.write(convertNumToHex(8, secondOfDay*1000000)); 
    		}
    		//時間の場合
    		else if(objs[i] instanceof LocalDateTime) {
    			LocalTime time = ((LocalDateTime)objs[i]).toLocalTime();
    			long secondOfDay = time.toSecondOfDay();
            	bos.write(convertNumToHex(4, 8));
            	bos.write(convertNumToHex(8, secondOfDay*1000000)); 
    		}
    		//数値（short 2byte）の場合
    		else if(objs[i] instanceof Short) {
            	bos.write(convertNumToHex(4, 2));
            	bos.write(convertNumToHex(2, (short) objs[i])); 
    		}
    		//数値（int 4byte）の場合
    		else if(objs[i] instanceof Integer) {
            	bos.write(convertNumToHex(4, 4));
            	bos.write(convertNumToHex(4, (int) objs[i])); 
    		}
    		//数値（long 8byte）の場合
    		else if(objs[i] instanceof Long) {
            	bos.write(convertNumToHex(4, 8));
            	bos.write(convertNumToHex(8, (long) objs[i])); 
    		} 
    		//未対応のクラスの場合
    		else {
    			throw new IllegalArgumentException("Unsupported class type specified.");
    		}
    	}
        	
		
	}
	
	/**
	 * クローズする。
	 * @throws IOException
	 */
	public void close() throws IOException {
		bos.write(TRAILER);
    	bos.close();
	}
	
	/**
	 * 数値をbyte配列に変換する
	 * @param degit - 桁数（バイト数）
	 * @param value - 数値
	 * @return
	 */
	private byte[] convertNumToHex(int degit, long value) {
    	String hex = Long.toHexString(value);
    	hex = String.format("%" + Integer.toString(degit*2) + "s", hex).replace(" ", "0");
    	return DatatypeConverter.parseHexBinary(hex);
    	
	}

}

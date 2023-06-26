package com.psql.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import org.junit.Test;

public class PsqlCopyWriterTest {

    @Test
//    @Description("PsqlCopyの正常データのテスト")
    public void test_testMethod00001() {
    	PsqlCopyWriter util = new PsqlCopyWriter();
    	Object[] objs1 = 
    		{"1", (short)1, 12, (long)123, LocalDate.of(      1, 1, 1), LocalTime.of(23, 59, 59), "a ", null};
    	Object[] objs2 = 
    		{"2", (short)2, 23, (long)234, LocalDate.of(   1999,12,31), LocalTime.of(00, 00, 00), "  ", "12345678abcdefg"};
    	Object[] objs3 = 
    		{"3", (short)3, 34, (long)345, LocalDate.of(   2000, 1, 1), LocalTime.of(12, 30, 30), " b", " "};
    	Object[] objs4 = 
    		{"4", (short)4, 45, (long)456, LocalDate.of(5874897,12,31), LocalTime.of(12, 30, 30), "ab", ""};

    	try {
        	util.open("target/"
        			+ new Object(){}.getClass().getEnclosingClass().getName()
        			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
			util.write(objs1);
			util.write(objs2);
			util.write(objs3);
			util.write(objs4);
        	util.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    @Test
//    @Description("PsqlCopyの正常データのテスト")
    public void test_testMethod00002() {
    	PsqlCopyWriter util = new PsqlCopyWriter();
    	Object[] objs1 = 
    		{LocalDateTime.of(LocalDate.of(      1, 1, 1), LocalTime.of(23, 59, 59))};

    	try {
        	util.open("target/"
        			+ new Object(){}.getClass().getEnclosingClass().getName()
        			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
			util.write(objs1);
        	util.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
    }

    
    @Test
//    @Description("PsqlCopyの0件データのテスト")
    public void test_testMethod00003() {
    	PsqlCopyWriter util = new PsqlCopyWriter();

    	try {
        	util.open("target/"
        			+ new Object(){}.getClass().getEnclosingClass().getName()
        			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
        	util.close();
		} catch (Exception e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
        //期待値を代入しておく
//        int expected = 3;
//        //実測値と期待値を比較する
//        assertEquals(actual, expected);
    }

    @Test(expected = IllegalArgumentException.class)
//    @Description("PsqlCopyの未対応データ（クラス）のテスト")
    public void test_testMethod90001() {
    	PsqlCopyWriter util = new PsqlCopyWriter();
    	
    	Object[] objs1 = 
    		{ new ArrayList<Object>() };

        	try {
            	util.open("target/"
            			+ new Object(){}.getClass().getEnclosingClass().getName()
            			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
	        	util.write(objs1);
	        	util.close();
			} catch (IllegalArgumentException e) {
				assertEquals("Unsupported class type specified.", e.getMessage());
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
    }

    @Test(expected = IllegalArgumentException.class)
//    @Description("PsqlCopyの未対応データ（日付最大値）のテスト")
    public void test_testMethod90002() {
    	PsqlCopyWriter util = new PsqlCopyWriter();
    	
    	Object[] objs1 = 
    		{ LocalDate.of(5874898, 1, 1) };

        	try {
            	util.open("target/"
            			+ new Object(){}.getClass().getEnclosingClass().getName()
            			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
	        	util.write(objs1);
	        	util.close();
			} catch (IllegalArgumentException e) {
				assertEquals("Unsupported date specified.", e.getMessage());
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
    }
    
    @Test(expected = IllegalArgumentException.class)
//    @Description("PsqlCopyの未対応データ（日付最小値）のテスト")
    public void test_testMethod90003() {
    	PsqlCopyWriter util = new PsqlCopyWriter();
    	
    	Object[] objs1 = 
    		{ LocalDate.of(0, 12, 31) };

        	try {
            	util.open("target/"
            			+ new Object(){}.getClass().getEnclosingClass().getName()
            			+ "-" + new Object(){}.getClass().getEnclosingMethod().getName() +".dat");
	        	util.write(objs1);
	        	util.close();
			} catch (IllegalArgumentException e) {
				assertEquals("Unsupported date specified.", e.getMessage());
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
			}
        	
    }

}

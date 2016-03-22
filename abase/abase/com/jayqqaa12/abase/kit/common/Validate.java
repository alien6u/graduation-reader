package com.jayqqaa12.abase.kit.common;


import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 校验工具类
 */
public class Validate {
	
	
			
	/**
	 * equals
	 * @param str
	 * @return
	 */
	public static boolean equals (String s1,String s2)
	{
		return notEmpty(s1)&&notEmpty(s2) &&s1.equals(s2);
		
	}
	
	

	/**
	 * 验证字符串有效性
	 */
	public static boolean isEmpty(String str){
		
		return !notEmpty(str);
	}
	
	
	
	/**
	 * 验证字符串有效性
	 */
	public static boolean notEmpty(String str){
		if(str == null || "".equals(str.trim())){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 验证集合有效性
	 */
	public static boolean notEmpty(Collection col){
		if(col == null || col.isEmpty()){
			return false ;
		}
		return true ;
	}
	
	/**
	 * 验证数组有效性
	 */
	public static boolean notEmpty(Object[] arr){
		if(arr == null || arr.length == 0){
			return false ;
		}
		return true ;
	}
	
	
	/**
	 * 验证 邮箱有效性
	 * @param email
	 * @return
	 */
	
	 public static boolean isEmail(String email){     
		 if(isEmpty(email))return false;

		 String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
	        Pattern p = Pattern.compile(str);     
	        Matcher m = p.matcher(email);     
	        return m.matches();     
	    } 
	
	
}
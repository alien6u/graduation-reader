package com.jayqqaa12.abase.core;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.EBean.Scope;

import com.squareup.otto.BasicBus;

/***
 * 
 * 接受的参数必需为对象 
 * 如 int 改成 Integer
 * 
 * 
 * 使用前 请先注册  regist(this)
 * 后完了 unregist(this)
 * @author 12
 *
 */
@EBean(scope = Scope.Singleton)
public class Abus extends BasicBus {

	
	
}
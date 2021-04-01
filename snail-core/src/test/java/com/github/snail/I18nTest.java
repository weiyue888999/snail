package com.github.snail;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18nTest {

	//@org.junit.Test
	public void test() {
        // 设置定制的语言国家代码 
        Locale locale1 = new Locale("en");
        System.out.println(locale1.toLanguageTag());
        System.out.println(Locale.US.toLanguageTag());
        Locale locale2 = Locale.getDefault();
        System.out.println(locale2.toLanguageTag());
        
        // 获得资源文件 
        ResourceBundle rb1 = ResourceBundle.getBundle("message/i18n/snail", locale2);
        // 获得相应的key值 
        String userInfo1 = rb1.getString("snail.verify.tips.fail");
        
        System.out.println(userInfo1); 
        
        ResourceBundle rb2 = ResourceBundle.getBundle("message/i18n/snail", locale1);
        
        String userInfo2 = rb2.getString("snail.verify.tips.fail");
        
        System.out.println(userInfo2); 
    }
}

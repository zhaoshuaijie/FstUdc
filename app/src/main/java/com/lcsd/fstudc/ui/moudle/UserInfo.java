package com.lcsd.fstudc.ui.moudle;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/21.
 */
public class UserInfo implements Serializable{
    private	String	id;	/*6*/
    private	String	user;	/*test*/

    public void setId(String value){
        this.id = value;
    }
    public String getId(){
        return this.id;
    }

    public void setUser(String value){
        this.user = value;
    }
    public String getUser(){
        return this.user;
    }
}

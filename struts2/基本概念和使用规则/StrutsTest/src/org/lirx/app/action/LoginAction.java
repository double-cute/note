package org.lirx.app.action;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport {
	private String username;
	private String passwd;
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPasswd() {
		return passwd;
	}
	
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
	@Override
	public String execute() throws Exception {
		if (getUsername().equals("Peter") && getPasswd().equals("pass")) {
			ActionContext.getContext().getSession().put("user", getUsername());
			return SUCCESS;
		}
		
		return ERROR;
	}
}

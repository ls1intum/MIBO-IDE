package edu.tum.ls1.mibo.editor.shared.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TargetGroup implements IsSerializable, Domain {

	// Identifier
	private String identifier;
	
	// Target group's users
	private User generic_user;
	private List<User> custom_users;

	public TargetGroup() {
		this.custom_users = new ArrayList<User>();
	}
	
	public TargetGroup(String identifier){
		this.custom_users = new ArrayList<User>();
		this.identifier = identifier;
	}

	// ---------- GET/SET ----------

	public void setIdentifier(String identifier){
		this.identifier = identifier;
	}
	
	public String getIdentifier(){
		return this.identifier;
	}
	
	public void setGenericUser(User user){
		this.generic_user = user;
	}
	
	public User getGenericUser(){
		return this.generic_user;
	}
	
	public void addCustomUser(User user){
		this.custom_users.add(user);
	}
	
	public void setCustomUsers(List<User> users){
		this.custom_users = users;
	}
	
	public List<User> getCustomUsers(){
		return this.custom_users;
	}
	
	// Try to generate displayName by getting the word after the last point
	@Override
	public String getDisplayName(){
		int position = this.identifier.lastIndexOf(".");
		if(position>=-1){
			String name = this.identifier.substring(position+1, this.identifier.length());
			String capitalizedName = name.substring(0, 1).toUpperCase() + name.substring(1);
			return capitalizedName;
		}
		return this.identifier;
	}
	
	@Override
	public String getDisplaySubName(){
		return this.identifier;
	}

}

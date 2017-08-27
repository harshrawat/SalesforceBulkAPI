package loginHandling.userinformation;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "UserInformation")
public class UserInformation {

	private String username;
	private String password;
	private String securityToken;
	private String orgtype;

	public String getOrgtype() {
		return orgtype.trim();
	}

	public void setOrgtype(String orgtype) {
		this.orgtype = orgtype;
	}

	public UserInformation(String username, String password, String securityToken, String orgtype) {
		this.username = username;
		this.password = password;
		this.securityToken = securityToken;
		this.orgtype = orgtype;
	}

	public String getUsername() {
		return username.trim();
	}

	public UserInformation() {
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password.trim();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecurityToken() {
		return securityToken.trim();
	}

	public void setSecurityToken(String securityToken) {
		this.securityToken = securityToken;
	}

}

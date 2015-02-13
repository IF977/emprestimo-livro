package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Usuário da aplicação.
 */
@Entity
@SuppressWarnings("serial")
public class UserModel extends Model {

	@Id
	@Required
	private String email;
	
	@Required
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}

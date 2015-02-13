package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import play.data.validation.Constraints.Required;
import play.db.ebean.Model;


/**
 * Livros.
 */
@Entity
@SuppressWarnings("serial")
public class BookModel extends Model {

	@Id
	private Integer id;
	
	@Required
	private String name;
	
	@ManyToOne
	@JoinColumn(name="owner_id")
	private UserModel owner;
	
	@ManyToOne
	@JoinColumn(name="holder_id")	
	private UserModel holder;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UserModel getOwner() {
		return owner;
	}

	public void setOwner(UserModel owner) {
		this.owner = owner;
	}

	public UserModel getHolder() {
		return holder;
	}

	public void setHolder(UserModel holder) {
		this.holder = holder;
	}
}

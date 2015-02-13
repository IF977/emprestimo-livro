package controllers;

import java.util.List;

import models.UserModel;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.avaje.ebean.Ebean;

/**
 * Página principal da aplicação (Página de Login) 
 */
public class Application extends Controller {

    public static Result index() {
        return ok(index.render(""));
    }
    
    /**
     * Na tela de login, verifica se o botão pressionado foi 'entrar' ou 'cadastrar'.
     */
    public static Result process() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String email = requestData.get("email");
        String password = requestData.get("password");

        if (requestData.get("entrar")!=null) {
        	return autenticate(email, password);
        } else if (requestData.get("cadastrar")!=null) {
        	return User.register();
        } else {
        	return badRequest("Erro! Ação inválida.");
        }
    }
    
    /**
     * Autentica o usuário para a área interna do sistema.
     */
    private static Result autenticate(String email, String password) {
    	List<UserModel> users = Ebean.find(UserModel.class).findList();
    	UserModel userFound = null;
    	for (UserModel user : users) {
    		if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
    			userFound = user;
    			break;
    		}
    	}

    	//usuario invalido
    	if (userFound == null) {
    		return ok(index.render("Usuário inválido!"));
    		//return unauthorized("Usuario invalido");
    	} 
    	
    	//usuario valido e autenticado
    	else {
    		session("connected", userFound.getEmail());
    	}

    	return Book.index();
    }
}

package controllers;

import org.apache.commons.lang3.StringUtils;

import models.UserModel;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.new_user;

import com.avaje.ebean.Ebean;

public class User extends Controller {

	/**
	 * Chama a tela de Cadastrar Usuário (@new_user.scala.html)
	 */
    public static Result register() {
        return ok(new_user.render("New User", "", null));
    }

    /**
     * Na tela de Novo Usuario, verifica se o botão pressionado foi 'salvar' ou 'voltar'.
     */
    public static Result new_user() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String email = requestData.get("email");
        String password = requestData.get("password");

        if (requestData.get("salvar")!=null) {
        	return save(email, password);
        } else if (requestData.get("voltar")!=null) {
        	return Application.index();
        } else {
        	return badRequest("Erro! Ação inválida.");
        }
    }

    /**
     * Salva um novo usuário.
     */
    public static Result save(String email, String password) {
        UserModel user = new UserModel();
       
        if (StringUtils.isBlank(email)) {
        	return ok(new_user.render("New User", "O campo 'E-mail' é obrigatório.", null));
        }
        
        else if (StringUtils.isBlank(password)) {
        	return ok(new_user.render("New User", "O campo 'Senha' é obrigatório.", null));
        }

        UserModel emailJaCadastrado = Ebean.find(UserModel.class).where("email = '"+email+"'").findUnique();
        if (emailJaCadastrado != null) {
        	return ok(new_user.render("New User", "E-mail já cadastrado.", null));
        }

        user.setEmail(email);
        user.setPassword(password);

        Ebean.save(user);
        
        return ok(new_user.render("New User", "Usuario cadastrado com sucesso!", null));
    }
}

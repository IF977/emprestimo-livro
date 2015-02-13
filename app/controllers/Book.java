package controllers;

import org.apache.commons.lang3.StringUtils;

import models.BookModel;
import models.UserModel;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.privates.book_menu;
import views.html.privates.book_register;
import views.html.privates.book_request;

import com.avaje.ebean.Ebean;

/**
 * Controlador de Empréstimo de Livros. 
 */
public class Book extends Controller {

	public static Result index() {
		return ok(book_menu.render("Menu Principal"));
	}

	public static Result process() {
        DynamicForm requestData = Form.form().bindFromRequest();

        if (requestData.get("cadastrar")!=null) {
        	return ok(book_register.render("Cadastrar Livro", ""));
        } else if (requestData.get("solicitar")!=null) {
        	return ok(book_request.render("Solicitar Livro", ""));
        } else if (requestData.get("liberar")!=null) {
        	return badRequest("Erro! Ação inválida.");
        } else if (requestData.get("sair")!=null) {
        	session().clear();
        	return Application.index();
        } else {
        	return badRequest("Erro! Ação inválida.");
        }
	}
	
    public static Result new_book() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String nome = requestData.get("nome");

        if (requestData.get("salvar")!=null) {
        	return register(nome);
        } else if (requestData.get("voltar")!=null) {
        	return index();
        } else {
        	return badRequest("Erro! Ação inválida.");
        }
    }

	/**
	 * Salva um novo livro.
	 */
	public static Result register(String nome) {
		String emailUsuarioLogado = session().get("connected");
		UserModel user = Ebean.find(UserModel.class).where("email = '"+emailUsuarioLogado+"'").findUnique();
		
		if (StringUtils.isBlank(nome)) {
			return ok(book_register.render("Cadastrar Livro", "O campo 'Nome' é obrigatório."));
		}
		
        BookModel book = new BookModel();
        book.setName(nome);
        book.setOwner(user);

        Ebean.save(book);
        
        return ok(book_register.render("Cadastrar Livro", "Livro cadastrado com sucesso!"));
	}
	
	/**
	 * Solicita um livro para empréstimo
	 */
	public static Result request_book() {
		return null;
	}
}

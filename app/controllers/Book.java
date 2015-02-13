package controllers;

import java.util.ArrayList;
import java.util.List;

import models.BookModel;
import models.UserModel;

import org.apache.commons.lang3.StringUtils;

import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.privates.book_borrow;
import views.html.privates.book_borrowed;
import views.html.privates.book_menu;
import views.html.privates.book_register;
import views.html.privates.book_request;
import views.html.privates.book_return;

import com.avaje.ebean.Ebean;

/**
 * Controlador de Empréstimo de Livros. 
 */
public class Book extends Controller {

	private static List<BookModel> booksSelecteds;
	private static List<BookModel> booksToBorrow;
	private static List<BookModel> booksBorrowed;
	private static List<BookModel> booksToReturn;

	public static Result index() {
		return ok(book_menu.render("Menu Principal", ""));
	}

	public static Result process() {
        DynamicForm requestData = Form.form().bindFromRequest();

        if (requestData.get("cadastrar")!=null) {
        	return ok(book_register.render("Cadastrar Livro", ""));
        } else if (requestData.get("solicitar")!=null) {
        	booksSelecteds = getBooksFromOtherUsers();
        	if (booksSelecteds.isEmpty()) {
        		return ok(book_request.render("Solicitar Livros", "Nenhum Livro Cadastrado", booksSelecteds));
        	}
        	return ok(book_request.render("Solicitar Livros", "", getBooksFromOtherUsers()));
        } else if (requestData.get("liberar")!=null) {
        	booksToBorrow = getBooksToBorrow();
        	if (booksToBorrow.isEmpty()) {
        		return ok(book_borrow.render("Solicitar Livros", "Nenhum Livro foi Solicitado", booksToBorrow));
        	}
        	return ok(book_borrow.render("Liberar Livros", "", booksToBorrow));
        } else if (requestData.get("quem_pegou")!=null) {
        	booksBorrowed = getWhoTookTheBook();
        	if (booksBorrowed.isEmpty()) {
        		return ok(book_borrowed.render("Quem Pegou", "Nenhum Emprestado", booksBorrowed));
        	}
        	return ok(book_borrowed.render("Quem Pegou", "", booksBorrowed));
        } else if (requestData.get("devolver")!=null) {
        	booksToReturn = getBooksToReturn();
        	if (booksToReturn.isEmpty()) {
        		return ok(book_return.render("Devolver Livro", "Você não tem nenhum livro para devolver", booksToReturn));
        	}
        	return ok(book_return.render("Devolver Livro", "", booksToReturn));
        } else if (requestData.get("sair")!=null) {
        	session().clear();
        	return Application.index();
        } else {
        	return badRequest("Erro! Ação inválida.");
        }
	}
	
	/**
	 * Consulta os livros dos demais usuários que estão livres para empréstimos.
	 */
	public static List<BookModel> getBooksFromOtherUsers() {
    	String emailUsuarioLogado = session().get("connected");
	    List<BookModel> books = Ebean.find(BookModel.class).findList();
	    List<BookModel> booksNames = new ArrayList<BookModel>();
	    for (BookModel model : books) {
	    	if (!model.getOwner().getEmail().equals(emailUsuarioLogado)
				&& model.getHolder() == null
				&& model.getRequester() == null) {
	    		booksNames.add(model);
	    	}
	    }
	    
	    return booksNames;
	}
	
	/**
	 * Consulta os livros do usuário logado que outros usuários solicitando para empréstimo.
	 */
	public static List<BookModel> getBooksToBorrow() {
		String emailUsuarioLogado = session().get("connected");
		List<BookModel> books = Ebean.find(BookModel.class).findList();
		List<BookModel> booksNames = new ArrayList<BookModel>();
		for (BookModel model : books) {
			if (model.getOwner().getEmail().equals(emailUsuarioLogado)
					&& model.getHolder() == null
					&& model.getRequester() != null) {
				booksNames.add(model);
			}
		}
		
		return booksNames;
	}
	
	/**
	 * Consulta os livros do usuário logado que estão emprestados para outros usuários.
	 */
	public static List<BookModel> getWhoTookTheBook() {
		String emailUsuarioLogado = session().get("connected");
		List<BookModel> books = Ebean.find(BookModel.class).findList();
		List<BookModel> booksNames = new ArrayList<BookModel>();
		for (BookModel model : books) {
			if (model.getOwner().getEmail().equals(emailUsuarioLogado)
					&& model.getHolder() != null
					&& model.getRequester() == null) {
				booksNames.add(model);
			}
		}
		
		return booksNames;
	}
	
	/**
	 * Consulta os livros que o usuário logado pegou emprestado.
	 */
	public static List<BookModel> getBooksToReturn() {
		String emailUsuarioLogado = session().get("connected");
		List<BookModel> books = Ebean.find(BookModel.class).findList();
		List<BookModel> booksNames = new ArrayList<BookModel>();
		for (BookModel model : books) {
			if (!model.getOwner().getEmail().equals(emailUsuarioLogado)
					&& model.getHolder() != null
					&& model.getHolder().getEmail().equals(emailUsuarioLogado)
					&& model.getRequester() == null) {
				booksNames.add(model);
			}
		}
		
		return booksNames;
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
		DynamicForm requestData = Form.form().bindFromRequest();
		if (requestData.get("voltar") != null) {
			return Book.index();
		} else if (requestData.get("solicitar") != null){
			
	        List<BookModel> selecteds = new ArrayList<BookModel>();
	        for (BookModel book : booksSelecteds) {
	        	if (requestData.get(book.getId().toString()) != null) {
	        		selecteds.add(book);
	        	}
	        }
	        
	        if (selecteds.isEmpty()) {
	        	return ok(book_request.render("Solicitar Livros", "Favor selecionar pelo menos um livro.", booksSelecteds));
	        }

	        String emailUsuarioLogado = session().get("connected");
	        UserModel user = Ebean.find(UserModel.class).where("email = '"+emailUsuarioLogado+"'").findUnique();
	        for (BookModel selected : selecteds) {
	        	selected.setRequester(user);
	        	Ebean.update(selected);
	        }

	        return ok(book_menu.render("Menu Principal", "Livros solicitados com sucesso!"));
		} else {
			return badRequest("Erro! Ação inválida.");
		}
	}
	
	/**
	 * Libera um livro para empréstimo
	 */
	public static Result borrow_book() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (requestData.get("voltar") != null) {
			return Book.index();
		} else if (requestData.get("emprestar") != null){
			
	        List<BookModel> selecteds = new ArrayList<BookModel>();
	        for (BookModel book : booksToBorrow) {
	        	if (requestData.get(book.getId().toString()) != null) {
	        		selecteds.add(book);
	        	}
	        }
	        
	        if (selecteds.isEmpty()) {
	        	return ok(book_borrow.render("Emprestar Livros", "Favor selecionar pelo menos um livro.", booksToBorrow));
	        }

	        for (BookModel selected : selecteds) {
	        	UserModel user = selected.getRequester();
	        	selected.setHolder(user);
	        	selected.setRequester(null);
	        	Ebean.update(selected);
	        }

	        return ok(book_menu.render("Menu Principal", "Livros emprestados com sucesso!"));
		} else {
			return badRequest("Erro! Ação inválida.");
		}
	}
	
	/**
	 * Devolve o livro para seu dono
	 */
	public static Result return_book() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (requestData.get("voltar") != null) {
			return Book.index();
		} else if (requestData.get("devolver") != null){
			
	        List<BookModel> selecteds = new ArrayList<BookModel>();
	        for (BookModel book : booksToReturn) {
	        	if (requestData.get(book.getId().toString()) != null) {
	        		selecteds.add(book);
	        	}
	        }
	        
	        if (selecteds.isEmpty()) {
	        	return ok(book_return.render("Devolver Livros", "Favor selecionar pelo menos um livro.", booksToReturn));
	        }

	        for (BookModel selected : selecteds) {
	        	selected.setHolder(null);
	        	selected.setRequester(null);
	        	Ebean.update(selected);
	        }

	        return ok(book_menu.render("Menu Principal", "Livros devolvido com sucesso!"));
		} else {
			return badRequest("Erro! Ação inválida.");
		}
	}

	/**
	 * Libera um livro para empréstimo
	 */
	public static Result borrowed_book() {
		DynamicForm requestData = Form.form().bindFromRequest();
		if (requestData.get("voltar") != null) {
			return Book.index();
		} else {
			return badRequest("Erro! Ação inválida.");
		}
	}
}

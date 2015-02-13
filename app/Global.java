import java.lang.reflect.Method;

import play.GlobalSettings;
import play.libs.F;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

/**
 * Classe criada para interceptar requisições, verificar se o usuário
 * está logado e, caso esteja, permite que ele prossiga,
 * caso não esteja, redireciona para a tela de login.
 */
public class Global extends GlobalSettings {

	@Override
	@SuppressWarnings("rawtypes")
	public Action onRequest(Request request, Method actionMethod) {
		return new Action.Simple() {
			public Promise<Result> call(Context ctx) throws Throwable {
				//se o usuario estiver logado ou acessando paginas publicas...
				//entao permite a requisicao
				if (ctx.request().path().equals("/") 
						|| ctx.request().path().equals("/process")
						|| ctx.request().path().equals("/new_user")
						|| ctx.session().get("connected") != null) {
					return delegate.call(ctx);
				} else {
					//caso o usuario nao esteja logado e esteja tentando acessar paginas publicas
					//nao permite a requisicao
					return F.Promise.pure((Result) unauthorized("Usuario nao autorizado"));
				}
		    }

		};
	}
}

package wf.garnier.spring.security.authorization.ott;

public interface MailNotifier {

	void notify(String title, String message, String link);

}

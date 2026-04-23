package wf.garnier.spring.security.authorization.ott;

class ConsoleMailNotifier implements MailNotifier {

	@Override
	public void notify(String title, String message, String link) {
		System.out.printf("📥 %s: %s%n", title, message);
	}

}

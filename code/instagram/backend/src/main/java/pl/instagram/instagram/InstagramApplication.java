package pl.instagram.instagram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InstagramApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstagramApplication.class, args);

		/*final String ACCOUNT_SID = "ACeeebfa6d5091578008aedcb507e84bd7";
		final String AUTH_TOKEN = "8adfc171197c043b04525b72c4373772";

		Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
		Message message = Message.creator(
						new com.twilio.type.PhoneNumber("+48739138994"),
						new com.twilio.type.PhoneNumber("+18124455066"),
						":DDDD Kamil")
				.create();

		System.out.println(message.getSid());*/
	}

}

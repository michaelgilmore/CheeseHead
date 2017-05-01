package cc.gilmore.games;

import java.net.MalformedURLException;
import java.net.URL;

public class OnlineTest {

	public static void main(String[] args) {
		URL u = null;
		try {
			//u = new URL("");
			u = new URL("http://www.example.com/");
			//u = new URL("http://www.notaexample.com/");
			//u = new URL("https://checkout.stripe.com/");
			//https://checkout.stripe.com/checkout.js
		} catch (MalformedURLException e) {
			System.out.println("Exception:" + e.getMessage());
			e.printStackTrace();
		}
		if(u == null) {
			System.out.println("null");
		}
		else {
			System.out.println("not null");
		}
	}

}

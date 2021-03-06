package everyst.analytics.twitter0Auth;

import java.util.Scanner;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * Taken from
 * https://github.com/scribejava/scribejava/blob/master/scribejava-apis/src/test/java/com/github/scribejava/apis/examples/TwitterExample.javas
 */
public abstract class AddSubscription {

	private static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account_activity/all/prod/subscriptions.json";

	public static void main(String... args) throws IOException, InterruptedException, ExecutionException {
		// Read api key
		final Scanner in = new Scanner(System.in);
		System.out.println("ConsumerKey: ");
		String consumerKey = in.nextLine();

		System.out.println("Secret: ");
		String secret = in.nextLine();

		final OAuth10aService service = new ServiceBuilder(consumerKey).apiSecret(secret).build(TwitterApi.instance());

		// Obtain the Request Token
		System.out.println("Fetching the Request Token...");
		final OAuth1RequestToken requestToken = service.getRequestToken();
		System.out.println("Got the Request Token!");
		System.out.println();

		System.out.println("Now go and authorize ScribeJava here:");
		System.out.println(service.getAuthorizationUrl(requestToken));
		System.out.println("And paste the verifier here");
		System.out.print(">>");
		final String oauthVerifier = in.nextLine();
		System.out.println();

		// Trade the Request Token and Verfier for the Access Token
		System.out.println("Trading the Request Token for an Access Token...");
		final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
		System.out.println("Got the Access Token!");
		System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
		System.out.println();

		// Now let's go and ask for a protected resource!
		System.out.println("Now we're going to access a protected resource...");
		final OAuthRequest request = new OAuthRequest(Verb.POST, PROTECTED_RESOURCE_URL);
		service.signRequest(accessToken, request);
		final Response response = service.execute(request);
		System.out.println("Got it! Lets see what we found...");
		System.out.println();
		System.out.println(response.getBody());

		System.out.println();
		System.out.println("User added! If the response did not show an error message!");
		in.close();

	}
}

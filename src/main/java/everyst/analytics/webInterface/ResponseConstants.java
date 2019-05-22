package everyst.analytics.webInterface;

public abstract class ResponseConstants {

	public static final String PRE = "<!doctype html>\n" + 
			"<html lang=\"en\">\n" + 
			"<head><!-- Required meta tags -->\n" + 
			"<meta charset=\"utf-8\">\n" + 
			"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, shrink-to-fit=no\">\n" + 
			"<style>\n" + 
			"table {\n" + 
			"margin-bottom: 50px;\n" + 
			"text-align: left;\n" + 
			"}\n" + 
			"tr:nth-child(even) {background-color: #f2f2f2;}\n" + 
			"</style>" + 
			"<title>Arc Webinterface!</title>\n" + 
			"</head>\n" + 
			"<body>\n" + 
			"<form action=\"./SimpleNumberOutput\">\n" + 
			"<select name=\"account\">\n" + 
			"<option value=\"Eddieededed\">Eddie</option>\n" + 
			"<option value=\"FromEveryst\">FromEveryst</option>\n" + 
			"<option value=\"Haunt_Jade\">Jade</option>\n" + 
			"<option value=\"CagerGabe\">Gabe</option>\n" + 
			"<option value=\"RW_Lecthel\">Lecthel</option>\n" + 
			"<option value=\"HMagKohaku\">Kohaku</option>\n" + 
			"</select> \n" + 
			"From: <input type=\"date\" name=\"from\">\n" + 
			"To: <input type=\"date\" name=\"to\">\n" + 
			"<input type=\"submit\" value = \"Get data\"></form>";
	
	
	public static final String AFTER = "</body></html>";

}

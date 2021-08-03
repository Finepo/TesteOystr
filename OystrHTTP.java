package Oystr;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OystrHTTP {
	private static HttpURLConnection connection;
	
	public static void main(String[] args) {		
		
		String urlInserido = "";
		String inputLine = "";
		
		urlInserido = "https://twitter.com";  
		
		if(!urlInserido.contains("https://")) {
			urlInserido = "https://" + urlInserido;
		}
		if(!urlInserido.contains("www.")) {
			urlInserido = urlInserido.split("/")[0] + "//www." + urlInserido.split("https://")[1];
		}
		try {
			URL url = new URL(urlInserido);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			
			if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				BufferedReader buffer = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
				StringBuffer content = new StringBuffer();
				while ((inputLine = buffer.readLine()) != null) {
				    content.append(inputLine);
				}
				String sourceCode = content.toString().toLowerCase();
				
				//Regex p/ encontrar Titulo
				String titulo = regex(sourceCode,"<title>(.+)</title>");
				if (titulo.isEmpty()) {
					System.out.println("Titulo nao encontrado");
				}
				else {
					System.out.println("Titulo da pag = " + titulo);
				}
				
				//Encontrar versao HTML
				if(sourceCode.contains("<!doctype html>")) {
					System.out.println("HTML Version: HTML 5");
				}
				else {
					String versao = regex(sourceCode, "\\s+(\\w{4,5}\\s+\\d\\.\\d+)");
					if (versao.isEmpty()){
						System.out.println("Erro ao encontrar versao HTML");
					}
					else {
						System.out.println("Versao do codigo: " + versao);
					}
				}
				
				//Regex encontrar links
				List <String> listaLinksInternos = new ArrayList<String>();
				List <String> listaLinksExternos = new ArrayList<String>();
				
				String nomeSite = urlInserido.split("\\.")[1];

				Pattern patternLinks = Pattern.compile("href=\\\"([^\\\"]+)");
				Matcher matchLinks = patternLinks.matcher(sourceCode);
				
				while (matchLinks.find()) {
					if (matchLinks.group(1).contains(nomeSite)) {
						listaLinksInternos.add(matchLinks.group(1));
					}
					else {
						listaLinksExternos.add(matchLinks.group(1));
					}
				}
				System.out.println("\nlinks externos: " + listaLinksExternos.size());
				System.out.println("links externos: " + listaLinksInternos.size());
			}
			else {
				System.out.println("erro ao estabelecer conexao");
			}
						
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			connection.disconnect();
		}

	}

	public static String regex(String HTML, String patternStr) {
		Pattern pattern = Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(HTML);
		if(matcher.find()){
			return matcher.group(1);
		}
		return "";
	}
}

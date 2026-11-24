package com.example.githubactivity;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main 
{ 
    public static void main(String[] args ){
        if (args.length == 0){
            System.out.println("Provide a GitHub username");
            return;
        }

        String username = args[0];
        System.out.println("Fetching activity for " + username);
        try {
            System.out.print(httpRequest(username).toString());
        } catch (IOException ioe){
            System.out.println("Connection failed.");
        } catch(InterruptedException ie){
            Thread.currentThread().interrupt();
            System.out.println("Request was interrupted.");
        }

    }

    public static String httpRequest(String username) throws IOException, InterruptedException{
        HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.github.com/users/" + username + "/events"))
                .GET().build();
            HttpResponse<String> response = client.send(
                request, HttpResponse.BodyHandlers.ofString());
        
            System.out.println(response.statusCode());
            

            if (response.statusCode() == 200){
                return(response.body());
            } else if (response.statusCode() == 404){
                return("404 activity not found or user doesn't exist.");
            } else if (response.statusCode() == 403){
                return("403 request was refused or API rate limit exceeded.");
        } else {
            return(response.statusCode() + " error");
        }
    }
}

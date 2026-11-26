package com.example.githubactivity;
import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Main 
{ 
    public static void main(String[] args ){
        if (args.length == 0){
            System.out.println("Provide a GitHub username");
            return;
        }

        String username = args[0];
        System.out.println("Fetching activity for " + username + "\n\n");

        try{
            if (httpRequest(username).equals("No activity.")){
                System.out.println("No activity.");
                return;
            } else {
                System.out.println(formatActivity(jsonParse(httpRequest(username))));
            }
        } catch (JsonProcessingException jpe){
            System.out.println("JSON processing error.");
        }  catch (IOException ioe){
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
            

            if (response.statusCode() == 200){
                if (response.body().trim().equals("[]")){
                    return ("No activity.");
                } else {
                    return(response.body());
                }
            } else if (response.statusCode() == 404){
                return("404 activity not found or user doesn't exist.");
            } else if (response.statusCode() == 403){
                return("403 request was refused or API rate limit exceeded.");
        } else {
            return(response.statusCode() + " error");
        }
    }

    //json processing exception when user has no public history

    public static JsonNode jsonParse(String response) throws JsonProcessingException{
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        return rootNode;
    }

    public static String formatActivity(JsonNode node){

        StringBuilder jsonString = new StringBuilder();

        if (node.isEmpty()){
            return "No activity.";
        }


        for (int i = 0; i < node.size(); i++){
            if (!node.get(i).isEmpty()){
                jsonString.append("Event: " + describeEvent((node.get(i).get("type").asText())));
                jsonString.append("\n");
                jsonString.append("Repo: " + node.get(i).get("repo").get("name").asText());
                jsonString.append("\n\n");
            }
        }

        return jsonString.toString();

    }

    public static String describeEvent(String event){
        switch(event){
            case "PushEvent":
                return "Pushed commits";
            case "WatchEvent":
                return "Starred a repo";
            case "IssuesEvent":
                return "Performed action on an issue";
            case "CreateEvent":
                return "Created a tag, branch, or repo";
            case "PullRequestEvent":
                return "Performed an action on a pull request";
            case "ForkEvent":
                return "Forked a repo";
            case "DeleteEvent":
                return "Deleted a tag or branch";
            default:
                return "Unknown event";
        }
    }
}

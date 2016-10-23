package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.ConnectionRequest;
import models.Profile;
import models.User;
import play.mvc.Controller;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by lubuntu on 10/23/16.
 */
public class HomeController extends Controller {

    @Inject
    ObjectMapper objectMapper;

    public Result getProfile(Long id){
        User user = User.find.byId(id);
        Profile profile= Profile.find.byId(user.profile.id);
        ObjectNode data=objectMapper.createObjectNode();
        data.put("id",user.id);
        data.put("firstName",profile.firstName);
        data.put("lastName",profile.lastName);
        data.put("email",user.email);
        data.put("company",profile.company);
        data.set("connections",objectMapper.valueToTree(user.connections.stream().map(connection->{
            ObjectNode connectionJson=objectMapper.createObjectNode();
            User connectionJsonUser=User.find.byId(connection.id);
            Profile connectionJsonProfile=Profile.find.byId(user.profile.id);
            connectionJson.put("id",connectionJsonUser.id);
            connectionJson.put("firstName",connectionJsonProfile.firstName);
            connectionJson.put("lastName",connectionJsonProfile.lastName);
            connectionJson.put("email",connectionJsonUser.email);
            connectionJson.put("company",connectionJsonProfile.company);
            return connectionJson;
        }).collect(Collectors.toList())));
        data.set("connectionRequests",objectMapper.valueToTree
                (user.connectionRequestsReceived.stream().filter
                        (connectionRequestCheck->connectionRequestCheck.status.equals
                            (ConnectionRequest.Status.WAITING)).map(connectionRequest->{
            ObjectNode connectionRequestJson=objectMapper.createObjectNode();
            User connectionRequestUser=connectionRequest.sender;
            Profile connectionRequestProfile=Profile.find.byId(connectionRequestUser.profile.id);
            connectionRequestJson.put("id",connectionRequest.id);
            connectionRequestJson.put("firstName",connectionRequestProfile.firstName);
            return connectionRequestJson;
        }).collect(Collectors.toList())));
        data.set("suggestions",objectMapper.valueToTree(User.find.all().stream()
                .filter(x->!user.equals(x))
                .filter(x->!user.connections.contains(x))
                .filter(x->!user.connectionRequestsReceived.stream()
                        .map(y->y.sender).collect(Collectors.toList()).contains(x))
                .filter(x->!user.connectionRequestsSent.stream()
                        .map(z->z.receiver).collect(Collectors.toList()).contains(x)).map(suggestion->{
            ObjectNode suggestionJson=objectMapper.createObjectNode();
            Profile suggestionJsonProfile= suggestion.profile;
            suggestionJson.put("firstName",suggestionJsonProfile.firstName);
            suggestionJson.put("email",suggestion.email);
            return suggestionJson;
                }).collect(Collectors.toList())));

        return ok();
    }
}

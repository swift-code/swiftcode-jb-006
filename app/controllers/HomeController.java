package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
            User connectionJsonUser=User.find.byId(id);
            Profile connectionJsonProfile=Profile.find.byId(user.profile.id);
            connectionJson.put("id",connectionJsonUser.id);
            connectionJson.put("firstName",connectionJsonProfile.firstName);
            connectionJson.put("lastName",connectionJsonProfile.lastName);
            connectionJson.put("email",connectionJsonUser.email);
            connectionJson.put("company",connectionJsonProfile.company);
            return connectionJson;
        }).collect(Collectors.toList())));

        return ok();
    }
}

package implementation;

import common.Constants;
import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONArray;

import java.io.IOException;

public class Implementation{

    Command command;

    public Implementation () {
        command = new Command();
    }

    public void Implementaion (Writer filewriter, Database database, JSONArray result) throws IOException {

        for (ActionInputData action : database.actionList) {
            if (action.getActionType().equals("command")) {
                if (action.getType().equals("favorite"))
                    result.add(command.Favorite(filewriter,action, database));
                else if (action.getType().equals("view"))
                    result.add(command.View(filewriter, action, database));
                else
                    result.add(command.Rating(filewriter, action, database));
            }

        }
    }
}

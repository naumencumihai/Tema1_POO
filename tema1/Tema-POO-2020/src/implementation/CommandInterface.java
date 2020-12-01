package implementation;

import fileio.ActionInputData;
import fileio.Writer;
import org.json.simple.JSONObject;

import java.io.IOException;

public interface CommandInterface {
    public JSONObject Favorite (Writer filewriter,
                                ActionInputData action,
                                Database database) throws IOException;

    public JSONObject View (Writer filewriter,
                            ActionInputData action,
                            Database database) throws IOException;

    public JSONObject Rating (Writer filewriter,
                              ActionInputData action,
                              Database database) throws IOException;
}

package DataBases;
import java.sql.SQLException;

import com.google.gson.JsonObject;
public interface DBConnectionInterface {
	public Object excute(JsonObject data) throws SQLException;
}

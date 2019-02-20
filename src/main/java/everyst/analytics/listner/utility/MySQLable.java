package everyst.analytics.listner.utility;

public interface MySQLable {
	
	/**
	 * Returns a MySQL Query to save this object to the database
	 */
	public String getQuery();

}

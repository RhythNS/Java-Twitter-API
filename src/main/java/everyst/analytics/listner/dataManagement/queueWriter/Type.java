package everyst.analytics.listner.dataManagement.queueWriter;

public enum Type {
	NOT_CONNECTED(0, "NotConnected"), ERROR(1, "Error"), EXIT(2, "Exit"), FULL(3, "Full"), DEBUG(4, "Debug"), ALL(5, "All"), READ_ERROR(6, "ReadError");

	int number;
	String path;

	private Type(int number, String path) {
		this.number = number;
		this.path = path;
	}

	public int getNumber() {
		return number;
	}

	public String getPath() {
		return path;
	}
}

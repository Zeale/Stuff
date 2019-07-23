package zeale.apps.stuff.api.chatroom.connections.messages;

public class EndConnectionMessage extends Message {

	/**
	 * SUID
	 */
	private static final long serialVersionUID = 1L;

	private final String reason;

	public EndConnectionMessage(String reason) {
		this.reason = reason;
	}

	public String getReason() {
		return reason;
	}

	public static final EndConnectionMessage STREAM_ERROR_OCCURRED = new EndConnectionMessage("Stream error occurred"),
			UNEXPECTED_DATA_RECEIVED = new EndConnectionMessage("Unexpected data received");

}

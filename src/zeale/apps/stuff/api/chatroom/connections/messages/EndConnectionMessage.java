package zeale.apps.stuff.api.chatroom.connections.messages;

/**
 * This class is used to end a connection between a client and a server. If
 * either a client or a server receives an instance of this class as a
 * standalone piece of data, it should not send a reply.
 * 
 * @author Zeale
 *
 */
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
			UNEXPECTED_DATA_RECEIVED = new EndConnectionMessage("Unexpected data received"),
			INCOMPATIBLE_VERSION = new EndConnectionMessage("Version determined to be incomaptible");

}

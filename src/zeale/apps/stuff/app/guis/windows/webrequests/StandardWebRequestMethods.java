package zeale.apps.stuff.app.guis.windows.webrequests;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

public enum StandardWebRequestMethods implements WebRequestMethod {
	GET {
		@Override
		public String preview(String url, String userAgent, Map<String, String> params, String body) {
			URL url0;
			try {
				url0 = new URL(url);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}

			String result = "GET ";
			String query = url0.getQuery();
			result += url0.getPath() + (query == null ? "" : query) + " HTTP/1.1\n";

			if (params != null)
				for (Entry<String, String> e : params.entrySet())
					result += e.getKey() + ": " + e.getValue() + "\n";

			result += "\n\n" + body;

			return result;
		}

		@Override
		public String send(String url, String userAgent, Map<String, String> params, String body)
				throws WebRequestException {

			URL url0;
			try {
				url0 = new URL(url);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(e);
			}

			String result = "GET ";
			String query = url0.getQuery();
			result += url0.getPath() + (query == null ? "" : query) + " HTTP/1.1\n";
			if (params != null)
				for (Entry<String, String> e : params.entrySet())
					result += e.getKey() + ": " + e.getValue() + "\n";

			result += "\n\n" + body;

			return send(url0.getHost(), 80, result);
		}
	},
	POST, HEAD, DELETE, PUT, CONNECT, OPTIONS, TRACE, PATCH;

	String send(String address, int port, String text) throws WebRequestException {
		try (Socket socket = new Socket(address, port)) {
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			output.println(text);

			InputStream input = socket.getInputStream();

			int preSize = input.available();
			ByteArrayOutputStream out = new ByteArrayOutputStream(preSize);
			byte[] buff = new byte[preSize < 1024 ? 1024 : preSize];

			int len;
			while ((len = input.read(buff)) != -1)
				out.write(buff, 0, len);

			return out.toString(StandardCharsets.UTF_8.name());

		} catch (Exception e) {
			throw new WebRequestException(e);
		}
	}

	@Override
	public String preview(String url, String userAgent, Map<String, String> params, String body) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String send(String url, String userAgent, Map<String, String> params, String body)
			throws WebRequestException {
		// TODO Auto-generated method stub
		return null;
	}

}

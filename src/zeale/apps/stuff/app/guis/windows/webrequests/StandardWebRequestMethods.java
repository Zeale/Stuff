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
	GET, POST {

		@Override
		String preview(URL url, String userAgent, Map<String, String> params, String body) throws WebRequestException {
			String path = url.getPath(),
					result = "POST " + (path.isEmpty() ? "/" : path) + " HTTP/1.1\r\n" + addExtras(userAgent, params);
			result += "\r\n";
			String query = url.getQuery();
			if (query != null)
				result += query;

			return result;

		}

	},
	HEAD, DELETE, PUT {
		@Override
		String preview(URL url, String userAgent, Map<String, String> params, String body) throws WebRequestException {
			String query = url.getQuery(), path = url.getPath(), result = "PUT " + (path.isEmpty() ? "/" : path)
					+ (query == null ? "" : query) + " HTTP/1.1\r\n" + addExtras(userAgent, params);

			if (body != null && !body.isEmpty())
				result += "\r\n" + body;
			return result;
		}
	},
	TRACE {
		@Override
		String preview(URL url, String userAgent, Map<String, String> params, String body) throws WebRequestException {
			String query = url.getQuery(), path = url.getPath(), result = "PUT " + (path.isEmpty() ? "/" : path)
					+ (query == null ? "" : query) + "\r\n" + addExtras(userAgent, params);

			if (body != null && !body.isEmpty())
				result += "\r\n" + body;
			return result;
		}
	},
	PATCH;

	static String send(String address, int port, String text) throws WebRequestException {
		try (Socket socket = new Socket(address, port)) {
			PrintWriter output = new PrintWriter(socket.getOutputStream());
			output.println(text);
			output.flush();

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

	private static String addExtras(String userAgent, Map<String, String> params) throws WebRequestException {
		String result = "";
		if (userAgent != null && !userAgent.isEmpty())
			result += "User-Agent: " + userAgent + "\r\n";
		if (params != null)
			for (Entry<String, String> e : params.entrySet())
				result += e.getKey() + ": " + e.getValue() + "\r\n";
		return result;
	}

	String preview(URL url, String userAgent, Map<String, String> params, String body) throws WebRequestException {
		String query = url.getQuery(), path = url.getPath(), result = name() + " " + (path.isEmpty() ? "/" : path)
				+ (query == null ? "" : query) + " HTTP/1.1\r\n" + addExtras(userAgent, params);

		if (body != null && !body.isEmpty())
			result += "\r\n" + body;

		return result;
	}

	private static URL url(String url) throws WebRequestException {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			throw new WebRequestException(e);
		}
	}

	@Override
	public String preview(String url, String userAgent, Map<String, String> params, String body)
			throws WebRequestException {
		return preview(url(url), userAgent, params, body);
	}

	@Override
	public String send(String url, String userAgent, Map<String, String> params, String body)
			throws WebRequestException {
		URL url0 = url(url);
		return send(url0.getHost(), 80, preview(url0, userAgent, params, body));
	}

}

package zeale.apps.stuff.app.guis.windows.webrequests;

import java.util.Collection;

interface WebRequestMethod {
	String preview(String url, String userAgent, Collection<String> params, String body);

	String send(String url, String userAgent, Collection<String> params, String body);
}

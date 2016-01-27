import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.security.config.rest.AuthenticationRequestWrapper;

@Component
public class XSSFilter implements Filter {

	private static Logger log=LoggerFactory.getLogger(XSSFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		String servletPath=((HttpServletRequest)request).getServletPath();

		if (	!servletPath.equals("/") &&
				servletPath.indexOf("/login")==-1 &&
				servletPath.indexOf("/success")==-1 &&
				servletPath.indexOf("/failure")==-1 &&
				servletPath.indexOf("/index")==-1 &&
				servletPath.indexOf("/favicon.ico")==-1 &&
				servletPath.indexOf("/assets/")==-1 &&
				servletPath.indexOf("/app/")==-1 &&
				servletPath.indexOf("/register")==-1 &&
				servletPath.indexOf("/public/")==-1 &&
				request.getContentType() != null && request.getContentType().toLowerCase().indexOf("multipart/form-data") == -1 
				){
			AuthenticationRequestWrapper req = new AuthenticationRequestWrapper((HttpServletRequest) request);
			log.info("In XSS filter: "+servletPath);
			chain.doFilter(req, response);
		}
		else{
			chain.doFilter(request, response);
		}
	}

}

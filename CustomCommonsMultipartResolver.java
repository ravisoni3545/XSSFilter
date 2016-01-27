import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class CustomCommonsMultipartResolver extends CommonsMultipartResolver {


	public CustomCommonsMultipartResolver() {
		super();
	}

	/**
	 * Constructor for standalone usage. Determines the servlet container's
	 * temporary directory via the given ServletContext.
	 * @param servletContext the ServletContext to use
	 */
	public CustomCommonsMultipartResolver(ServletContext servletContext) {
		this();
		setServletContext(servletContext);
	}

	@Override
	protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
			MultipartParsingResult res=parseFileItems(fileItems, encoding);

			Map<String, String[]> params=res.getMultipartParameters();

			Map<String, String[]> modifiedRes= new HashMap<String, String[]>();

			for(Entry<String, String[]> entry:params.entrySet()){

				if(entry.getValue().length>0){
					String val=Arrays.toString(entry.getValue());

					if(!NullCheckUtil.isEmpty(val)){
						String cleanedString=XSSUtil.stripXSS(val);
						String[] valModified=new String[]{cleanedString.substring(1, cleanedString.length()-1)};
						modifiedRes.put(entry.getKey(), valModified);
					}
					else{
						modifiedRes.put(entry.getKey(), entry.getValue());
					}
				}
				else{
					modifiedRes.put(entry.getKey(), entry.getValue());
				}
			}

			MultipartParsingResult modifiedResp=new MultipartParsingResult(res.getMultipartFiles(), modifiedRes, res.getMultipartParameterContentTypes());
			return modifiedResp;
		}
		catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		}
		catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart servlet request", ex);
		}
	}
}

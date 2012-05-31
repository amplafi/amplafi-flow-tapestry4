/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 * the specific language governing permissions and limitations under the
 * License.
 */

package org.amplafi.flow.web.services;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.amplafi.flow.BaseFlowResponse;
import org.amplafi.flow.web.BaseFlowService;
import org.amplafi.flow.web.FlowRequest;
import org.amplafi.flow.web.FlowResponse;
import org.amplafi.flow.web.FlowService;
import org.apache.commons.lang.StringUtils;
import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.http.HttpStatus;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.PageNotFoundException;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.RedirectException;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.util.ContentType;
import org.apache.tapestry.web.WebRequest;
import org.apache.tapestry.web.WebResponse;



/**
 * Base implementation for Tapestry services that start / continue flows.
 *
 */
@Deprecated
public abstract class EngineFlowService extends BaseFlowService implements FlowService, IEngineService {

	private WebRequest request;
    private WebResponse response;
	private String name;
	private HttpServletRequest httpServletRequest;
	private HttpServletResponse httpServletResponse;

    @Override
    public void service(final IRequestCycle cycle) throws IOException {
		try {
			FlowRequest flowRequest = new EngineFlowRequest(this.getRenderResultDefault(), cycle, request, httpServletRequest, getReferingUri(httpServletRequest));
			FlowResponse flowResponse = new BaseFlowResponse(getWriter());
			service(flowRequest, flowResponse);
		} catch (PageRedirectException e) {
			throw e;
       } catch (PageNotFoundException e) {
            throw e;
		} catch (RedirectException e) {
			throw e;
		} catch(ApplicationRuntimeException e){
	       Throwable rootCause = e.getRootCause();
            if (rootCause instanceof PageRedirectException) {
                throw (PageRedirectException) rootCause;
            } else if (rootCause instanceof PageNotFoundException) {
                throw (PageNotFoundException) rootCause;
            } else if (rootCause instanceof RedirectException) {
                throw (RedirectException) rootCause;
            } else {
                getLog().info(getReferingUri(httpServletRequest), e);
            }
		}catch (Exception e) {
			getLog().info(getReferingUri(httpServletRequest), e);
		}
	}

    @Override
	public void service(FlowRequest flowRequest, FlowResponse flowResponse) {
		super.service(flowRequest, flowResponse);
		httpServletResponse.setStatus(flowResponse.hasErrors() ? HttpStatus.SC_BAD_REQUEST : HttpStatus.SC_OK);
		if (flowResponse.isRedirectSet()) {
			try {
				httpServletResponse.sendRedirect(flowResponse.getRedirect().toASCIIString());
			} catch (IOException e) {
				throw new IllegalStateException("Failed to send flow redirect to: " + flowResponse.getRedirect());
			}
		}
	}

	public static String getReferingUri(HttpServletRequest httpServletRequest) {
       String referingUriStr = httpServletRequest.getHeader("Referer");
       if(StringUtils.isNotBlank(referingUriStr)){
           URI referingUri;
           try {
               referingUri = new URI(referingUriStr);
               return referingUri.toString();
           } catch (URISyntaxException e) {
               // ignore bad uri
           }
       }
       return null;
	}

	public PrintWriter getWriter() {
       ContentType contentType = new ContentType(SCRIPT_CONTENT_TYPE);
       String encoding = contentType.getParameter("charset");
       if (encoding == null) {
    	   contentType.setParameter("charset", "utf-8");
       }
       try {
		return response.getPrintWriter(contentType);
       } catch (IOException e) {
       	throw new IllegalStateException(e);
       }
    }

	@Override
	public String getName() {
       return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setResponse(WebResponse response) {
       this.response = response;
	}

	public WebResponse getResponse() {
       return response;
	}

	public WebRequest getRequest() {
       return request;
	}

	public void setRequest(WebRequest request) {
       this.request = request;
	}

	public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
       this.httpServletRequest = httpServletRequest;
	}

	public HttpServletRequest getHttpServletRequest() {
       return httpServletRequest;
	}

	public HttpServletResponse getHttpServletResponse() {
		return httpServletResponse;
	}

	public void setHttpServletResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}
}

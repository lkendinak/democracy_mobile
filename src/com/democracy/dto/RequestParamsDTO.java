package com.democracy.dto;

import br.com.evcash.dto.request.BaseParamsDTO;

public class RequestParamsDTO {

	private String url;
	private String method;
	private String filePath;
	private BaseParamsDTO params;

	public RequestParamsDTO(String url, String method, BaseParamsDTO params) {
		this.url = url;
		this.method = method;
		this.params = params;
	}

	public RequestParamsDTO(String url, String method, BaseParamsDTO params,
			String filePath) {
		super();
		this.url = url;
		this.method = method;
		this.params = params;
		this.filePath = filePath;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public BaseParamsDTO getParams() {
		return params;
	}

	public void setParams(BaseParamsDTO params) {
		this.params = params;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}

package com.adalytics.adalytics_backend.external;

import com.adalytics.adalytics_backend.enums.ErrorCodes;
import com.adalytics.adalytics_backend.exceptions.BadGatewayException;
import com.adalytics.adalytics_backend.exceptions.MethodNotAllowedException;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApiService {
    public String callExternalApi(String url, String method, String requestBody, Map<String, String> headers) throws Exception {
        String response = null;

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            HttpUriRequestBase httpRequest;
            switch (method.toUpperCase()) {
                case "GET":
                    httpRequest = new HttpGet(url);
                    break;
                case "POST":
                    httpRequest = new HttpPost(url);
                    if (requestBody != null && !requestBody.isEmpty()) {
                        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                        httpRequest.setEntity(entity);
                    }
                    break;
                case "PUT":
                    httpRequest = new HttpPut(url);
                    if (requestBody != null && !requestBody.isEmpty()) {
                        StringEntity entity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                        httpRequest.setEntity(entity);
                    }
                    break;
                case "DELETE":
                    httpRequest = new HttpDelete(url);
                    break;
                default:
                    throw new MethodNotAllowedException("Unsupported HTTP method: " + method, ErrorCodes.Method_Not_Allowed.getErrorCode());
            }

            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    httpRequest.addHeader(entry.getKey(), entry.getValue());
                }
            }

            response = httpClient.execute(httpRequest, clientResponse -> {
                int status = clientResponse.getCode();
                if (status >= 200 && status < 300) {
                    return clientResponse.getEntity().toString();
                } else {
                    throw new BadGatewayException("External API returned status code: " + status, ErrorCodes.Client_Not_Responding.getErrorCode());
                }
            });
        } catch (Exception e) {
            throw new BadGatewayException("Error while calling external API: " + e.getMessage(), ErrorCodes.Client_Not_Responding.getErrorCode());
        }

        return response;
    }
}

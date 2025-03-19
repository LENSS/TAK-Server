package org.ism.api;

import org.ism.invoker.ApiClient;

import org.ism.model.CanAccessDto;
import org.ism.model.CanAccessResponse;
import org.ism.model.MarkingResponse;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2025-03-18T21:49:37.751722524-05:00[America/Chicago]")
public class DefaultApi {
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * 
     * 
     * <p><b>200</b> - 
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public void appControllerGetSuccess() throws RestClientException {
        appControllerGetSuccessWithHttpInfo();
    }

    /**
     * 
     * 
     * <p><b>200</b> - 
     * @return ResponseEntity&lt;Void&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<Void> appControllerGetSuccessWithHttpInfo() throws RestClientException {
        Object localVarPostBody = null;
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = {  };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<Void> localReturnType = new ParameterizedTypeReference<Void>() {};
        return apiClient.invokeAPI("/", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Verify a user can access all portion marks provided
     * 
     * <p><b>200</b> - A boolean value for each marking stating whether or not the user has access
     * @param canAccessDto  (required)
     * @return CanAccessResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public CanAccessResponse ismControllerCanAccess(CanAccessDto canAccessDto) throws RestClientException {
        return ismControllerCanAccessWithHttpInfo(canAccessDto).getBody();
    }

    /**
     * Verify a user can access all portion marks provided
     * 
     * <p><b>200</b> - A boolean value for each marking stating whether or not the user has access
     * @param canAccessDto  (required)
     * @return ResponseEntity&lt;CanAccessResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<CanAccessResponse> ismControllerCanAccessWithHttpInfo(CanAccessDto canAccessDto) throws RestClientException {
        Object localVarPostBody = canAccessDto;
        
        // verify the required parameter 'canAccessDto' is set
        if (canAccessDto == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'canAccessDto' when calling ismControllerCanAccess");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<CanAccessResponse> localReturnType = new ParameterizedTypeReference<CanAccessResponse>() {};
        return apiClient.invokeAPI("/can-access", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Parse a classification string
     * 
     * <p><b>200</b> - The classification serialized, deserialized, and validated
     * @param format  (required)
     * @param classification  (required)
     * @param fgiExpand  (optional)
     * @param fgiCombine  (optional)
     * @param enforceProgramNicknames  (optional)
     * @param camelCaseKeys  (optional)
     * @return MarkingResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public MarkingResponse ismControllerParseMarking(String format, String classification, Boolean fgiExpand, Boolean fgiCombine, Boolean enforceProgramNicknames, Boolean camelCaseKeys) throws RestClientException {
        return ismControllerParseMarkingWithHttpInfo(format, classification, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys).getBody();
    }

    /**
     * Parse a classification string
     * 
     * <p><b>200</b> - The classification serialized, deserialized, and validated
     * @param format  (required)
     * @param classification  (required)
     * @param fgiExpand  (optional)
     * @param fgiCombine  (optional)
     * @param enforceProgramNicknames  (optional)
     * @param camelCaseKeys  (optional)
     * @return ResponseEntity&lt;MarkingResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MarkingResponse> ismControllerParseMarkingWithHttpInfo(String format, String classification, Boolean fgiExpand, Boolean fgiCombine, Boolean enforceProgramNicknames, Boolean camelCaseKeys) throws RestClientException {
        Object localVarPostBody = null;
        
        // verify the required parameter 'format' is set
        if (format == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'format' when calling ismControllerParseMarking");
        }
        
        // verify the required parameter 'classification' is set
        if (classification == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'classification' when calling ismControllerParseMarking");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "fgiExpand", fgiExpand));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "fgiCombine", fgiCombine));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "format", format));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "enforceProgramNicknames", enforceProgramNicknames));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "classification", classification));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "camelCaseKeys", camelCaseKeys));

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = {  };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<MarkingResponse> localReturnType = new ParameterizedTypeReference<MarkingResponse>() {};
        return apiClient.invokeAPI("/parse", HttpMethod.GET, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
    /**
     * Rollup an array of classification strings to a banner line or portion mark
     * 
     * <p><b>200</b> - The rolled up banner marking serialized, deserialized, and validated
     * @param format  (required)
     * @param body  (required)
     * @param expandable  (optional)
     * @param fgiExpand  (optional)
     * @param fgiCombine  (optional)
     * @param enforceProgramNicknames  (optional)
     * @param camelCaseKeys  (optional)
     * @return MarkingResponse
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public MarkingResponse ismControllerRollupMarkings(String format, String body, List<String> expandable, Boolean fgiExpand, Boolean fgiCombine, Boolean enforceProgramNicknames, Boolean camelCaseKeys) throws RestClientException {
        return ismControllerRollupMarkingsWithHttpInfo(format, body, expandable, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys).getBody();
    }

    /**
     * Rollup an array of classification strings to a banner line or portion mark
     * 
     * <p><b>200</b> - The rolled up banner marking serialized, deserialized, and validated
     * @param format  (required)
     * @param body  (required)
     * @param expandable  (optional)
     * @param fgiExpand  (optional)
     * @param fgiCombine  (optional)
     * @param enforceProgramNicknames  (optional)
     * @param camelCaseKeys  (optional)
     * @return ResponseEntity&lt;MarkingResponse&gt;
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public ResponseEntity<MarkingResponse> ismControllerRollupMarkingsWithHttpInfo(String format, String body, List<String> expandable, Boolean fgiExpand, Boolean fgiCombine, Boolean enforceProgramNicknames, Boolean camelCaseKeys) throws RestClientException {
        Object localVarPostBody = body;
        
        // verify the required parameter 'format' is set
        if (format == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'format' when calling ismControllerRollupMarkings");
        }
        
        // verify the required parameter 'body' is set
        if (body == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'body' when calling ismControllerRollupMarkings");
        }
        

        final MultiValueMap<String, String> localVarQueryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders localVarHeaderParams = new HttpHeaders();
        final MultiValueMap<String, String> localVarCookieParams = new LinkedMultiValueMap<String, String>();
        final MultiValueMap<String, Object> localVarFormParams = new LinkedMultiValueMap<String, Object>();

        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(ApiClient.CollectionFormat.valueOf("multi".toUpperCase(Locale.ROOT)), "expandable", expandable));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "fgiExpand", fgiExpand));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "fgiCombine", fgiCombine));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "format", format));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "enforceProgramNicknames", enforceProgramNicknames));
        localVarQueryParams.putAll(apiClient.parameterToMultiValueMap(null, "camelCaseKeys", camelCaseKeys));

        final String[] localVarAccepts = { 
            "application/json"
         };
        final List<MediaType> localVarAccept = apiClient.selectHeaderAccept(localVarAccepts);
        final String[] localVarContentTypes = { 
            "application/json"
         };
        final MediaType localVarContentType = apiClient.selectHeaderContentType(localVarContentTypes);

        String[] localVarAuthNames = new String[] {  };

        ParameterizedTypeReference<MarkingResponse> localReturnType = new ParameterizedTypeReference<MarkingResponse>() {};
        return apiClient.invokeAPI("/rollup", HttpMethod.POST, Collections.<String, Object>emptyMap(), localVarQueryParams, localVarPostBody, localVarHeaderParams, localVarCookieParams, localVarFormParams, localVarAccept, localVarContentType, localVarAuthNames, localReturnType);
    }
}

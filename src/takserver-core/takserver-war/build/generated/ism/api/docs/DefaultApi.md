# DefaultApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**appControllerGetSuccess**](DefaultApi.md#appControllerGetSuccess) | **GET** / |  |
| [**ismControllerCanAccess**](DefaultApi.md#ismControllerCanAccess) | **POST** /can-access | Verify a user can access all portion marks provided |
| [**ismControllerParseMarking**](DefaultApi.md#ismControllerParseMarking) | **GET** /parse | Parse a classification string |
| [**ismControllerRollupMarkings**](DefaultApi.md#ismControllerRollupMarkings) | **POST** /rollup | Rollup an array of classification strings to a banner line or portion mark |



## appControllerGetSuccess

> appControllerGetSuccess()



### Example

```java
// Import classes:
import org.ism.invoker.ApiClient;
import org.ism.invoker.ApiException;
import org.ism.invoker.Configuration;
import org.ism.invoker.models.*;
import org.ism.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        try {
            apiInstance.appControllerGetSuccess();
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#appControllerGetSuccess");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters

This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: Not defined


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** |  |  -  |


## ismControllerCanAccess

> CanAccessResponse ismControllerCanAccess(canAccessDto)

Verify a user can access all portion marks provided

### Example

```java
// Import classes:
import org.ism.invoker.ApiClient;
import org.ism.invoker.ApiException;
import org.ism.invoker.Configuration;
import org.ism.invoker.models.*;
import org.ism.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        CanAccessDto canAccessDto = new CanAccessDto(); // CanAccessDto | 
        try {
            CanAccessResponse result = apiInstance.ismControllerCanAccess(canAccessDto);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#ismControllerCanAccess");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **canAccessDto** | [**CanAccessDto**](CanAccessDto.md)|  | |

### Return type

[**CanAccessResponse**](CanAccessResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | A boolean value for each marking stating whether or not the user has access |  -  |


## ismControllerParseMarking

> MarkingResponse ismControllerParseMarking(format, classification, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys)

Parse a classification string

### Example

```java
// Import classes:
import org.ism.invoker.ApiClient;
import org.ism.invoker.ApiException;
import org.ism.invoker.Configuration;
import org.ism.invoker.models.*;
import org.ism.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        String format = "bl"; // String | 
        String classification = "S//NF"; // String | 
        Boolean fgiExpand = false; // Boolean | 
        Boolean fgiCombine = false; // Boolean | 
        Boolean enforceProgramNicknames = false; // Boolean | 
        Boolean camelCaseKeys = false; // Boolean | 
        try {
            MarkingResponse result = apiInstance.ismControllerParseMarking(format, classification, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#ismControllerParseMarking");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **format** | **String**|  | [enum: bl, pm] |
| **classification** | **String**|  | |
| **fgiExpand** | **Boolean**|  | [optional] |
| **fgiCombine** | **Boolean**|  | [optional] |
| **enforceProgramNicknames** | **Boolean**|  | [optional] |
| **camelCaseKeys** | **Boolean**|  | [optional] |

### Return type

[**MarkingResponse**](MarkingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The classification serialized, deserialized, and validated |  -  |


## ismControllerRollupMarkings

> MarkingResponse ismControllerRollupMarkings(format, body, expandable, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys)

Rollup an array of classification strings to a banner line or portion mark

### Example

```java
// Import classes:
import org.ism.invoker.ApiClient;
import org.ism.invoker.ApiException;
import org.ism.invoker.Configuration;
import org.ism.invoker.models.*;
import org.ism.api.DefaultApi;

public class Example {
    public static void main(String[] args) {
        ApiClient defaultClient = Configuration.getDefaultApiClient();
        defaultClient.setBasePath("http://localhost");

        DefaultApi apiInstance = new DefaultApi(defaultClient);
        String format = "bl"; // String | 
        String body = ["S//ACCM-HAPPY FROG","C//ACCM","C//ACCM-SAD-TOAD"]; // String | 
        List<String> expandable = Arrays.asList(); // List<String> | 
        Boolean fgiExpand = false; // Boolean | 
        Boolean fgiCombine = false; // Boolean | 
        Boolean enforceProgramNicknames = false; // Boolean | 
        Boolean camelCaseKeys = false; // Boolean | 
        try {
            MarkingResponse result = apiInstance.ismControllerRollupMarkings(format, body, expandable, fgiExpand, fgiCombine, enforceProgramNicknames, camelCaseKeys);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling DefaultApi#ismControllerRollupMarkings");
            System.err.println("Status code: " + e.getCode());
            System.err.println("Reason: " + e.getResponseBody());
            System.err.println("Response headers: " + e.getResponseHeaders());
            e.printStackTrace();
        }
    }
}
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **format** | **String**|  | [enum: bl, pm] |
| **body** | **String**|  | |
| **expandable** | [**List&lt;String&gt;**](String.md)|  | [optional] |
| **fgiExpand** | **Boolean**|  | [optional] |
| **fgiCombine** | **Boolean**|  | [optional] |
| **enforceProgramNicknames** | **Boolean**|  | [optional] |
| **camelCaseKeys** | **Boolean**|  | [optional] |

### Return type

[**MarkingResponse**](MarkingResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: application/json
- **Accept**: application/json


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | The rolled up banner marking serialized, deserialized, and validated |  -  |


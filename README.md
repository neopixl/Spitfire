[![Twitter](https://img.shields.io/badge/Twitter-@Neopixl-blue.svg?style=flat)](http://twitter.com/neopixl)
[![Site](https://img.shields.io/badge/Site-neopixl.com-orange.svg?style=flat)](https://neopixl.com)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Spitfire-orange.svg?style=flat)](https://android-arsenal.com/details/1/6290)


[![Travis](https://api.travis-ci.org/neopixl/Spitfire.svg?branch=master)](https://travis-ci.org/neopixl/Spitfire)
[![Coverage Status](https://coveralls.io/repos/github/neopixl/Spitfire/badge.svg?branch=master)](https://coveralls.io/github/neopixl/Spitfire?branch=master)
[![Bintray](https://img.shields.io/bintray/v/fdewasmes/Spitfire/Spitfire.svg)]()
[![API](https://img.shields.io/badge/API-16%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=8)

# Spitfire by @Neopixl


![Logo](logo.png)

A simple Android Network library. This library can be used as a wrapper for Google Volley, FasterXML Jackson serializer.
All the Volley and Jackson method are fully operational.


All the documentations are available here :

 - Volley : [https://developer.android.com/training/volley/index.html](https://developer.android.com/training/volley/index.html)
 - Jackson : [https://github.com/FasterXML/jackson](https://github.com/FasterXML/jackson)
 
Contribution : [CONTRIBUTING.md](CONTRIBUTING.md)

## Usage
This library was designed to be as simple to use as possible.  Here are the steps you'll need to follow:

* Include the maven dependencies for this library as well as it's dependencies build.gradle file.  (Note: Event bus is required only if you use it.)


		dependencies {
			implementation 'com.neopixl:spitfire:1.0.0'
			implementation 'com.android.volley:volley:1.0.0'
		}

* Create the DTO that you would like your request to be parsed as.

		public class DummyResponse {
			private String message;
			private int id;
			private List<DummyResponse> childrens;
			
			// TODO : Added getters and setters
		}

* Create a new Volley RequestQueue, where all requests will be processed.

        RequestQueue requestQueue = Volley.newRequestQueue(this);
		
* Add a new BaseRequest to your RequestQueue.  This will kick off the process of accessing the network resource and parsing the response into your DTO. 

		
		BaseRequest<DummyResponse> request = new BaseRequest.Builder<DummyResponse>(Request.Method.GET,
                "YOUR URL", DummyResponse.class).listener(new RequestListener<DummyResponse>() {
            @Override
            public void onSuccess(Request request, NetworkResponse response, DummyResponse result) {
                Log.d("YOUR APP", "Dummy success");
            }

            @Override
            public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                Log.d("YOUR APP", "Dummy error");
            }
        }).build();
        requestQueue.add(request);
        
* That's it! 


## More Complex Usage

### Get / Post / Put
To launch a Get, Post, Put request simply use the BaseRequest as before.
All the parameters will be added to the URL for the GET requests and to the body for the others

### Json Body

If you want to send a full JSON in the body of your request, simply create a new DTO, for example our "DummyResponse"
	
		DummyResponse objectToSerializeAndSend = new DummyResponse();
        objectToSerializeAndSend.setMessage("My message");

        BaseRequest<DummyResponse> request = new BaseRequest.Builder<DummyResponse>(Request.Method.GET, "YOUR URL", DummyResponse.class)
                .object(objectToSerializeAndSend)
                .listener(new RequestListener<DummyResponse>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, DummyResponse dummyResponse) {
                        Log.d("YOUR APP", "Dummy success");
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        Log.d("YOUR APP", "Dummy error");
                    }
                }).build();
        requestQueue.add(request);

### MultipartData

	    
		 HashMap<String, RequestData> multipardData = new HashMap<>();
        RequestData data = new RequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multipardData.put("image1", data);
        data = new RequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
        multipardData.put("image1", data);

        MultipartRequest<Void> multipartRequest = new MultipartRequest.Builder<Void>(Request.Method.PUT, "YOUR URL", Void.class)
                .listener(new RequestListener<Void>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, Void v) {
                        Log.d("YOUR APP", "Dummy success");
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        Log.d("YOUR APP", "Dummy error");
                    }
                })
                .multiPartData(multipardData)
                .build();
        requestQueue.add(multipartRequest);
        
### Unique File Upload

	UploadFileRequest<String[]> uploadFileRequest = new UploadFileRequest.Builder<String[]>(Request.Method.PUT, "YOUR URL", String[].class)
                .listener(new RequestListener<String[]>() {
                    @Override
                    public void onSuccess(Request request, NetworkResponse response, String[] v) {
                        Log.d("YOUR APP", "Dummy sucess");
                    }

                    @Override
                    public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                        Log.d("YOUR APP", "Dummy error");
                    }
                })
                .partData(data)
                .build();
	requestQueue.add(uploadFileRequest);

### SpitfireManager

SpitfireManager is a global class used to set settings for all the requests.

You can change :

* default request timeout
                
		//Change the default timeout for the default retry policy used for all requests.
   		SpitfireManager.setRequestTimeout(10000);


* default retry policy

		DefaultRetryPolicy retryPolicy = new DefaultRetryPolicy(
                10000,      // 10 seconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

		//Change the default retry policy used for all requests. 
		SpitfireManager.setDefaultRetryPolicy(retryPolicy);

# Proguard

	# Jackson 2.x
	-keep class com.fasterxml.jackson.databind.ObjectMapper {
	    public <methods>;
	    protected <methods>;
	}
	-keep class com.fasterxml.jackson.databind.ObjectWriter {
	    public ** writeValueAsString(**);
	}
	-keep @com.fasterxml.jackson.annotation.JsonIgnoreProperties class * { *; }
	-keep class com.fasterxml.** { *; }
	-keep class org.codehaus.** { *; }
	-keepnames class com.fasterxml.jackson.** { *; }
	-keepclassmembers public final enum com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility {
	    public static final com.fasterxml.jackson.annotation.JsonAutoDetect$Visibility *;
	}
	-keep class com.fasterxml.jackson.databind.ObjectMapper {
	    public <methods>;
	    protected <methods>;
	}
	-keep class com.fasterxml.jackson.databind.ObjectWriter {
	    public ** writeValueAsString(**);
	}
	-keepnames class com.fasterxml.jackson.** { *; }
	-dontwarn com.fasterxml.jackson.databind.**



# They use it

 - Cerbaliance

# License
Spitfire is released under the Apache 2.0 licence. See LICENSE for details.

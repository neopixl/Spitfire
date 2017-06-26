# Spitfire by @Neopixl

A simple Android Network library. This library can be used as a wrapper for Google Volley, FasterXML Jackson serializer.
All the Volley and Jackson method are fully operational.

All the documentations are available here :

 - Volley : [https://developer.android.com/training/volley/index.html](https://developer.android.com/training/volley/index.html)
 - Jackson : [https://github.com/FasterXML/jackson](https://github.com/FasterXML/jackson)
 - EventBus : [https://github.com/greenrobot/EventBus](https://github.com/greenrobot/EventBus)

## Usage
This library was designed to be as simple to use as possible.  Here are the steps you'll need to follow:

* Include the maven dependencies for this library as well as it's dependencies build.gradle file.  (Note: Event bus is required only if you use it.)


		repositories {	
		    maven {
		        url "http://nexus.neopixl.com/nexus/content/repositories/releases/"
		    }
	
		}

		dependencies {
			compile 'com.neopixl.library:spitfire:0.3.5'
			compile 'com.android.volley:volley:1.0.0'
			compile 'com.fasterxml.jackson.core:jackson-core:2.8.5'
			compile 'com.fasterxml.jackson.core:jackson-databind:2.8.5'
			compile 'com.fasterxml.jackson.core:jackson-annotations:2.8.5'
			compile 'org.greenrobot:eventbus:3.0.0'
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
            public void onSuccess(Request request, NetworkResponse response, DummyResponse response) {
                Log.d("YOUR APP", "Dummy success");
            }

            @Override
            public void onFailure(Request request, NetworkResponse response, VolleyError volleyError) {
                Log.d("YOUR APP", "Dummy error");
            }
        }).build();
        requestQueue.add(request);
        
* That's it! 

### Event Bus
If you do not specify a listener the library automaticaly fallback to a EventBus event.

	@Override
	protected void onStart() {
		super.onStart();
		BaseRequest<DummyResponse> request = new BaseRequest.Builder<DummyResponse>(Request.Method.GET, "YOUR URL", DummyResponse.class).build();
       requestQueue.add(request);
	}
	
	@Subscribe
	public void requestResponse(ResponseEvent<DummyResponse> event) {
		Log.d("YOUR APP", "Dummy success ?: "+ event.isSuccess());
	}

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


# In progress

 - Enable deploy from gradle

# They use it

 - App 1

# License
Spitfire is released under the Apache 2.0 licence. See LICENSE for details.

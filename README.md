# NeoRequest by @Neopixl

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
			compile 'com.neopixl.library:neorequest:0.1'
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

		RequestQueue mRequestQueue = Volley.newRequestQueue(this);
		
* Add a new NeoRequest to your RequestQueue.  This will kick off the process of accessing the network resource and parsing the response into your DTO. 

		Map<String, String> emtpyHeaders = new HashMap<>();
		Map<String, String> emtpyParameters = new HashMap<>();
		
		NeoRequest<DummyResponse> neoRequest = new NeoRequest<>(Request.Method.GET,
		"YOUR URL", emtpyHeaders, emtpyParameters, new NeoRequestListener<DummyResponse>() {
			@Override
			public void onSuccess(DummyResponse dummyResponse) {
				Log.d("YOUR APP", "Dummy success");
			}
		
			@Override
			public void onFailure(VolleyError volleyError, int statusCode) {
				Log.d("YOUR APP", "Dummy error");
			}
		}, DummyResponse.class);
		requestQueue.add(neoRequest);

* That's it! 

### Listener
If you do not specify a listener the library automaticaly fallback to a EventBus event.

	@Override
	protected void onStart() {
		super.onStart();
		NeoRequest<DummyResponse> neoRequest = new NeoRequest<>(Request.Method.GET, "YOUR URL", emtpyHeaders, DummyResponse.class);
		requestQueue.add(neoRequest);
	}
	
	@Subscribe
	public void requestResponse(NeoResponseEvent<DummyResponse> event) {
		Log.d("YOUR APP", "Dummy success ?: "+ event.isSuccess());
	}

## More Complex Usage

### Get / Post / Put
To launch a Get, Post, Put request simply use the NeoRequest as before.
All the parameters will be added to the URL for the GET requests and to the body for the others

### Json Body

If you want to send a full JSON in the body of your request, simply create a new DTO, for example our "DummyResponse"

	Map<String, String> emtpyHeaders = new HashMap<>();
	
	DummyResponse objectToSerializeAndSend = new DummyResponse();
	objectToSerializeAndSend.setMessage("My message");
	
	NeoRequest<DummyResponse> neoRequest = new NeoRequest<>(Request.Method.GET,
	"YOUR URL", emtpyHeaders, objectToSerializeAndSend, new NeoRequestListener<DummyResponse>() {
		@Override
		public void onSuccess(DummyResponse dummyResponse) {
			Log.d("YOUR APP", "Dummy success");
		}
	
		@Override
		public void onFailure(VolleyError volleyError, int statusCode) {
			Log.d("YOUR APP", "Dummy error");
		}
	}, DummyResponse.class);
	requestQueue.add(neoRequest);

### MultipartData

	Map<String, String> emtpyHeaders = new HashMap<>();
	    
	HashMap<String, NeoRequestData> multipardData = new HashMap<>();
	NeoRequestData data = new NeoRequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
	multipardData.put("image1", data);
	data = new NeoRequestData("image1", new byte[] {1,1,1,1,0,0,1}, "image/jpeg");
	multipardData.put("image1", data);
	    
	Map<String, String> emtpyParametersOrJsonObject = new HashMap<>();
	    
	MultipartNeoRequest<Void> multipartNeoRequest = new MultipartNeoRequest<>(Request.Method.PUT,
	"YOUR URL", emtpyHeaders, emtpyParametersOrJsonObject, multipardData, new NeoRequestListener<Void>() {
		@Override
		public void onSuccess(Void v) {
			Log.d("YOUR APP", "Dummy success");
		}
	    
		@Override
		public void onFailure(VolleyError volleyError, int i) {
			Log.d("YOUR APP", "Dummy error"); 
		}
	}, Void.class);
	requestQueue.add(multipartNeoRequest);

### FileStream

	FileStreamNeoRequest<String[]> fileStreamNeoRequest = new FileStreamNeoRequest<>(Request.Method.PUT,
	"YOUR URL", emtpyHeaders, data, new NeoRequestListener<String[]>() {
		@Override
		public void onSuccess(String[] v) {
			Log.d("YOUR APP", "Dummy sucess");
		}
	    
		@Override
		public void onFailure(VolleyError volleyError, int i) {
			Log.d("YOUR APP", "Dummy error");   
		}
	}, String[].class);
	requestQueue.add(fileStreamNeoRequest);

# They use it

 - App 1

# License
NeoRequest is released under the XXXXXXXX license.
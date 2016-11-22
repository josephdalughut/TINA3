package ng.edu.aun.tina3.util;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.litigy.lib.java.error.LitigyException;
import com.litigy.lib.java.generic.DoubleReceiver;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.util.CharsetUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Content {

    public static class MultipartRequest extends Request<String> {

        MultipartEntityBuilder entity = MultipartEntityBuilder.create();
        HttpEntity httpentity;

        private final Map<String, File> files;
        private final Map<String, String> params;
        private final DoubleReceiver<String, LitigyException> progressConsumer;
        String token;
        public MultipartRequest(Map<String, File> files, Map<String, String> params,
                                String url, String token, final DoubleReceiver<String, LitigyException> progressConsumer) throws IOException {
            super(Method.POST, url, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error in response, error is "+error.toString());
                    if(!Value.IS.nullValue(progressConsumer)) {
                        Log.d("Feeding error to progress consumer");
                        progressConsumer.onReceive2(LitigyException.consumeIOException(new IOException(error.getMessage())));
                    }
                    }
            });
            this.files = files;
            this.params = params;
            this.token = token;
            this.progressConsumer = progressConsumer;
            entity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            try {
                entity.setCharset(CharsetUtils.get("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            buildMultipartEntity();
            httpentity = entity.build();
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map<String, String> headerParams = new HashMap<>();
            headerParams.put("Authorization", "Bearer "+ token);
            return headerParams;
        }

        private void buildMultipartEntity() {
            setRetryPolicy(new DefaultRetryPolicy(300000, 2, 2));
            for(Map.Entry<String, File> entry : files.entrySet()) {
                ContentType contentType = ContentType.create("image/jpeg");
                entity.addPart(entry.getKey(), new FileBody(entry.getValue(), contentType, entry.getValue().getName()));
            }
            if (!Value.IS.nullValue(params)) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    entity.addTextBody(entry.getKey(), entry.getValue());
                }

            }
        }


        @Override
        public String getBodyContentType() {
            return httpentity.getContentType().getValue();
        }

        @Override
        public byte[] getBody() throws AuthFailureError {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            try {
                httpentity.writeTo(new FilterOutputStream(bos));
            } catch (IOException e) {
                Log.d("IOException writing to ByteArrayOutputStream");
            }
            return bos.toByteArray();
        }

        @Override
        protected Response<String> parseNetworkResponse(NetworkResponse response) {

            try {
//          System.out.println("Network Response "+ new String(response.data, "UTF-8"));
                return Response.success(new String(response.data, "UTF-8"),
                        getCacheEntry());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                // fuck it, it should never happen though
                return Response.success(new String(response.data), getCacheEntry());
            }
        }

        @Override
        protected void deliverResponse(String response) {
            if(!Value.IS.nullValue(progressConsumer))
                progressConsumer.onReceive1(response);
        }

        @Override
        public void deliverError(VolleyError error) {
            Log.d("Volley error: "+error.getMessage());
            try {
                progressConsumer.onReceive2(LitigyException.consumeIOException(new IOException(Value.IS.ANY.nullValue(error) ? " " : error.getMessage())));
            }catch (Exception e){
                Log.d("Error feeding callback to progressConsumer, error is "+e.getMessage());
            }
        }

        /*
        public static class CountingOutputStream extends FilterOutputStream {
            private final TripleCollector<Integer, ?,  ?> integerCallback;
            private long transferred;
            private long fileLength;

            public CountingOutputStream(final OutputStream out, long fileLength,
                                        final TripleConsumer<Integer, ?, ?> callback) {
                super(out);
                this.fileLength = fileLength;
                this.integerCallback = callback;
                this.transferred = 0;
            }

            public void write(byte[] b, int off, int len) throws IOException {
                out.write(b, off, len);
                if (!Value.NULL(integerCallback)) {
                    this.transferred += len;
                    int prog = (int) (transferred * 100 / fileLength);
                    this.integerCallback.consume1(prog);
                }
            }

            public void write(int b) throws IOException {
                out.write(b);
                if (!Value.NULL(integerCallback)) {
                    this.transferred++;
                    int prog = (int) (transferred * 100 / fileLength);
                    this.integerCallback.consume1(prog);
                }
            }
        }
        */
    }

}
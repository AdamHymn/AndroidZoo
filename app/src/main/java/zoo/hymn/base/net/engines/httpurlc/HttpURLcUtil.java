package zoo.hymn.base.net.engines.httpurlc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import zoo.hymn.base.net.response.base.BaseResponse;

/**
 * ClassName: HttpURLcUtil
 * Function : HttpURLConnection方式的网络层
 * Author   : Hymn【向下扎根，向上结果】
 * Email    : ayang139@qq.com
 * Date     : 2017/10/11
 */

public class HttpURLcUtil {

    private static HttpURLcUtil httpURLcUtil;
    public static HttpURLcUtil getInstance(){
        if(httpURLcUtil == null){
            httpURLcUtil = new HttpURLcUtil();
        }
        return httpURLcUtil;
    }

    /**
     * HttpURLConnection方式的POST请求
     * @param path 请求路径
     * @param params 参数（按key=value&key=value方式拼接）
     * @return BaseResponse（返回code，message，content）
     */
    public BaseResponse doPost(String path, String params){
        BaseResponse baseResponse = null;
        try {
//        path = "http://www.caifangxin.com/financeTax/app/common/getAuthCode.do";
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            OutputStream os = httpURLConnection.getOutputStream();
//        os.write(new String("mobileNumber=18629388113").getBytes("UTF-8"));
            os.write(params.getBytes("UTF-8"));
            os.flush();
            os.close();

            baseResponse = new BaseResponse();
            baseResponse.responseCode = httpURLConnection.getResponseCode();
            baseResponse.responseMessage = httpURLConnection.getResponseMessage();
            System.err.println("ResponseCode: " + baseResponse.responseCode);
            System.err.println("ResponseMessage: " + baseResponse.responseMessage);

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] b = getBytesByInputStream(inputStream);
            baseResponse.responseContent =  getStringByBytes(b);
            System.err.println("responseContent: " + baseResponse.responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baseResponse;
    }

    /**
     * HttpURLConnection方式的GET请求
     * @param path 请求路径,若有参数（按key=value&key=value方式拼接）
     * @return BaseResponse（返回code，message，content）
     */
    public BaseResponse doGet(String path){
        BaseResponse baseResponse = null;
        try {
            URL url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            baseResponse = new BaseResponse();
            baseResponse.responseCode = httpURLConnection.getResponseCode();
            baseResponse.responseMessage = httpURLConnection.getResponseMessage();
            System.err.println("ResponseCode: " + baseResponse.responseCode);
            System.err.println("ResponseMessage: " + baseResponse.responseMessage);

            InputStream inputStream = httpURLConnection.getInputStream();
            byte[] b = getBytesByInputStream(inputStream);
            baseResponse.responseContent =  getStringByBytes(b);
            System.err.println("responseContent: " + baseResponse.responseContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baseResponse;
    }

    //根据字节数组构建UTF-8字符串
    private String getStringByBytes(byte[] bytes) {
        String str = "";
        try {
            str = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

    //从InputStream中读取数据，转换成byte数组，最后关闭InputStream
    private byte[] getBytesByInputStream(InputStream is) {
        byte[] bytes = null;
        BufferedInputStream bis = new BufferedInputStream(is);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BufferedOutputStream bos = new BufferedOutputStream(baos);
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        try {
            while ((length = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }
}

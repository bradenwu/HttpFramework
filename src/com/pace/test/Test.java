
package com.pace.test;

import android.content.Intent;

import com.pace.httpframework.apachehttp.ApacheHttpClient;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;

public class Test {
    public static void invoke() {
        HttpParams params = null;
        HttpPost post = null;
        new ApacheHttpClient().execute(params, post);

    }

    public static void main(String[] args)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        Generic generic = new Generic();
        // 调用泛型方法
        Intent obj = generic.getObject(0);
        // 判断obj的类型是否是指定的User类型
    }

    public static class Generic {
        /**
         * 泛型方法
         * 
         * @param <T> 声明一个泛型T
         * @param c 用来创建泛型对象
         * @return
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public <T> T getObject(int i) throws InstantiationException, IllegalAccessException {
            // 创建泛型对象
            // T t = c.newInstance();
            return null;
        }
    }
}

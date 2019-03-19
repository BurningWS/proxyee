package com.github.monkeywie.proxyee

import groovy.json.JsonSlurper
import io.netty.handler.codec.http.HttpHeaders
import org.apache.http.client.ResponseHandler
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients

class WXhref {

    static void ff(String xx, HttpHeaders headers) {

//实例化CloseableHttpClient对象
        CloseableHttpClient httpclient = HttpClients.custom().build()


        def file = new File(WXhref.class.getClassLoader().getResource("").path)
        def fs = file.parentFile.parentFile
        file = new File(fs.path + "/b.md")

        file.text = ""
        file << '''
日期 |名字  | 链接
--| --|---
'''
        Set m = new HashSet()
        (0..10).each {
            def href = xx

            href = href.replaceFirst(~"(?<=offset=)(.+?(?=&))+?", (it - 1) * 10 + "")
            println("offset:" + it)

            HttpGet httpget = new HttpGet(href)
//            httpget.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")

            headers.each {
                k->httpget.setHeader(k.key, k.value)
            }


            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = httpclient.execute(httpget, responseHandler);

            def jsonTool = new JsonSlurper()
            def j = jsonTool.parseText(responseBody)
            jsonTool.parseText(j.general_msg_list).list.each {

                def time = new Date(it."comm_msg_info"."datetime" * 1000l).format('yyyy-MM-dd')
                if ("app_msg_ext_info" in it) {

                    def info = it."app_msg_ext_info"
                    if (!m.add(info.title))
                        return

                    def ss = "${time} | [${info.title.replace("|", "\\|")}](${info.content_url})   |  \n"
                    file << ss
                    println(ss)
                    if ("multi_app_msg_item_list" in info) {
                        info.multi_app_msg_item_list.each {
                            ss = "${time} | [${it.title.replace("|", "\\|")}](${it.content_url})   | \n"
                            println(ss)
                            file << ss
                        }
                    }
                }
            }
        }
    }

}

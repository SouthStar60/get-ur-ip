package com.example.iptools

import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.NetworkInterface
import java.net.URL

/**
 * IP 地址获取工具。
 *
 * - 内网地址：遍历本机网卡 [NetworkInterface] 得到。
 * - 公网地址：访问仅解析 A / AAAA 记录的回显服务，
 *   从而分别强制走 IPv4 / IPv6 出口，拿到对应协议的公网地址。
 */
object IpUtils {

    /** 内网 IPv4：返回 "地址 (网卡名)" 列表，例如 192.168.1.10 (wlan0)。 */
    fun getLocalIpv4List(): List<String> {
        val result = mutableListOf<String>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return emptyList()
            for (intf in interfaces) {
                if (!intf.isUp || intf.isLoopback) continue
                for (addr in intf.inetAddresses) {
                    if (addr is Inet4Address && !addr.isLoopbackAddress) {
                        result.add("${addr.hostAddress}  (${intf.displayName})")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /** 内网全局 IPv6（排除回环、链路本地 fe80::、唯一本地 fc00::/7）。 */
    fun getLocalGlobalIpv6List(): List<String> {
        val result = mutableListOf<String>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return emptyList()
            for (intf in interfaces) {
                if (!intf.isUp || intf.isLoopback) continue
                for (addr in intf.inetAddresses) {
                    if (addr is Inet6Address
                        && !addr.isLoopbackAddress
                        && !addr.isLinkLocalAddress
                        && !addr.isSiteLocalAddress
                        && !addr.isAnyLocalAddress
                    ) {
                        result.add("${addr.hostAddress?.replace("%${intf.name}", "")}  (${intf.displayName})")
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    /** 获取公网 IPv4。以下域名均只有 A 记录，保证请求走 IPv4。 */
    fun getPublicIpv4(): String = fetchPublicIp(
        listOf(
            "https://ipv4.icanhazip.com",
            "https://v4.ident.me",
            "https://api.ipify.org"
        )
    )

    /** 获取公网 IPv6。以下域名均只有 AAAA 记录，保证请求走 IPv6。 */
    fun getPublicIpv6(): String = fetchPublicIp(
        listOf(
            "https://ipv6.icanhazip.com",
            "https://v6.ident.me",
            "https://api6.ipify.org"
        )
    )

    /** 依次尝试回显服务，返回去掉空白后的 IP 文本；全部失败则抛出最后一个异常。 */
    private fun fetchPublicIp(urls: List<String>): String {
        var lastError: Exception? = null
        for (urlText in urls) {
            var conn: HttpURLConnection? = null
            try {
                conn = (URL(urlText).openConnection() as HttpURLConnection).apply {
                    connectTimeout = 8000
                    readTimeout = 8000
                    requestMethod = "GET"
                    setRequestProperty("User-Agent", "IPTools/1.0")
                    instanceFollowRedirects = true
                }
                val code = conn.responseCode
                if (code in 200..299) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }.trim()
                    if (body.isNotEmpty()) return body
                }
            } catch (e: Exception) {
                lastError = e
            } finally {
                conn?.disconnect()
            }
        }
        throw lastError ?: RuntimeException("获取公网地址失败")
    }
}

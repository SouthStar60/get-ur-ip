package com.example.iptools

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.iptools.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRefreshAll.setOnClickListener { refreshAll() }

        binding.btnRefreshLocal.setOnClickListener { refreshLocal() }
        binding.btnRefreshV4.setOnClickListener { refreshPublicV4() }
        binding.btnRefreshV6.setOnClickListener { refreshPublicV6() }

        // 点击 IP 文本即可复制
        binding.tvLocalIp.setOnClickListener { copyToClipboard(binding.tvLocalIp.text) }
        binding.tvPublicV4.setOnClickListener { copyToClipboard(binding.tvPublicV4.text) }
        binding.tvPublicV6.setOnClickListener { copyToClipboard(binding.tvPublicV6.text) }

        refreshAll()
    }

    private fun refreshAll() {
        refreshLocal()
        refreshPublicV4()
        refreshPublicV6()
    }

    private fun refreshLocal() {
        binding.tvLocalIp.setText(R.string.loading)
        lifecycleScope.launch {
            val text = withContext(Dispatchers.IO) {
                val v4 = IpUtils.getLocalIpv4List()
                val v6 = IpUtils.getLocalGlobalIpv6List()
                buildString {
                    if (v4.isEmpty() && v6.isEmpty()) {
                        append("未获取到内网地址\n请检查网络连接")
                    }
                    if (v4.isNotEmpty()) {
                        append("IPv4\n").append(v4.joinToString("\n"))
                    }
                    if (v6.isNotEmpty()) {
                        if (isNotEmpty()) append("\n\n")
                        append("IPv6\n").append(v6.joinToString("\n"))
                    }
                }
            }
            binding.tvLocalIp.text = text
        }
    }

    private fun refreshPublicV4() {
        binding.tvPublicV4.setText(R.string.loading)
        lifecycleScope.launch {
            binding.tvPublicV4.text = fetchWithError { IpUtils.getPublicIpv4() }
        }
    }

    private fun refreshPublicV6() {
        binding.tvPublicV6.setText(R.string.loading)
        lifecycleScope.launch {
            binding.tvPublicV6.text = fetchWithError { IpUtils.getPublicIpv6() }
        }
    }

    private suspend fun fetchWithError(block: () -> String): String =
        withContext(Dispatchers.IO) {
            try {
                block()
            } catch (e: Exception) {
                "获取失败\n当前网络可能不支持该协议"
            }
        }

    private fun copyToClipboard(text: CharSequence) {
        val ip = extractIp(text.toString())
        if (ip == null) {
            Toast.makeText(this, "暂无可复制的地址", Toast.LENGTH_SHORT).show()
            return
        }
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("IP", ip))
        Toast.makeText(this, "已复制：$ip", Toast.LENGTH_SHORT).show()
    }

    /** 从展示文本中提取第一个 IPv4 或 IPv6 地址，忽略标题、网卡名、提示语。 */
    private fun extractIp(text: String): String? {
        Regex("""\d{1,3}(?:\.\d{1,3}){3}""").find(text)?.let { return it.value }
        Regex("(?i)[0-9a-f]{0,4}(?::[0-9a-f]{0,4}){2,}").find(text)?.let { return it.value }
        return null
    }
}
